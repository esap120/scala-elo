package com.github.esap120.scala_elo

/**
  * Allows for creating matches before knowing winner, useful for matchmaking a pool of players
  * or used to create a tournament of players
  *
  * Players can play matches against other players, here is an example following
  * the example from wikipedia [[https://en.wikipedia.org/wiki/Elo_rating_system#Mathematical_details]]
  * {{{
  * val playerA = new Player(rating = 1613)
  * val opponent1 = new Player(rating = 1609)
  * val opponent2 = new Player(rating = 1477)
  * val opponent3 = new Player(rating = 1388)
  * val opponent4 = new Player(rating = 1586)
  * val opponent5 = new Player(rating = 1720)
  *
  * val matchOne = playerA plays opponent1
  * val matchTwo = playerA plays opponent2
  * val matchThree = playerA plays opponent3
  * val matchFour = playerA plays opponent4
  * val matchFive = playerA plays opponent5
  *
  * matchOne winner opponent1
  * matchTwo.draw()
  * matchThree loser opponent3
  * matchFour.winner(playerA)
  * matchFive.loser(playerA)
  *
  * playerA.updateRating(KFactor.USCF) // If not specified the Simple K-Factor is used
  * playerA.rating // playerA now has a rating of 1601
  * }}}
  */
class Matchup(val player: Player, val opponent: Player) {

  private var gameOver: Boolean = false

  /**
    * @return Returns true if the match has already been completed
    */
  def isGameOver: Boolean = gameOver

  /**
    * Applies a winning game to the selected player and a losing game to the opponent
    * @param winningPlayer The player who has won the match
    */
  def winner(winningPlayer: Player): Unit = {
    require(!gameOver, "The match has already ended")

    winningPlayer match {
      case this.player =>
        player.wins(opponent)
      case this.opponent =>
        opponent.wins(player)
      case _ => throw new IllegalArgumentException("Must a be a player in the match")
    }
    gameOver = true
  }

  /**
    * Apples a losing game to the selected player and a winning game to the opponent
    * @param losingPlayer
    */
  def loser(losingPlayer: Player): Unit = {
    require(!gameOver, "The match has already ended")

    losingPlayer match {
      case this.player =>
        player.loses(opponent)
      case this.opponent =>
        opponent.loses(player)
      case _ => throw new IllegalArgumentException("Must a be a player in the match")
    }
    gameOver = true
  }

  /**
    * Applies a draw to both players in the matchup
    */
  def draw(): Unit = {
    require(!gameOver, "The match has already ended")
    player.draws(opponent)
  }
}
