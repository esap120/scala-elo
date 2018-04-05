package com.github.esap120.scala_elo

import org.scalatest.{FlatSpec, Matchers}

class PlayerSpec extends FlatSpec with Matchers {

  "A player" must "a rating" in {

    // New players should have a rating of 1400
    val john = new Player()
    assert(john.rating == 1400)

    // A rating should be able to be set in the constructor
    val joe = new Player(rating = 2200)
    assert(joe.rating == 2200)
  }

  it can "be constructed with a starting number of games already played" in {
    // This will be useful if we want to add in players who
    // already have established games/rating.
    val tony = new Player(rating = 1850, startingGameCount = 32)
    assert(tony.rating == 1850)
    assert(tony.gamesPlayed == 32)
  }

  it must "be able to have it's rating updated" in {
    // New players should have a rating of 1400
    val john = new Player()
    assert(john.rating == 1400)

    john.rating = 1550
    assert(john.rating == 1550)

    val joe = new Player(rating = 2200)
    assert(joe.rating == 2200)

    joe.rating = 2100
    assert(joe.rating == 2100)
  }

  it must "track the number of games played" in {
    // New player starting with 0 games
    val john = new Player()

    // Experienced player with 32 games
    val tony = new Player(rating = 1850, startingGameCount = 32)

    // They play a game and Tony wins
    john.loses(tony)

    // John should now have played 1 game and Tony 33 games
    assert(john.gamesPlayed == 1)
    assert(tony.gamesPlayed == 33)

    // Both players should have a game in their game history
    assert(john.gameHistory.lengthCompare(1) == 0)
    assert(tony.gameHistory.lengthCompare(1) == 0)

    // It should represent the same game from opposite perspectives
    val johnsGame = john.gameHistory.head
    val tonysGame = tony.gameHistory.head
    assert(johnsGame.player == tonysGame.opponent)
    assert(johnsGame.opponent == tonysGame.player)
    assert(johnsGame.result == GameResult.LOSS && tonysGame.result == GameResult.WIN)
  }

  it must "know which opponents they have previously played" in {
    val john = new Player()
    val tony = new Player()

    // They play a game and Tony wins
    tony.wins(john)

    // They play a game and Tony wins
    assert(john hasAlreadyPlayed tony)
    assert(tony hasAlreadyPlayed john)
  }

  // This is necessary since the game history is what is used to update the rating
  // and should not then be used in subsequent rating updates.
  it must "have their tournament history reset after getting a new rating" in {
    val john = new Player()
    val tony = new Player()

    // They play a game and Tony wins
    tony.wins(john)

    assert(john.tournamentHistory.lengthCompare(1) == 0)
    assert(tony.tournamentHistory.lengthCompare(1) == 0)

    john.updateRating()
    tony.updateRating()

    assert(john.tournamentHistory.isEmpty)
    assert(tony.tournamentHistory.isEmpty)
  }

  // This is necessary since the game history is what is used to update the rating
  // and should not then be used in subsequent rating updates.
  it should "be easily extendable" in {

    class NamedPlayer(val name: String, customStartingRating: Int = 1200, playedGames: Int = 0)
      extends Player(customStartingRating, playedGames)

    val john = new NamedPlayer("John")
    val tony = new NamedPlayer("Tony", 1750, 5)

    assert(john.name == "John")
    assert(john.rating == 1200)
    assert(john.gamesPlayed == 0)

    assert(tony.name == "Tony")
    assert(tony.rating == 1750)
    assert(tony.gamesPlayed == 5)

    // They play a game and Tony wins
    tony.wins(john)

    assert(john.tournamentHistory.lengthCompare(1) == 0)
    assert(tony.tournamentHistory.lengthCompare(1) == 0)

    john.updateRating()
    tony.updateRating()

    assert(john.rating == 1168)
    assert(tony.rating == 1755)

    assert(john.tournamentHistory.isEmpty)
    assert(tony.tournamentHistory.isEmpty)
  }
}
