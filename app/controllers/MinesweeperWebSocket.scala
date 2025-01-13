package controllers

import de.htwg.se.minesweeper.observer.Observer
import de.htwg.se.minesweeper.controller.Event
import de.htwg.se.minesweeper.controller.baseController.{
  BaseController => MinesweeperController
}
import org.apache.pekko.actor.ActorRef
import org.apache.pekko.actor.Props
import org.apache.pekko.actor.Actor
import de.htwg.se.minesweeper.controller.Event
import de.htwg.se.minesweeper.controller.LostEvent
import de.htwg.se.minesweeper.controller.WonEvent
import de.htwg.se.minesweeper.controller.SetupEvent
import de.htwg.se.minesweeper.controller.StartGameEvent
import de.htwg.se.minesweeper.controller.FieldUpdatedEvent
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.Reads._
import play.api.libs.json.JsPath
import play.api.libs.functional.syntax._
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsObject
import play.api.libs.json.JsBoolean
import play.api.libs.json.JsError
import play.api.libs.json.Json
import play.api.libs.json.JsNumber
import de.htwg.se.minesweeper.observer.Observable
import de.htwg.se.minesweeper.model.GameState
import play.api.libs.json.JsString

case class XY(val x: Int, val y: Int)

class MinesweeperWebSocketActor(
    val out: ActorRef,
    val controller: MinesweeperController,
    val gameObserver: GameObserver
) extends Actor
    with Observer[Event] {

  controller.addObserver(this)

  implicit val xyReads: Reads[XY] = (
    (JsPath \ "x").read[Int] and
      (JsPath \ "y").read[Int]
  )(XY.apply)

  override def receive = {
    case msg: JsValue => {
      (msg \ "type").get.as[String] match {
        case "open" => out ! gameStateJson
        case "undo" => undo()
        case "redo" => redo()
        case "flag" =>
          (msg \ "data").validate[XY] match {
            case JsSuccess(xy, _) => flag(xy)
            case e: JsError       => println(e)
          }
        case "reveal" =>
          (msg \ "data").validate[XY] match {
            case JsSuccess(xy, _) => reveal(xy)
            case e: JsError       => println(e)
          }
        case "restart" => {
          controller.setup()
        }
        case "retry" => {
          val gameState = controller.getGameState
          controller.setup()
          controller.startGame(
            gameState.width,
            gameState.height,
            gameState.bombChance,
            gameState.maxUndos
          )
        }
      }
    }
  }

  private def undo() = controller.undo()
  private def redo() = controller.redo()

  private def reveal(xy: XY) = controller.reveal(xy.x, xy.y)

  private def flag(xy: XY) = controller.flag(xy.x, xy.y)

  override def postStop() = {
    controller.removeObserver(this)
    println("websocket closed")
  }

  override def update(e: Event): Unit = {
    e match {
      case SetupEvent() =>
        out ! Json.obj("reload" -> JsBoolean(true))
      case WonEvent() =>
        out ! Json.obj("end" -> "won")
      case LostEvent() =>
        out ! Json.obj("end" -> "lost")
      case StartGameEvent(_) | FieldUpdatedEvent(_) =>
        out ! gameStateJson
      case _ => {}
    }
  }

  private def gameStateJson = gameStateWrites
    .writes(controller.getGameState)
    .as[JsObject] + ("timer" -> JsNumber(gameObserver.getTime))
}

object MinesweeperWebSocketActorFactory {
  def create(
      out: ActorRef,
      controller: MinesweeperController,
      gameObserver: GameObserver
  ) = {
    Props(new MinesweeperWebSocketActor(out, controller, gameObserver))
  }
}

class MultiplayerWebsocketActor(
    val out: ActorRef,
    val username: String,
    val player: Player
) extends Actor {

  player.setWs(this)

  var receiveController: MinesweeperController = null

  implicit val xyReads: Reads[XY] = (
    (JsPath \ "x").read[Int] and
      (JsPath \ "y").read[Int]
  )(XY.apply)

  override def receive = {
    case msg: JsValue => {
      (msg \ "type").get.as[String] match {
        case "undo" => undo()
        case "redo" => redo()
        case "flag" =>
          (msg \ "data").validate[XY] match {
            case JsSuccess(xy, _) => flag(xy)
            case e: JsError       => println(e)
          }
        case "reveal" =>
          (msg \ "data").validate[XY] match {
            case JsSuccess(xy, _) => reveal(xy)
            case e: JsError       => println(e)
          }
        case "restart" => this.player.restart()
        case "retry"   => {}
      }
    }
  }

  private def undo() = receiveController.undo()
  private def redo() = receiveController.redo()

  private def reveal(xy: XY) = receiveController.reveal(xy.x, xy.y)

  private def flag(xy: XY) = receiveController.flag(xy.x, xy.y)

  override def postStop() = {
    player.websocketDisconnected()
    println("websocket closed")
  }

  def update(
      ev: (String, Event, MinesweeperController, Long, Boolean)
  ): Unit = {
    val (user, e, controller, time, restart) = ev

    if restart then {
      out ! Json.obj("type" -> "restart");
      return
    }

    e match {
      case SetupEvent() => println("setup" + user)
      case LostEvent() =>
        out ! Json.obj(
          "type" -> "lost",
          "username" -> user
        )
      case WonEvent() =>
        out ! Json.obj(
          "type" -> "won",
          "username" -> user
        )
      case StartGameEvent(_) | FieldUpdatedEvent(_) =>
        out ! gameStateJson(user, controller, time)
      case _ => {}
    }
  }

  private def gameStateJson(
      user: String,
      controller: MinesweeperController,
      time: Long
  ) =
    gameStateWrites
      .writes(controller.getGameState)
      .as[JsObject] + ("timer" -> JsNumber(time)) + ("username" -> JsString(
      user
    )) + ("type" -> JsString("update"))
}

class Player(
    val username: String,
    val dcCallback: (Player) => Unit
) extends Observable[(String, Event, MinesweeperController, Long, Boolean)]
    with Observer[Event] {

  var ws: MultiplayerWebsocketActor = null
  def setWs(ws: MultiplayerWebsocketActor) = {
    this.ws = ws
  }
  def websocketDisconnected() = {
    if controller != null then {
      controller.removeObserver(this)
    }
    dcCallback(this)
  }
  def restart() = {
    this.notifyObservers((null, null, null, 0, true))
  }

  var controller: MinesweeperController = null
  def setController(controller: MinesweeperController) = {
    this.controller = controller
    ws.receiveController = controller
    controller.addObserver(this)
  }

  private var start_time = current_time_seconds
  private def current_time_seconds = System.currentTimeMillis() / 1000;
  def getTime = current_time_seconds - start_time

  override def update(e: Event): Unit = {
    e match {
      case StartGameEvent(_) => start_time = current_time_seconds
      case _                 => {}
    }
    this.notifyObservers((username, e, this.controller, getTime, false))
  }
}

class MultiplayerWebsocketDispatcher(
    val players: Map[String, Player],
    val startOpts: StartOpts
) extends Observable[(String, GameState)]
    with Observer[(String, Event, MinesweeperController, Long, Boolean)] {
  for (_, player) <- players do player.addObserver(this)
  for (_, player) <- players do
    player.ws.out ! Json.obj(
      "type" -> "setup",
      "players" -> players.keySet.toList
    )
  for (_, player) <- players do player.controller.startGame.tupled(startOpts)

  override def update(
      e: (String, Event, MinesweeperController, Long, Boolean)
  ): Unit =
    for (_, player) <- players do {
      player.ws.update(e)
    }
}
