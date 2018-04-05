package com.github.esap120.scala_elo

import org.scalatest.{FlatSpec, Matchers}

class GameSpec extends FlatSpec with Matchers {

  "A game" should "contain information on the player, opponent, and score" in {
    val john = new Player()
    val joe = new Player()

    val gameWin = new Game(john, joe, GameResult.WIN)
    assert(gameWin.player == john)
    assert(gameWin.opponent == joe)
    assert(gameWin.result == GameResult.WIN)
  }

  it should "be able to generate an opposing viewpoint of the game" in {
    val john = new Player()
    val joe = new Player()

    // John winning should be the same thing as Joe losing
    val johnWins = new Game(john, joe, GameResult.WIN)
    val joeLoses = new Game(joe, john, GameResult.LOSS)

    // If John's winning game is inverse it should be the same as Joe losing
    val inverseJohnWins = johnWins.inverse()
    assert(inverseJohnWins.player == joe)
    assert(inverseJohnWins.opponent == john)
    assert(inverseJohnWins.result == GameResult.LOSS)

    // If Tony's losing game is inverse it should be the same as John winning
    val inverseJoeLoses = joeLoses.inverse()
    assert(inverseJoeLoses.player == john)
    assert(inverseJoeLoses.opponent == joe)
    assert(inverseJoeLoses.result == GameResult.WIN)
  }

}
