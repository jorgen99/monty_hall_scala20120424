package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Game(id: Pk[Long] = NotAssigned, playerName: String)

object Game {

  /**
   * Parse a Game from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("game.id") ~
    get[String]("game.playerName") map {
      case id~playerName => Game(id, playerName)
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
          insert into game values (
            (select next value for game_seq),
            {playerName}
          )
        """
      ).on(
        'playerName -> game.playerName
      ).executeInsert()
    }
  }

}
