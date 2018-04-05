package com.github.esap120.scala_elo

import GameResult.GameResult

/**
  * Tracks the result of a game between two players, maintaining the players, game result,
  * and the player's ratings during that particular game
  *
  * @param player Player object
  * @param opponent Opponent object
  * @param result Result of the game @see [[GameResult]]
  */
class Game(val player: Player, val opponent: Player, val result: GameResult) {

  /**
    * Player's rating during this game
    */
  val playerRating: Int = player.rating

  /**
    * Opponent's rating during this game
    */
  val opponentRating: Int = opponent.rating

  // Generates game from the opposing perspective
  private[scala_elo] def inverse(): Game = {
    new Game(opponent, player,
      if (result == GameResult.WIN)
        GameResult.LOSS
      else if (result == GameResult.LOSS)
        GameResult.WIN
      else
        GameResult.DRAW
    )
  }

  // Use to correctly calculate ELO score, wins are worth 1, draws 0.5 and losses 0
  private[scala_elo] def score: Double = {
    val score = result match {
      case GameResult.DRAW => 0.5
      case GameResult.WIN =>  1
      case GameResult.LOSS =>  0
    }
    score
  }
}
