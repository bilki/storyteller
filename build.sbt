import Dependencies._

scalaVersion in ThisBuild   := "2.12.10"
version in ThisBuild        := "0.1.0-SNAPSHOT"
organization in ThisBuild   := "com.lambdarat"

lazy val storyteller = (project in file("."))
  .dependsOn(generator, plugin)
  .aggregate(generator, plugin)

lazy val generator = (project in file("storyteller-generator"))
  .settings(
    name := "storyteller-generator",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      scalameta,
      zio
    ) ++ atto
  )

lazy val plugin = (project in file("storyteller-plugin"))
  .enablePlugins(SbtPlugin)
  .dependsOn(generator)
  .settings(
    name := "storyteller-plugin"
  )
