name := "scala-elo"

version := "1.0"

scalaVersion := "2.12.4"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

// Publish Scaladoc to github pages
enablePlugins(GhpagesPlugin)
enablePlugins(SiteScaladocPlugin)
git.remoteRepo := "git@github-personal:esap120/scala-elo.git"

mappings in makeSite ++= Seq(
  file("LICENSE") -> "LICENSE"
)

// Publish with Sonatype
homepage := Some(url("https://github.com/esap120/scala-elo"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/esap120/scala-elo"),
    "scm:git@github.com:esap120/scala-elo.git"
  )
)
developers := List(Developer("esap120",
  "Evan Sapienza",
  "evan.sapienza@gmail.com",
  url("https://github.com/esap120")))
licenses += ("MIT License", url("https://opensource.org/licenses/mit-license.php"))
publishMavenStyle := true

// Add sonatype repository settings
publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)
