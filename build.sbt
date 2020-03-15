scalaVersion in ThisBuild := "2.12.10"
version in ThisBuild := "0.1.0-SNAPSHOT"
organization in ThisBuild := "com.lambdarat"

lazy val storyteller = (project in file("."))
  .dependsOn(`storyteller-generator`, `storyteller-plugin`, `storyteller-sample`)
  .aggregate(`storyteller-generator`, `storyteller-plugin`, `storyteller-sample`)

lazy val `storyteller-generator` = project
  .settings(
    libraryDependencies ++= Seq(scalatest % Test, scalameta, zio, cats) ++ atto,
    kindProjector
  )

lazy val `storyteller-plugin` = project
  .enablePlugins(SbtPlugin)
  .dependsOn(`storyteller-generator`)

lazy val `storyteller-sample` = project
  .enablePlugins(StorytellerPlugin)
  .settings(
    libraryDependencies ++= Seq(scalatest % Test, scalacheck % Test)
  )
  .settings(
    storytellerSrcGenDomainPackages := Seq(
      "com.lambdarat.storyteller.sample.domain"
    )
  )
