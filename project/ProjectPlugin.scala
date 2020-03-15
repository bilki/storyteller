import sbt._
import sbt.plugins.JvmPlugin

object ProjectPlugin extends AutoPlugin {

  object autoImport {

    lazy val scalatest  = "org.scalatest"  %% "scalatest"  % "3.1.0"
    lazy val scalacheck = "org.scalacheck" %% "scalacheck" % "1.14.1"
    lazy val scalameta  = "org.scalameta"  %% "scalameta"  % "4.3.0"
    lazy val atto = Seq(
      "org.tpolecat" %% "atto-core"    % "0.7.0",
      "org.tpolecat" %% "atto-refined" % "0.7.0"
    )
    lazy val cats = "org.typelevel" %% "cats-core" % "2.1.0"
    lazy val zio  = "dev.zio"       %% "zio"       % "1.0.0-RC18-2"

    lazy val kindProjector = addCompilerPlugin(
      "org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full
    )
  }

  override def requires: Plugins = JvmPlugin

  override def trigger: PluginTrigger = allRequirements

}
