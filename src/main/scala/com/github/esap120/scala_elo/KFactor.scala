package com.github.esap120.scala_elo

/**
  * Enumeration for which KFactor Formula to use
  * @see [[https://en.wikipedia.org/wiki/Elo_rating_system#Most_accurate_K-factor]]
  */
object KFactor extends Enumeration {
  type KFactor = Value

  /**
    * Approximation of USCF rating, '''K-Factor = 800 / (N_e + m)'''
    *
    * Where N_e is effective number of games a player's rating is based on and m is current tournament games played
    */
  val SIMPLE = Value

  /**
    * FIDE K-Factor is described as follows:
    *  - item K = 40, for a player new to the rating list until the completion of events with a total of 30 games
    *  - item K = 20, for players with a rating always under 2400.
    *  - item K = 10, for players with any published rating of at least 2400 and at least 30 games played in previous events.
    */
  val FIDE = Value

  /**
    * USCF K-Factor is described as follow:
    *  - Players below 2100: K-factor of 32 used
    *  - Players between 2100 and 2400: K-factor of 24 used
    *  - Players above 2400: K-factor of 16 used.
    */
  val USCF = Value
}
