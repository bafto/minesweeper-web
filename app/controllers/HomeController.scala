package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import de.htwg.se.minesweeper.controller.baseController.{
  BaseController => MinesweeperController
}
import de.htwg.se.minesweeper.model.fieldComponent.field.RandomFieldFactory
import scala.util.Random
import de.htwg.se.minesweeper.model.FileIOComponent.JSON.FileIO
import de.htwg.se.minesweeper.observer.Observer
import scala.util.Success
import scala.util.Failure
import de.htwg.se.minesweeper.controller.Event
import de.htwg.se.minesweeper.controller.LostEvent
import de.htwg.se.minesweeper.controller.WonEvent
import de.htwg.se.minesweeper.controller.SetupEvent
import de.htwg.se.minesweeper.controller.StartGameEvent
import de.htwg.se.minesweeper.model.Cell
import play.api.libs.json.JsObject
import play.api.libs.json.JsValue
import play.api.libs.json.Writes
import play.api.libs.json.Json
import play.api.libs.json.JsBoolean
import play.api.libs.streams.ActorFlow
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.actor.Props
import play.api.http.ContentTypes

type StartOpts = (Int, Int, Float, Int)

enum GameState:
  case NotStarted, Running, Won, Lost

class GameObserver extends Observer[Event] {

  private var state = GameState.NotStarted
  private var start_time = current_time_seconds
  override def update(e: Event): Unit = {
    e match {
      case WonEvent()   => state = GameState.Won
      case LostEvent()  => state = GameState.Lost
      case SetupEvent() => state = GameState.NotStarted
      case StartGameEvent(_) => {
        state = GameState.Running
        start_time = current_time_seconds
      }
      case _ => {}
    }
  }

  private def current_time_seconds = System.currentTimeMillis() / 1000;
  def getState = state
  def getTime = current_time_seconds - start_time
}

@Singleton
class HomeController @Inject() (val controllerComponents: ControllerComponents)(
    implicit val system: ActorSystem
) extends BaseController {

  def start_game(): Action[JsValue] = Action(parse.json) {
    (request: Request[JsValue]) =>
      {
        Redirect("/")
      }
  }

  def health() = Action {
    Ok(Json.obj("healthy" -> true)).as(ContentTypes.JSON)
  }

  def websocket() = WebSocket.accept[JsValue, JsValue] { request =>
    ActorFlow.actorRef { out =>
      println("received websocket connection")
      val gameObserver = GameObserver()
      val minesweeperController = {
        val controller =
          MinesweeperController(RandomFieldFactory(Random()), FileIO())
        controller.setup()
        controller.addObserver(gameObserver)
        controller
      }
      println("width");
      println(request.getQueryString("width").get.toInt);
      minesweeperController.startGame(
        request.getQueryString("width").get.toInt,
        request.getQueryString("height").get.toInt,
        request.getQueryString("bomb_chance").get.toFloat,
        request.getQueryString("max_undos").get.toInt
      )
      MinesweeperWebSocketActorFactory.create(
        out,
        minesweeperController,
        gameObserver
      )
    }
  }

  var users = Set[String]()
  var lobbies = Map[String, (StartOpts, List[Player])]()

  def getLobbies(): Action[AnyContent] = Action {
    val lobbiesJson = lobbies.map { case (lobbyName, (_, players)) =>
      Json.obj(
        "name" -> lobbyName,
        "players" -> players.map(_.username) // Extract the player IDs
      )
    }
    Ok(Json.toJson(lobbiesJson))
  }

  def select_multiplayer(): Action[JsValue] = Action(parse.json) {
    (request: Request[JsValue]) =>
      {
        val username = (request.body \ "username").as[String]
        if users.contains(username) then {
          Ok(Json.obj("error" -> "username already exists"))
        } else {
          val startOpts = (
            (request.body \ "width").as[Int],
            (request.body \ "height").as[Int],
            (request.body \ "bomb_chance").as[Float],
            (request.body \ "max_undos").as[Int]
          )

          users = users + username
          lobbies = lobbies + (username -> (startOpts, List[Player]()))
          Ok(Json.obj())
        }
      }
  }

  def multiplayer_websocket() =
    WebSocket.accept[JsValue, JsValue] { request =>
      val username = request.getQueryString("username") match {
        case Some(username) => username
        case None           => throw RuntimeException("no username given")
      }
      val lobby = request.getQueryString("lobby") match {
        case Some(username) => username
        case None           => throw RuntimeException("no username given")
      }

      if !users.contains(username) then users = users + username
      if !(lobbies isDefinedAt lobby) then
        throw RuntimeException("not a valid lobby")

      ActorFlow.actorRef { out =>

        for player <- lobbies(lobby)(1) do {
          player.ws.out ! Json.obj(
            "type" -> "status",
            "message" -> (username + " joined the lobby")
          )
        }

        val player = Player(
          username,
          (p: Player) => {
            users = users - username
            lobbies = lobbies.updated(
              lobby,
              (
                lobbies(lobby)(0),
                (lobbies(lobby)(1).filterNot(_ == p))
              )
            )
            if lobbies(lobby)(1).length == 0 then {
              lobbies = lobbies - lobby
            }
          }
        )

        lobbies = lobbies.updated(
          lobby,
          (
            lobbies(lobby)(0),
            (lobbies(lobby)(1)) ++ List[Player](player)
          )
        )

        Props(
          MultiplayerWebsocketActor(
            out,
            username,
            player
          )
        )
      }
    }

  def start_multiplayer(): Action[JsValue] = Action(parse.json) {
    (request: Request[JsValue]) =>
      {
        val lobby = (request.body \ "lobby").as[String]
        if !lobbies.contains(lobby) then {
          Ok(Json.obj("type" -> "error", "message" -> "no lobby open"))
        } else if lobbies(lobby)(1).length < 2 then {
          Ok(
            Json.obj(
              "type" -> "error",
              "message" -> "not enough players in lobby"
            )
          )
        } else {

          var playerMap = Map[String, Player]()

          for player <- lobbies(lobby)(1) do {
            val controller =
              MinesweeperController(RandomFieldFactory(Random()), FileIO())
            controller.setup()
            player.setController(controller)
            playerMap = playerMap + (player.username -> player)
          }

          MultiplayerWebsocketDispatcher(playerMap, lobbies(lobby)(0))

          Ok(Json.obj())
        }
      }
  }
}
