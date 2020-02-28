// For using the plugins in their own build
unmanagedSourceDirectories in Compile ++= Seq(
  baseDirectory.value.getParentFile / "storyteller-plugin" / "src" / "main" / "scala",
  baseDirectory.value.getParentFile / "storyteller-generator" / "src" / "main" / "scala"
)

// Need to copy dependencies for `storyteller-generator`
libraryDependencies += "org.scalatest" %% "scalatest"    % "3.1.0"
libraryDependencies += "org.scalameta" %% "scalameta"    % "4.3.0"
libraryDependencies += "org.tpolecat"  %% "atto-core"    % "0.7.0"
libraryDependencies += "org.tpolecat"  %% "atto-refined" % "0.7.0"
libraryDependencies += "dev.zio"       %% "zio"          % "1.0.0-RC17"
