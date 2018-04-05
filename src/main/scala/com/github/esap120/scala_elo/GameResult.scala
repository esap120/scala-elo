package com.github.esap120.scala_elo

/**
  * Enumeration for game results
  */
object GameResult extends Enumeration {
  type GameResult = Value
  val DRAW, WIN, LOSS = Value
}
