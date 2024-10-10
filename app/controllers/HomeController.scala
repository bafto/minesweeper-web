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

enum GameState:
  case NotStarted, Running, Won, Lost

class GameObserver extends Observer[Event] {

  private var state = GameState.NotStarted
  override def update(e: Event): Unit = {
    e match {
      case WonEvent()        => state = GameState.Won
      case LostEvent()       => state = GameState.Lost
      case SetupEvent()      => state = GameState.NotStarted
      case StartGameEvent(_) => state = GameState.Running
      case _                 => {}
    }
  }

  def getState = state
}

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

  def index_params = {
    val field = minesweeperController.getGameState.field
    for {
      x <- 0 until field.dimension(0)
      y <- 0 until field.dimension(1)
    } yield (
      x,
      y,
      field.getCell(x, y) match {
        case Success(c) => c
        case Failure(e) => throw RuntimeException(e)
      }
    )
  }

  def minesweeper() = Action {
    gameObserver.getState match {
      case GameState.Won        => Ok(views.html.won())
      case GameState.Lost       => Ok(views.html.lost())
      case GameState.NotStarted => Ok(views.html.start())
      case GameState.Running    => Ok(views.html.index(index_params))
    }
  }

  def reveal(x: Int, y: Int) = Action {
    minesweeperController.reveal(x, y)
    Ok("")
  }

  def retry() = Action {
    minesweeperController.setup()
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
