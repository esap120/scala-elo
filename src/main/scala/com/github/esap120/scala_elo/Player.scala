package com.github.esap120.scala_elo

import KFactor.KFactor

import scala.collection.mutable.ListBuffer

/**
  * Holds player information and is backed by an ELO rating engine to update the player's rating.
  * Also contains tournament and game history.
  *
  * Players can play games against other players, here is an example following
  * the example from wikipedia [[https://en.wikipedia.org/wiki/Elo_rating_system#Mathematical_details]]
  * {{{
  * val playerA = new Player(rating = 1613)
  * val opponent1 = new Player(rating = 1609)
  * val opponent2 = new Player(rating = 1477)
  * val opponent3 = new Player(rating = 1388)
  * val opponent4 = new Player(rating = 1586)
  * val opponent5 = new Player(rating = 1720)
  *
  * playerA loses opponent1
  * playerA draws opponent2
  * playerA wins opponent3
  * playerA wins opponent4
  * playerA loses opponent5
  *
  * playerA.updateRating(KFactor.USCF) // If not specified the Simple K-Factor is used
  * playerA.rating // playerA now has a rating of 1601
  * }}}
  *
  * The class can be extended as shown:
  * {{{
  *
  * // An example class that requires a name, rating, and player's always start at 0 games.
  * class NamedPlayer(val name: String, rating: Int) extends Player(rating, 0) {
  *
  *   // Some possible methods that can be added.
  *   def isPro: Boolean = if (rating > 2400) true else false
  *   def isNew: Boolean = if (gamesPlayed < 30) true else false
  *
  *   // Change the default KFactor to USCF.
  *   def updateRating(): Unit = super.updateRating(KFactor.USCF)
  *
  *   // Or create a custom KFactor
  *   def updateRating(): Unit = {
  *     val customerFactor = if (isPro) 10 else 400 / gamesPlayed
  *     super.updateRating(customerFactor)
  *   }
  * }
  *
  * val myPlayer = new NamedPlayer("My Player Name", 1613)
  * }}}
  *
  *
  * @param rating Player's starting rating, defaulted to 1400
  * @param startingGameCount How many games the player has already played, defaulted to 0
  */
class Player(var rating: Int = 1400, val startingGameCount: Int = 0) {

  // Tracks games in active tournament
  private var _tournamentHistory = ListBuffer.empty[Game]

  /**
    * @return Active tournament history
    */
  def tournamentHistory: List[Game] = _tournamentHistory.toList

  // Tracks all previously played games
  private var _gameHistory = ListBuffer.empty[Game]

  /**
    * @return Games history
    */
  def gameHistory: List[Game] = _gameHistory.toList

  /**
    * @return Total games played
    */
  def gamesPlayed: Int = gameHistory.size + startingGameCount

  /**
    * @param opponent Player to check against
    * @return Returns true if the player has played the opponent
    */
  def hasAlreadyPlayed(opponent: Player): Boolean = {
    gameHistory.map(_.opponent).contains(opponent)
  }

  /**
    * Adds win to the player history and loss to opponent's
    * @param opponent Opposing player
    * @example
    * {{{
    *   players wins opponent
    * }}}
    */
  def wins(opponent: Player): Unit = {

    // Add win to player's history
    val game = new Game(this, opponent, GameResult.WIN)
    addGame(game)

    // Add loss to opponent's history
    opponent.addGame(game.inverse())
  }

  /**
    * Adds loss to the player history and win to opponent's
    * @param opponent Opposing player
    * @example
    * {{{
    *   players loses opponent
    * }}}
    */
  def loses(opponent: Player): Unit = {

    // Add loss to player's history
    val game = new Game(this, opponent, GameResult.LOSS)
    addGame(game)

    // Add win to opponent's history
    opponent.addGame(game.inverse())
  }

  /**
    * Adds draw to the player history and draw to opponent's
    * @param opponent Opposing player
    * @example
    * {{{
    *   players draws opponent
    * }}}
    */
  def draws(opponent: Player): Unit = {
    val game = new Game(this, opponent, GameResult.DRAW)
    addGame(game)
    opponent.addGame(game)
  }

  /**
    * @param opponent Opposing player
    * @return Returns a matchup between player and opponent
    * @example
    * {{{
    *   val matchup = players plays opponent
    * }}}
    */
  def plays(opponent: Player): Matchup = {
    new Matchup(this, opponent)
  }

  /**
    * Updates player's rating and clears tournament history
    * @param kFactorType What K-Factor formula to use when calculating the rating
    */
  def updateRating(kFactorType: KFactor = KFactor.SIMPLE): Unit = {
    this.rating = EloEngine.calculateRating(this, kFactorType)
    _tournamentHistory.clear()
  }

  /**
    * Updates the player's rating and clears tournament history
    * @param kFactor Custom K-Factor value or function
    */
  def updateRating(kFactor: Double): Unit = {
    this.rating = EloEngine.calculateRating(this, kFactor)
    _tournamentHistory.clear()
  }

  // Add a game to the player's history
  private def addGame(game: Game): Unit = {
    _tournamentHistory += game
    _gameHistory += game
  }
}
