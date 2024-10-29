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
import de.htwg.se.minesweeper.model.{GameState => MinesweeperState}
import play.api.libs.json.JsValue
import play.api.libs.json.JsObject
import play.api.libs.json.Writes
import play.api.libs.json.Json
import de.htwg.se.minesweeper.model.fieldComponent.FieldInterface
import play.api.libs.json.JsBoolean

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

case class GameViewParams(
    val width: Int,
    val height: Int,
    val cells: Seq[(Int, Int, Cell)],
    val elapsed: Long,
    val undos: Int
);

@Singleton
class HomeController @Inject() (val controllerComponents: ControllerComponents)
    extends BaseController {

  val gameObserver = GameObserver()
  val minesweeperController = {
    val controller =
      MinesweeperController(RandomFieldFactory(Random()), FileIO())
    controller.setup()
    controller.addObserver(gameObserver)
    controller
  }

  def game_params = {
    val field = minesweeperController.getGameState.field
    GameViewParams(
      field.dimension(0),
      field.dimension(1),
      for {
        y <- 0 until field.dimension(1)
        x <- 0 until field.dimension(0)
      } yield (
        x,
        y,
        field.getCell(x, y) match {
          case Success(c) => c
          case Failure(e) => throw RuntimeException(e)
        }
      ),
      gameObserver.getTime,
      minesweeperController.getGameState.undos
    )
  }

  def minesweeper() = Action {
    gameObserver.getState match {
      case GameState.Won        => Ok(views.html.won(gameObserver.getTime))
      case GameState.Lost       => Ok(views.html.lost(gameObserver.getTime))
      case GameState.NotStarted => Ok(views.html.start())
      case GameState.Running    => Ok(views.html.game(game_params))
    }
  }

  def reveal(x: Int, y: Int) = Action {
    minesweeperController.reveal(x, y)
    Ok(gameStateWrites.writes(minesweeperController.getGameState))
  }

  def flag(x: Int, y: Int) = Action {
    minesweeperController.flag(x, y)
    Ok(gameStateWrites.writes(minesweeperController.getGameState))
  }

  def undo() = Action {
    minesweeperController.undo() // TODO: return error
    Ok(gameStateWrites.writes(minesweeperController.getGameState))
  }

  def redo() = Action {
    minesweeperController.redo() // TODO: return error
    Ok(gameStateWrites.writes(minesweeperController.getGameState))
  }

  def restart() = Action {
    minesweeperController.setup()
    Redirect("/")
  }

  def retry() = Action {
    val gameState = minesweeperController.getGameState
    minesweeperController.setup()
    minesweeperController.startGame(
      gameState.width,
      gameState.height,
      gameState.bombChance,
      gameState.maxUndos
    )
    Redirect("/")
  }

  def start_game(): Action[JsValue] = Action(parse.json) {
    (request: Request[JsValue]) =>
      {
        minesweeperController.startGame(
          (request.body \ "width").as[Int],
          (request.body \ "height").as[Int],
          (request.body \ "bomb_chance").as[Float],
          (request.body \ "max_undos").as[Int]
        )
        Redirect("/")
      }
  }

  def about() = Action {
    Ok(views.html.about())
  }

  implicit val cellWrites: Writes[Cell] = new Writes[Cell] {
    def writes(c: Cell) = Json.obj(
      "isRevealed" -> c.isRevealed,
      "isBomb" -> c.isBomb,
      "isFlagged" -> c.isFlagged,
      "nearbyBombs" -> c.nearbyBombs
    )
  }

  implicit val fieldWrites: Writes[FieldInterface] =
    new Writes[FieldInterface] {
      def writes(field: FieldInterface) = Json.toJson(
        for {
          y <- 0 until field.dimension(1)
          x <- 0 until field.dimension(0)
        } yield Json.obj(
          "x" -> x,
          "y" -> y,
          "cell" -> (field.getCell(x, y) match {
            case Success(c) => cellWrites.writes(c)
            case Failure(e) => throw RuntimeException(e)
          })
        )
      )
    }

  implicit val gameStateWrites: Writes[MinesweeperState] =
    new Writes[MinesweeperState] {
      def writes(state: MinesweeperState) = Json.obj(
        "undos" -> state.undos,
        "maxUndos" -> state.maxUndos,
        "cells" -> fieldWrites.writes(state.field),
        "bombChance" -> state.bombChance,
        "width" -> state.width,
        "height" -> state.height,
        "timer" -> gameObserver.getTime,
        "won" -> JsBoolean(gameObserver.getState == GameState.Won),
        "lost" -> JsBoolean(gameObserver.getState == GameState.Lost)
      )
    }
}
