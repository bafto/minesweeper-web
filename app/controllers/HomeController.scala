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

class WonLostObserver extends Observer[Event] {
  var won: Boolean = false
  var lost: Boolean = false
  override def update(e: Event): Unit = {
    e match {
      case WonEvent()   => won = true
      case LostEvent()  => lost = true
      case SetupEvent() => reset()
      case _            => {}
    }
  }

  def reset() = {
    won = false
    lost = false
  }
}

/** This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject() (val controllerComponents: ControllerComponents)
    extends BaseController {

  val wonLostObserver = WonLostObserver()
  val minesweeperController = {
    val controller =
      MinesweeperController(RandomFieldFactory(Random()), FileIO())
    controller.setup()
    controller.startGame(10, 10, 0.3, 3)
    controller.addObserver(wonLostObserver)
    controller
  }

  def fieldAsText() = minesweeperController.getGameState.field.toString()

  /** Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method will be
    * called when the application receives a `GET` request with a path of `/`.
    */
  // def index() = Action {
  //   Ok(views.html.index())
  // }

  def minesweeper() = Action {
    val field = minesweeperController.getGameState.field
    (wonLostObserver.won, wonLostObserver.lost) match {
      case (true, false) => Ok(views.html.won())
      case (false, true) => Ok(views.html.lost())
      case (_, _) =>
        Ok(
          views.html.index(
            for {
              x <- 0 until field.dimension(0)
              y <- 0 until field.dimension(1)
            } yield (
              x,
              y,
              field.getCell(x, y) match {
                case Success(c) => c.toString()
                case Failure(e) => e.toString()
              }
            )
          )
        )
    }
  }

  def reveal(x: Int, y: Int) = Action {
    minesweeperController.reveal(x, y)
    Ok("")
  }

  def retry() = Action {
    minesweeperController.setup()
    minesweeperController.startGame(10, 10, 0.3, 3)
    Ok("")
  }
}
