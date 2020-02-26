package com.lambdarat.storyteller.writer

import com.lambdarat.storyteller.core.{StoryGenerator, StorytellerError}
import com.lambdarat.storyteller.core.StorytellerError.ErrorGeneratingSourceFiles
import com.lambdarat.storyteller.domain.Story

import scala.meta.Source

import java.io.File
import java.nio.file.{Files, Path}

import zio.{IO, ZIO}

object StoryWriter {

  def writeStory(folder: File)(story: Story): ZIO[StoryGenerator, StorytellerError, File] = {
    val cleanStoryName = story.name.replaceAll(".story", "")
    val createFile     = IO(Files.createFile(folder.toPath.resolve(s"$cleanStoryName.scala")))
    def writeToFile(createdFile: Path, source: Source) =
      IO(Files.write(createdFile, source.syntax.getBytes))

    for {
      createdFile <- createFile <> IO.fail(ErrorGeneratingSourceFiles(story.name))
      source      <- ZIO.access[StoryGenerator](_.generateStoryAST(story, cleanStoryName))
      _           <- writeToFile(createdFile, source) <> IO.fail(ErrorGeneratingSourceFiles(story.name))
    } yield createdFile.toFile
  }

  def writeStories(
      targetFolder: File,
      stories: List[Story]
  ): ZIO[StoryGenerator, StorytellerError, Set[File]] =
    ZIO.foreach(stories)(writeStory(targetFolder)).map(_.toSet)

}
