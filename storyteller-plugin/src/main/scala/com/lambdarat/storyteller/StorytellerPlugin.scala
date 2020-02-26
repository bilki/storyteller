package com.lambdarat.storyteller

import com.lambdarat.storyteller.app.Storyteller

import sbt.Keys._
import sbt._

import java.io.File

object StorytellerPlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

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
              storytellerSrcGenBasePackage.value
            )(storytellerSrcGenStoriesTargetDir.value.allPaths.get.toSet).toSeq
          }
        )
        .value
    )
  }

  private def srcGenTask(
      targetDir: File,
      cacheDir: File,
      basePackage: String
  ): Set[File] => Set[File] =
    FileFunction.cached(cacheDir, FilesInfo.lastModified, FilesInfo.exists) {
      inputFiles: Set[File] =>
        Storyteller.generateStoriesSourceFiles(
          inputFiles.filter(_.name.endsWith(defaultStoryExtension)),
          targetDir,
          defaultStoryExtension,
          basePackage
        )
    }

  override def projectSettings: Seq[Def.Setting[_]] = taskSettings
}
