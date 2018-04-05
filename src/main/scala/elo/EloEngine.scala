package elo

import elo.KFactor.KFactor


/**
  * Calculates the player's rating based on their actual performance against their expected performance
  * Formula is: R_new = R_old + K(Score_actual - Score_expected) where R_new is there new rating,
  * R_old is their old rating, K is the K-factor coefficient, Score_actual is their score
  * from their games and Score_expected is the score that was expected.
  *
  * For more details @see [[https://en.wikipedia.org/wiki/Elo_rating_system]]
  */
object EloEngine {

  /**
    * Calculates rating from a player object using KFactor presets
    */
  def calculateRating(player: Player, kFactorType: KFactor = KFactor.SIMPLE): Int = {
    val kFactorResult = calculateKFactor(player.rating, player.gamesPlayed, kFactorType)
    calculateRating(player.rating, player.tournamentHistory, player.gamesPlayed, kFactorResult)
  }

  /**
    * Calculates rating from a player object and allows for custom K-Factor
    */
  def calculateRating(player: Player, kFactor: Double): Int = {
    calculateRating(player.rating, player.tournamentHistory, player.gamesPlayed, kFactor)
  }

  /**
    * Calculates rating given the player's rating, played games, and total games played
    */
  def calculateRating(playerRating: Int, playedGames: List[Game],
                      totalGamesPlayed: Int, kFactor: Double): Int = {
    if (playedGames.isEmpty) {
      return playerRating
    }

    // Get the score results from the player's game history
    val actualScoreSum = playedGames.map(game => game.score).foldLeft(0.0)(_ + _)

    // Calculate the expected score based on the game history and the rating of each player during that game.
    val expectedScoreSum = playedGames
      .map(game => calculateExpectedScore(game.playerRating, game.opponentRating))
      .foldLeft(0.0)(_ + _)

    // Calculate a new ELO rating for the player based on
    // their actual performance against their expected performance.
    calculateRating(playerRating, actualScoreSum, expectedScoreSum, kFactor)
  }

  /**
    * @param currentRating Current rating of player
    * @param totalGamesPlayed Total games player has played
    * @param actualScore Score of the games played (0 for Loss, 1 for win, 0.5 for a Draw)
    * @param expectedScore Expected score of played games @see [[EloEngine.calculateExpectedScore]]
    * @param kFactor K-Factor preset
    * @return Returns rating of player
    */
  def calculateRating(currentRating: Int, totalGamesPlayed: Int,
                      actualScore: Double, expectedScore: Double,
                      kFactor: KFactor): Int = {
    val kFactorResult = calculateKFactor(currentRating, totalGamesPlayed, kFactor)
    calculateRating(currentRating, actualScore, expectedScore, kFactorResult)
  }

  /**
    *
    * @param currentRating Current rating of player
    * @param actualScore Score of the games played (0 for Loss, 1 for win, 0.5 for a Draw)
    * @param expectedScore Expected score of played games @see [[EloEngine.calculateExpectedScore]]
    * @param kFactor K-Factor value
    * @return Returns rating of player
    */
  def calculateRating(currentRating: Int, actualScore: Double,
                      expectedScore: Double, kFactor: Double): Int = {
    Math.round(currentRating + kFactor * (actualScore - expectedScore)).toInt
  }

  /**
    * Calculates expected score using logistic curve.
    */
  def calculateExpectedScore(playerRating: Int, opponentRating: Int): Double = {
    1.0 / (1.0 + Math.pow(10.0, (opponentRating - playerRating) / 400.0))
  }

  /**
    * Calculates the K-Factor to control sensitivity of rating changes from possible presets.
    * New players will have greater fluctuations in their initial rating
    * than players with more games.
    */
  def calculateKFactor(currentRating: Int, totalGamesPlayed: Int,
                                    kFactor: KFactor = KFactor.SIMPLE): Double = {
    require(totalGamesPlayed > 0, "A player cannot have negative or zero games played")

    kFactor match {
      // Simple approach
      case KFactor.SIMPLE =>
        val kFactor = 800.0 / totalGamesPlayed
        if (kFactor < 10) 10 else kFactor
      // USCF approach
      case KFactor.USCF =>
        if (currentRating < 2100) {
          32
        } else if (currentRating >= 2100 && currentRating < 2400) {
          24
        } else {
          16
        }
      // FIDE approach
      case KFactor.FIDE =>
        if (totalGamesPlayed < 30) {
          40
        }
        else if (currentRating < 2400) {
          20
        } else {
          10
        }
    }
  }
}
