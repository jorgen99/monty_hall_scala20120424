package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.libs.json.Json._

import anorm._

import views._
import models._

object Application extends Controller {

  val gameForm: Form[Game] = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "playerName" -> text
    )
    { (id, playerName) => Game(playerName = playerName) }
    { game => Some(game.id, game.playerName) }
  )

  def index = Action {
    Ok(html.index(gameForm))
  }

  def newGame = Action { implicit request =>
    gameForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.index(formWithErrors)),
      game => {
        Game.insert(game) match {
            case Some(id) => Redirect(routes.Application.game(id))
            case None => Redirect(routes.Application.index)
        }
      }
    )
  }

  def newGameFor(playerName: String) = Action {
    val game = Game(playerName = playerName)
    Game.insert(game) match {
      case Some(id) => Redirect(routes.Application.game(id))
      case None => Redirect(routes.Application.index)
    }
  }

  def game(id: Long) = Action {
    Game.findById(id).map { game =>
      Ok(html.game(game))
    }.getOrElse(NotFound)
  }

  def selectDoor(id: Long, doorNo: Int) = Action {
    Game.findById(id).map { game =>
      game.initialPlayerDoor = doorNo
      Game.update(id, game)
      Ok(toJson(Map("goatDoor" -> game.goatDoor)))
    }.getOrElse(NotFound)
  }

  def stayOrSwitch(id: Long, doorNo: Int) = Action {
    Game.findById(id).map { game =>
      game.stayOrSwitch(doorNo)
      Game.update(id, game)
      Ok(toJson(Map("carDoor" -> game.carDoor.toString)))
    }.getOrElse(NotFound)
  }

}
