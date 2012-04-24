package models;

import org.specs2.mutable._
import org.specs2.specification.Scope

class GameSpec extends Specification {

  trait RandomGame extends Scope {
    val game = Game(playerName = "Keyser Söze");
  }

  trait GameWithCarDoorSet extends Scope {
    val game = Game(playerName = "Keyser Söze", carDoor = 3);
  }


  "The Monty Hall Game" should {

    "set a car door on creation" in new RandomGame {
      game.carDoor must_!= null
      game.carDoor must beGreaterThan(0)
      game.carDoor must beLessThan(4)
    }

    "display a goat behind the door that the player choose" in new RandomGame {
      game.initialPlayerDoor = 1
      for(i <- 1 to 500) {
        game.goatDoor must_!=(1)
      }
    }

    "not open the door that hides the car when displaying the goat" in new RandomGame {
      game.initialPlayerDoor = 1
      for(i <- 1 to 500) {
        game.goatDoor must_!=(game.carDoor)
      }
    }

    "be won when the player chooses the door that hides the car" in new GameWithCarDoorSet {
      game.initialPlayerDoor = 3
      game.stayOrSwitch(3)
      game.won mustEqual true
    }

    "know that the player has switched doors" in new GameWithCarDoorSet {
      game.initialPlayerDoor = 1
      game.stayOrSwitch(2)
      game.switched mustEqual true
    }

    "know that the game is over after the player has stayed or switched"  in new GameWithCarDoorSet {
      game.initialPlayerDoor = 3
      game.stayOrSwitch(3)
      game.gameOver mustEqual true
    }

  }
}

