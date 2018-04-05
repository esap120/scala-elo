package com.github.esap120.scala_elo

import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}


class EloEngineSpec extends FlatSpec with Matchers {

  // Evaluate double values at 4 decimals of precision
  val epsilon = 1e-4f
  implicit val doubleEq: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(epsilon)

  "An ELO game engine" must "correctly calculate an expected score of a player" in {
    // Player score should be the inverse of: 1 + 10^((Opponent_rating - Player_rating) / 400)
    val playerExpectedScore = EloEngine.calculateExpectedScore(1500, 1600)

    // Opponent score should be (1 - player score) since probability should equal 1
    val opponentExpectedScore = EloEngine.calculateExpectedScore(1600, 1500)

    assert(playerExpectedScore === 0.3600)
    assert(opponentExpectedScore === 0.6400)
  }

  // Estimated USCF rating system K-Factor is: K = 800 / (N_e) where N_e is total games played.
  // Results in power function y = a * x^-1 with each game lowering K-factor
  // coefficient affect on rating.
  it must "properly calculate a players K-factor using the approximate USCF formula" in {
    assert(EloEngine.calculateKFactor(1400, 1) === 800.0)
    assert(EloEngine.calculateKFactor(1400, 7) === 114.2857)
    assert(EloEngine.calculateKFactor(1400, 12) === 66.6666)
    assert(EloEngine.calculateKFactor(1400, 23) === 34.7826)
    assert(EloEngine.calculateKFactor(1400, 60) === 13.3333)

    // Basic check to make sure we have a positive count of games played.
    assertThrows[IllegalArgumentException] { EloEngine.calculateKFactor(1400, 0) }
    assertThrows[IllegalArgumentException] { EloEngine.calculateKFactor(1400, -1) }

  }

  // USCF Formula is as follows:
  // If rating is < 2100 the KFactor is 32
  // If the rating is between 2100 and 2400 the KFactor is 24
  // If the rating is over 2400 the KFactor is 16
  it must "properly calculate a players K-factor using the USCF formula" in {
    assert(EloEngine.calculateKFactor(1400, 1, KFactor.USCF) == 32)
    assert(EloEngine.calculateKFactor(2200, 7, KFactor.USCF) == 24)
    assert(EloEngine.calculateKFactor(2500, 12, KFactor.USCF) == 16)
  }

  // FIDE Formula is as follows:
  // If the player has played less than 30 games the KFactor is 40
  // If the player has played over 30 games but has a rating < 2400 the KFactor is 20
  // If the player has played over 30 games and their rating is > 2400 the KFactor is 10
  it must "properly calculate a players K-factor using the FIDE formula" in {
    assert(EloEngine.calculateKFactor(1400, 1, KFactor.FIDE) == 40)
    assert(EloEngine.calculateKFactor(2200, 7, KFactor.FIDE) == 40)
    assert(EloEngine.calculateKFactor(2500, 12, KFactor.FIDE) == 40)
    assert(EloEngine.calculateKFactor(2000, 35, KFactor.FIDE) == 20)
    assert(EloEngine.calculateKFactor(2500, 32, KFactor.FIDE) == 10)
  }

  // With K-Factor, actual results, and expected results a player's updated rating
  // can be calculated as: R_new = R_old + K(Score_actual - Score_expected).
  it must "properly update a players rating based on their performance" in {

    // A new player playing only other new players would be expected to win half the time,
    // if this player won every game it would result in a 100% win rate, represented as 1.
    // Let's also assume they have played 4 games, giving them a K-Factor of 200.
    // This results in the following equation: R_new = 1400 + 200(1 - 0.5), which
    // gives a new rating of 1500.
    assert(EloEngine.calculateRating(1400, 4, 1, 0.5, KFactor.SIMPLE) == 1500)

    // An advanced player with say 50 games could have a score of 2200 assuming they
    // play a novice with a rating of 1300, an example expected score of 0.8, and they
    // draw the formula would be: R_new = 2200 + 16(0.5 - 0.8), which gives a
    // new rating of 2195
    assert(EloEngine.calculateRating(2200, 50, 0.5, 0.8, KFactor.SIMPLE) == 2195)

    // and if they won: 2200 + 16(1 - 0.8) = 2203
    assert(EloEngine.calculateRating(2200, 50, 1, 0.8, KFactor.SIMPLE) == 2203)

    // and if they lost: 2200 + 16(0 - 0.8) = 2187
    assert(EloEngine.calculateRating(2200, 50, 0, 0.8, KFactor.SIMPLE) == 2187)

    // Same scenario but from the novice's perspective would result in the following:
    // Assuming they have played 10 games, would result in a K-Factor of 80 so
    // the new formula for the novice would be R_new = 1300 + 80(0.5 - 0.2)
    assert(EloEngine.calculateRating(1300, 10, 0.5, 0.2, KFactor.SIMPLE) == 1324)

    // and if they lost: 1300 + 80(0 - 0.2) = 1284
    assert(EloEngine.calculateRating(1300, 10, 0, 0.2, KFactor.SIMPLE) == 1284)

    // and if they won 1300 + 80(1 - 0.2) = 1364:
    assert(EloEngine.calculateRating(1300, 10, 1, 0.2, KFactor.SIMPLE) == 1364)
  }

  // The rating should be correctly updated if done through player objects
  it must "correctly calculate a new ELO rating from a players game history" in {
    // Example from wikipedia:
    // https://en.wikipedia.org/wiki/Elo_rating_system#Mathematical_details
    val player1 = new Player(rating = 1613)
    val opponent1 = new Player(rating = 1609)
    val opponent2 = new Player(rating = 1477)
    val opponent3 = new Player(rating = 1388)
    val opponent4 = new Player(rating = 1586)
    val opponent5 = new Player(rating = 1720)

    player1.loses(opponent1)
    player1.draws(opponent2)
    player1.wins(opponent3)
    player1.wins(opponent4)
    player1.loses(opponent5)

    assert(EloEngine.calculateRating(player1) == 1554)

    // Should be the same result if ran with the player's update rating method.
    // Make sure that updating another player's rating first does not change the result
    opponent1.wins(opponent2)
    opponent1.updateRating()
    opponent2.draws(opponent3)

    player1.updateRating()
    assert(player1.rating == 1554)
    assert(EloEngine.calculateRating(player1) == player1.rating)
  }
}
