name := "scala-elo"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

enablePlugins(GhpagesPlugin)
enablePlugins(SiteScaladocPlugin)
git.remoteRepo := "git@github-personal:esap120/scala-elo.git"

mappings in makeSite ++= Seq(
  file("LICENSE") -> "LICENSE"
)
