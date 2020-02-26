lazy val sample = (project in file("."))
  .settings(StorytellerPlugin.defaultSettings)
  .settings(
    name := "sample",
    version := "1.0.0",
    sourceGenerators in Test += (storytellerSrcGen in Compile).taskValue,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0"
  )
