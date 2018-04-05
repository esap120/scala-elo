name := "scala-elo"
organization := "com.github.esap120"

scalaVersion := "2.12.4"

crossScalaVersions := Seq("2.10.6","2.11.11", "2.12.4")

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

import ReleaseTransformations._
releaseCrossBuild := true // true if you cross-build the project for multiple Scala versions
releasePublishArtifactsAction := PgpKeys.publishSigned.value
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  // For non cross-build projects, use releaseStepCommand("publishSigned")
  releaseStepCommandAndRemaining("+publishSigned"),
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
)
