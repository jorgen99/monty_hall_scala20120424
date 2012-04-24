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

  val gameForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "playerName" -> text,
      "initialPlayerDoor" -> optional(of[Int]),
      "carDoor" -> optional(of[Int]),
      "switched" -> optional(of[Boolean]),
      "won" -> optional(of[Boolean]),
      "gameOver" -> optional(of[Boolean])
    )
    (Game.apply)(Game.unapply)
  )

  def index = Action {
    Ok(html.index(gameForm))
  }

  def newGame = Action { implicit request =>
    gameForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.index(formWithErrors)),
      game => {
        game.randomizeCarDoor
        Game.insert(game) match {
            case Some(id) => Redirect(routes.Application.game(id))
            case None => Redirect(routes.Application.index)
        }
      }
    )
  }

  def newGameFor(playerName: String) = Action {
    val game = Game(playerName = playerName, carDoor = Some(Game.randomDoor))
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
      game.initialPlayerDoor = Some(doorNo)
      Game.update(id, game)
      Ok(toJson(Map("goatDoor" -> game.goatDoor)))
    }.getOrElse(NotFound)
  }

  def stayOrSwitch(id: Long, doorNo: Int) = Action {
    Game.findById(id).map { game =>
      game.stayOrSwitch(doorNo)
      Game.update(id, game)
      Ok(toJson(Map("carDoor" -> game.carDoor.get.toString)))
    }.getOrElse(NotFound)
  }

//    public static Result stayOrSwitch(Long id, int doorNo) {
//        Game currentGame = Game.find.byId(id);
//        currentGame.stayOrSwitch(doorNo);
//        currentGame.save();
//        ObjectNode result = Json.newObject();
//        result.put("carDoor", currentGame.carDoor);
//        result.put("won", currentGame.won);
//        return ok(result);
//    }
//
//    private static Result redirectToGame(Game newGame) {
//        newGame.save();
//        flash("success", "Game " + newGame.playerName + " has been created");
//        return redirect(routes.Application.game(newGame.id));
//    }

}
