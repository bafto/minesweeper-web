package controllers

import play.api.libs.json.JsObject
import play.api.libs.json.JsValue
import play.api.libs.json.Writes
import play.api.libs.json.Json
import de.htwg.se.minesweeper.model.Cell
import de.htwg.se.minesweeper.model.{GameState => MinesweeperState}
import de.htwg.se.minesweeper.model.fieldComponent.FieldInterface
import scala.util.Success
import scala.util.Failure

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
      "height" -> state.height
    )
  }
