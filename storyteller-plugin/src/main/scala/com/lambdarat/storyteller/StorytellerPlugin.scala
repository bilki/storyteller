package com.lambdarat.storyteller

import com.lambdarat.storyteller.app.{Storyteller, StorytellerConfig}

import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

import java.io.File

object StorytellerPlugin extends AutoPlugin {

  override def requires: Plugins = JvmPlugin

  object autoImport {

    lazy val storytellerSrcGen: TaskKey[Seq[File]] =
      taskKey[Seq[File]]("Generates Scala files from Storyteller stories")

    lazy val storytellerSrcGenSourceDirs: SettingKey[Seq[File]] =
      settingKey[Seq[File]]("Directories where story files are located")

    lazy val storytellerSrcGenTargetDir: SettingKey[File] =
      settingKey[File](
        "Scala target directory, where the `srcGen` task will write the generated files"
      )

    lazy val storytellerSrcGenStoriesTargetDir: SettingKey[File] =
      settingKey[File]("Target directory where stories will be copied")

    lazy val storytellerSrcGenDomainPackages: SettingKey[Seq[String]] =
      settingKey[Seq[String]]("Packages from where domain types are imported")

    lazy val storytellerSrcGenBasePackage: SettingKey[String] =
      settingKey[String]("Base package for generated spec files")
  }

  import autoImport._

  lazy val defaultStoryExtension: String = ".story"

  lazy val defaultStoriesFolder: String = "stories"

  lazy val defaultSettings: Seq[Def.Setting[_]] = Seq(
    storytellerSrcGenSourceDirs := Seq((resourceDirectory in Test).value / defaultStoriesFolder),
    storytellerSrcGenTargetDir := (sourceManaged in Test).value,
    storytellerSrcGenStoriesTargetDir := (sourceManaged in Test).value / defaultStoriesFolder,
    storytellerSrcGenBasePackage := s"${organization.value}.storyteller"
  )

  lazy val taskSettings: Seq[Def.Setting[_]] = {
    Seq(
      storytellerSrcGen := Def
        .sequential(
          Def.task {
            storytellerSrcGenSourceDirs.value.toSet
              .foreach { dir: File =>
                IO.copyDirectory(
                  dir,
                  storytellerSrcGenStoriesTargetDir.value,
                  CopyOptions(
                    overwrite = true,
                    preserveLastModified = true,
                    preserveExecutable = true
                  )
                )
              }
          },
          Def.task {
            srcGenTask(
              storytellerSrcGenTargetDir.value,
              target.value / "srcgen",
              storytellerSrcGenBasePackage.value,
              storytellerSrcGenDomainPackages.value
            )(storytellerSrcGenStoriesTargetDir.value.allPaths.get.toSet).toSeq
          }
        )
        .value
    )
  }

  private def srcGenTask(
      targetDir: File,
      cacheDir: File,
      basePackage: String,
      domainPackages: Seq[String]
  ): Set[File] => Set[File] =
    FileFunction.cached(cacheDir, FilesInfo.lastModified, FilesInfo.exists) {
      inputFiles: Set[File] =>
        val configValidation =
          StorytellerConfig.buildConfig(
            targetDir,
            defaultStoryExtension,
            basePackage,
            domainPackages
          )

        def filesGeneration(config: StorytellerConfig) =
          Storyteller.generateStoriesSourceFiles(
            inputFiles.filter(_.name.endsWith(defaultStoryExtension)),
            config
          )

        val generationResult = for {
          config <- configValidation
          files  <- filesGeneration(config)
        } yield files

        generationResult.fold(err => sys.error(err.msg), identity)
    }

  override def projectSettings: Seq[Def.Setting[_]] =
    taskSettings ++ defaultSettings :+
      (sourceGenerators in Test += (storytellerSrcGen in Compile).taskValue)
}
