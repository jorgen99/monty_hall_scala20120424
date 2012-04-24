import models.Game
import org.specs2.mutable._

class AcceptanceSpec extends Specification {

  "A simulation" should {
    "show that switching will win two times out of three" in {
      val tenThousandGames = for (i <- 1 to 10000) yield {
        val game = Game(playerName = "Keyser Söze")
        game.initialPlayerDoor = 1
        val goatDoor = game.goatDoor
        if (goatDoor == 2) {
          game.stayOrSwitch(3)
        }
        else {
          game.stayOrSwitch(2)
        }
        game.won
      }
      tenThousandGames.count(_ == true) must be closeTo (6667 +/- 200)

    }
  }

}