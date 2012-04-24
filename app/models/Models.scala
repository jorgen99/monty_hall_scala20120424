package models

import java.util.Random

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import models.Game._
import scala.Some

case class Game(id: Pk[Long] = NotAssigned,
                playerName: String,
                var initialPlayerDoor: Option[Int] = None,
                var carDoor: Option[Int],
                var switched: Option[Boolean] = None,
                var won: Option[Boolean] = None,
                var gameOver: Option[Boolean] = None) {

  def randomizeCarDoor {
    carDoor = Some(Game.randomDoor)
  }

    def goatDoor(): Int = {
      val candidateDoor = randomDoor
      if(isCarDoorOrPlayerDoor(candidateDoor)) {
        return goatDoor()
      }
      candidateDoor
    }


  def isCarDoorOrPlayerDoor(possibleGoat: Int): Boolean = {
    possibleGoat == carDoor.getOrElse(-1) || possibleGoat == initialPlayerDoor.getOrElse(-1)
  }

  def stayOrSwitch(doorNo: Int) {
       if(doorNo != initialPlayerDoor.getOrElse(-1)) {
         play.Logger.info("switched = true ")
         switched = Some(true)
       }
       if(doorNo == carDoor.getOrElse(-1)) {
         play.Logger.info("won = true ")
         won = Some(true)
       }
       gameOver = Some(true)
   }


}

object Game {

  /**
   * Parse a Game from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("game.id") ~
    get[String]("game.playerName") ~
    get[Option[Int]]("game.initialPlayerDoor") ~
    get[Option[Int]]("game.carDoor") ~
    get[Option[Boolean]]("game.switched") ~
    get[Option[Boolean]]("game.won") ~
    get[Option[Boolean]]("game.gameOver") map {
      case id~playerName~initialPlayerDoor~carDoor~switched~won~gameOver => Game(id, playerName, initialPlayerDoor, carDoor, switched, won, gameOver)
    }
  }

  def findById(id: Long): Option[Game] = {
    val game = DB.withConnection { implicit connection =>
      SQL(
      "select * from game where id = {id}"
      ).on(
        'id -> id
        ).as(
        Game.simple.singleOpt
      )
    }

    play.Logger.info("find game.carDoor = '" + game.get.carDoor.toString + "'")
    game
  }

  def insert(game: Game) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into game (id, playerName, initialPlayerDoor, carDoor,
                            switched, won, gameOver)
          values (
            (select next value for game_seq),
            {playerName}, {initialPlayerDoor}, {carDoor}, {switched}, {won}, {gameOver}
          )
        """
      ).on(
        'playerName -> game.playerName,
        'initialPlayerDoor -> game.initialPlayerDoor,
        'carDoor -> game.carDoor,
        'switched -> game.switched,
        'won -> game.won,
        'gameOver -> game.gameOver
      ).executeInsert()
    }
  }

  def update(id: Long, game: Game) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update game
          set
            playerName = {playerName},
            initialPlayerDoor = {initialPlayerDoor},
            carDoor = {carDoor},
            switched = {switched},
            won = {won},
            gameOver = {gameOver}
          where id = {id}
        """
      ).on(
        'id -> id,
        'playerName -> game.playerName,
        'initialPlayerDoor -> game.initialPlayerDoor,
        'carDoor -> game.carDoor,
        'switched -> game.switched,
        'won -> game.won,
        'gameOver -> game.gameOver
      ).executeUpdate()
    }

  }

  def randomDoor = new Random().nextInt(3) + 1
}
