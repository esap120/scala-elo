# Scala-Elo

[![Travis Badge](https://travis-ci.org/esap120/scala-elo.svg?branch=master)](https://travis-ci.org/esap120/scala-elo)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.esap120/scala-elo_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.esap120/scala-elo_2.12)

Scala-Elo is an implementation of the ELO Rating System written Scala. Useful for ranking, matchmaking, seeding tournaments, etc.

### Documentation
API Documenation can be found: [here](https://esap120.github.io/scala-elo/ "here")

### Overview
Description from [Wikipedia](https://en.wikipedia.org/wiki/Elo_rating_system "Wikipedia"):

> The Elo rating system is a method for calculating the relative skill levels of players in zero-sum games such as chess.

>The Elo system was originally invented as an improved chess rating system, but is also used as a rating system for multiplayer competition in a number of video games, association football, American football, basketball, Major League Baseball, Scrabble, board games such as Diplomacy and other games.

>The difference in the ratings between two players serves as a predictor of the outcome of a match. Two players with equal ratings who play against each other are expected to score an equal number of wins. A player whose rating is 100 points greater than their opponent's is expected to score 64%; if the difference is 200 points, then the expected score for the stronger player is 76%.

### Installation
To install copy the line below to your `build.sbt`:
```scala
libraryDependencies += "com.github.esap120" %% "scala-elo" % "1.0.1"
```

### Usage

##### Playing games
The player object contains all relvant information on the player such as rating and game history.

Players can play games against other players, here is an example following the example from wikipedia https://en.wikipedia.org/wiki/Elo_rating_system#Mathematical_details
```scala
val playerA = new Player(rating = 1613)
val opponent1 = new Player(rating = 1609)
val opponent2 = new Player(rating = 1477)
val opponent3 = new Player(rating = 1388)
val opponent4 = new Player(rating = 1586)
val opponent5 = new Player(rating = 1720)

playerA loses opponent1
playerA draws opponent2
playerA wins opponent3
playerA wins opponent4
playerA loses opponent5

playerA.updateRating(KFactor.USCF) // If not specified the Simple K-Factor is used
playerA.rating // playerA now has a rating of 1601
```
Players can also play in matchups, this is useful if you wish to pair up players before determining the winner.
```scala
val playerA = new Player(rating = 1613)
val opponent1 = new Player(rating = 1609)
val opponent2 = new Player(rating = 1477)
val opponent3 = new Player(rating = 1388)
val opponent4 = new Player(rating = 1586)
val opponent5 = new Player(rating = 1720)

val matchOne = playerA plays opponent1
val matchTwo = playerA plays opponent2
val matchThree = playerA plays opponent3
val matchFour = playerA plays opponent4
val matchFive = playerA plays opponent5

matchOne winner opponent1
matchTwo.draw()
matchThree loser opponent3
matchFour.winner(playerA)
matchFive.loser(playerA)

playerA.updateRating(KFactor.USCF) // If not specified the Simple K-Factor is used
playerA.rating // playerA now has a rating of 1601
```
The player object can also be easily extended to include any additional fields or methods you desire:
```scala
// An example class that requires a name, rating, and player's always start at 0 games.
class NamedPlayer(val name: String, rating: Int) extends Player(rating, 0) {

  // Some possible methods that can be added.
  def isPro: Boolean = if (rating > 2400) true else false
  def isNew: Boolean = if (gamesPlayed < 30) true else false

  // Change the default KFactor to USCF.
  def updateRating(): Unit = super.updateRating(KFactor.USCF)

  // Or create a custom KFactor
  def updateRating(): Unit = {
    val customerFactor = if (isPro) 10 else 400 / gamesPlayed
    super.updateRating(customerFactor)
  }
}

val myPlayer = new NamedPlayer("My Player Name", 1613)
```

##### K-Rating
The K-Rating determines how much a win or loss will affect a player's rating. The are 3 built-in options that can be used:
- SIMPLE (approximation of USCF K-Factor)
- USCF
- FIDE

[Read here for me information](https://en.wikipedia.org/wiki/Elo_rating_system#Most_accurate_K-factor "Read here for me information")


## Development
Feel free to create a pull request, all request must pass all unit tests and be approved before merging
## License
Code is provided under the MIT license available at http://opensource.org/licenses/Apache-2.0,
as well as in the LICENSE file.

[g8]: http://www.foundweekends.org/giter8/
