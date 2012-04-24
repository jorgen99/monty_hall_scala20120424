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
                var initialPlayerDoor: Int = -1,
                carDoor: Int = randomDoor,
                var switched: Boolean = false,
                var won: Boolean = false,
                var gameOver: Boolean = false) {

    def goatDoor(): Int = {
      val candidateDoor = randomDoor
      if(isCarDoorOrPlayerDoor(candidateDoor)) {
        return goatDoor()
      }
      candidateDoor
    }

  def isCarDoorOrPlayerDoor(possibleGoat: Int) = {
    possibleGoat == carDoor || possibleGoat == initialPlayerDoor
  }

  def stayOrSwitch(doorNo: Int) {
       if(doorNo != initialPlayerDoor) {
         switched = true
       }
       if(doorNo == carDoor) {
         won = true
       }
       gameOver = true
   }

}

object Game {

  /**
   * Parse a Game from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("game.id") ~
    get[String]("game.playerName") ~
    get[Int]("game.initialPlayerDoor") ~
    get[Int]("game.carDoor") ~
    get[Boolean]("game.switched") ~
    get[Boolean]("game.won") ~
    get[Boolean]("game.gameOver") map {
      case id~playerName~initialPlayerDoor~carDoor~switched~won~gameOver => Game(id, playerName, initialPlayerDoor, carDoor, switched, won, gameOver)
    }
  }

  def findById(id: Long): Option[Game] = {
    DB.withConnection { implicit connection =>
      SQL(
      "select * from game where id = {id}"
      ).on(
        'id -> id
        ).as(
        Game.simple.singleOpt
      )
    }
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
