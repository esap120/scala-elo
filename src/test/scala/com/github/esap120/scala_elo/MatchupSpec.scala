package com.github.esap120.scala_elo

import org.scalatest.{FlatSpec, Matchers}

class MatchupSpec extends FlatSpec with Matchers {

  "A matchup" must "correctly update a players tournament history" in {
    val john = new Player()
    val tony = new Player(1740, 5)

    val matchupOne = john.plays(tony)
    matchupOne.winner(john)

    assert(john.tournamentHistory.size == 1)
    assert(john.gamesPlayed == 1)
    assert(tony.tournamentHistory.size == 1)
    assert(tony.gamesPlayed == 6)

    val matchupTwo = new Matchup(john, tony)
    matchupTwo.draw()
    assert(john.tournamentHistory.size == 2)
    assert(john.gamesPlayed == 2)
    assert(tony.tournamentHistory.size == 2)
    assert(tony.gamesPlayed == 7)

    john.updateRating()
    tony.updateRating()
    assert(john.tournamentHistory.isEmpty)
    assert(john.gamesPlayed == 2)
    assert(tony.tournamentHistory.isEmpty)
    assert(tony.gamesPlayed == 7)
  }

  it must "update player ratings the same as directly from the player class" in {

    val john = new Player()
    val johnCopy= new Player()

    val tony = new Player(1740, 5)
    val tonyCopy = new Player(1740, 5)

    john.loses(tony)

    val matchup = johnCopy.plays(tonyCopy)
    matchup.winner(tonyCopy)

    // The match should now be over
    assert(matchup.isGameOver)

    // Throw exception if attempt to change winner after game is over
    assertThrows[IllegalArgumentException](matchup.winner(johnCopy))

    // Update the original 'john' player
    john.updateRating()

    // At this point these two player objects should not have the same rating
    assert(john.rating != johnCopy.rating)

    // Update the 'copy' of john
    johnCopy.updateRating()

    // Now they should match
    assert(john.rating == johnCopy.rating)

    // Same logic applies for other player
    tony.updateRating()
    assert(tony.rating != tonyCopy.rating)
    tonyCopy.updateRating()
    assert(tony.rating == tonyCopy.rating)
  }

}
