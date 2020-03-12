// For using the plugins in their own build
unmanagedSourceDirectories in Compile ++= Seq(
  baseDirectory.value.getParentFile / "storyteller-plugin" / "src" / "main" / "scala",
  baseDirectory.value.getParentFile / "storyteller-generator" / "src" / "main" / "scala"
)

// Need to copy dependencies for `storyteller-generator`
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest"    % "3.1.0",
  "org.scalameta" %% "scalameta"    % "4.3.0",
  "org.tpolecat"  %% "atto-core"    % "0.7.0",
  "org.tpolecat"  %% "atto-refined" % "0.7.0",
  "dev.zio"       %% "zio"          % "1.0.0-RC18-2"
)
