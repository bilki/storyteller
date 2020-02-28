scalaVersion in ThisBuild   := "2.12.10"
version in ThisBuild        := "0.1.0-SNAPSHOT"
organization in ThisBuild   := "com.lambdarat"

lazy val storyteller = (project in file("."))
  .dependsOn(`storyteller-generator`, `storyteller-plugin`)
  .aggregate(`storyteller-generator`, `storyteller-plugin`)

lazy val `storyteller-generator` = project
  .settings(libraryDependencies ++= Seq(scalaTest % Test, scalameta, zio) ++ atto)

lazy val `storyteller-plugin` = project
  .enablePlugins(SbtPlugin)
  .dependsOn(`storyteller-generator`)
