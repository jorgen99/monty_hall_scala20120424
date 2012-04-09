package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Game(id: Pk[Long] = NotAssigned, 
                playerName: String, 
                var selectedDoor: Option[Int] = None) {
    def goatDoor = 3
}

object Game {

  /**
   * Parse a Game from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("game.id") ~
    get[String]("game.playerName") ~
    get[Option[Int]]("game.selectedDoor") map {
      case id~playerName~selectedDoor => Game(id, playerName, selectedDoor)
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
          insert into game (id, playerName) values (
            (select next value for game_seq),
            {playerName}
          )
        """
      ).on(
        'playerName -> game.playerName
      ).executeInsert()
    }
  }

  def update(id: Long, game: Game) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update game
          set playerName = {playerName}, selectedDoor = {selectedDoor}
          where id = {id}
        """
      ).on(
        'id -> id,
        'playerName -> game.playerName,
        'selectedDoor -> game.selectedDoor
      ).executeUpdate()
    }

  }

}
