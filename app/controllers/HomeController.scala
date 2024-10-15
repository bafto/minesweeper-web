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
    val elapsed: Long
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
      gameObserver.getTime
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
    Ok("")
  }

  def flag(x: Int, y: Int) = Action {
    minesweeperController.flag(x, y)
    Ok("")
  }

  def undo() = Action {
    minesweeperController.undo() // TODO: return error
    Ok("")
  }

  def redo() = Action {
    minesweeperController.redo() // TODO: return error
    Ok("")
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

  def start_game(
      width: Int,
      height: Int,
      bomb_chance: Float,
      max_undos: Int
  ): Action[AnyContent] = Action {
    minesweeperController.startGame(width, height, bomb_chance, max_undos)
    Redirect("/")
  }

  def about() = Action {
    Ok(views.html.about())
  }
}
