import sbt._
import sbt.plugins.JvmPlugin

object ProjectPlugin extends AutoPlugin {

  object autoImport {

    lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.1.0"
    lazy val scalameta = "org.scalameta" %% "scalameta" % "4.3.0"
    lazy val atto = Seq(
      "org.tpolecat" %% "atto-core"    % "0.7.0",
      "org.tpolecat" %% "atto-refined" % "0.7.0"
    )
    lazy val zio = "dev.zio" %% "zio" % "1.0.0-RC17"

  }

  override def requires: Plugins = JvmPlugin

  override def trigger: PluginTrigger = allRequirements

}