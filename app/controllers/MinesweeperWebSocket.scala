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
      case WonEvent() | LostEvent() | SetupEvent() =>
        out ! Json.obj("reload" -> JsBoolean(true))
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
