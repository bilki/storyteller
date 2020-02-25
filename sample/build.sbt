lazy val sample = (project in file("."))
  .settings(StorytellerPlugin.defaultSettings)
  .settings(
    name := "sample",
    version := "1.0.0",
    sourceGenerators in Compile += (storytellerSrcGen in Compile).taskValue,
  )
