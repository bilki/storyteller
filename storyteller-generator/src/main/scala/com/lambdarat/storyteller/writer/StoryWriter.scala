package com.lambdarat.storyteller.writer

import com.lambdarat.storyteller.core.{StoryGenerator, StorytellerError}
import com.lambdarat.storyteller.core.StorytellerError.ErrorGeneratingSourceFiles
import com.lambdarat.storyteller.domain.Story

import scala.meta.Source

import java.io.File
import java.nio.file.{Files, Path}

import zio.{IO, ZIO}

object StoryWriter {

  def writeStory(folder: File, basePackage: String)(
      story: Story
  ): ZIO[StoryGenerator, StorytellerError, File] = {
    val cleanStoryName = story.name.replaceAll(".story", "")
    val createFile     = IO(Files.createFile(folder.toPath.resolve(s"$cleanStoryName.scala")))
    def writeToFile(createdFile: Path, source: Source) =
      IO(Files.write(createdFile, source.syntax.getBytes))

    for {
      createdFile <- createFile <> IO.fail(ErrorGeneratingSourceFiles(story.name))
      source      <- ZIO.access[StoryGenerator](_.generateStoryAST(story, basePackage, cleanStoryName))
      _           <- writeToFile(createdFile, source) <> IO.fail(ErrorGeneratingSourceFiles(story.name))
    } yield createdFile.toFile
  }

  def writeStories(
      targetFolder: File,
      stories: List[Story],
      basePackage: String
  ): ZIO[StoryGenerator, StorytellerError, Set[File]] =
    ZIO.foreach(stories)(writeStory(targetFolder, basePackage)).map(_.toSet)

}
