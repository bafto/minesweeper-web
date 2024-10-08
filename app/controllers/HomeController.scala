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

/** This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject() (val controllerComponents: ControllerComponents)
    extends BaseController {

  val minesweeperController = {
    val controller =
      MinesweeperController(RandomFieldFactory(Random()), FileIO())
    controller.setup()
    controller.startGame(10, 10, 0.3, 3)
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
    println("minesweeper called")
    Ok(views.html.index(fieldAsText()))
  }

  def reveal(x: Int, y: Int) = Action {
    minesweeperController.reveal(x, y)
    Ok("")
  }
}
