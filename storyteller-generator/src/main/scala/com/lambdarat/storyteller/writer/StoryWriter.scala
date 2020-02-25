package com.lambdarat.storyteller.writer

import com.lambdarat.storyteller.core.StorytellerError
import com.lambdarat.storyteller.core.StorytellerError.GeneratingError
import com.lambdarat.storyteller.domain.Story

import java.io.File
import java.nio.file.{Files, Path}

import zio.IO

object StoryWriter {

  def writeStory(folder: File)(story: Story): IO[StorytellerError, File] = {
    val createFile = IO(Files.createFile(folder.toPath.resolve(story.name)))
    def writeToFile(createdFile: Path) =
      IO(Files.write(createdFile, story.steps.head.toString.getBytes))

    for {
      createdFile <- createFile <> IO.fail(GeneratingError(story.name))
      _           <- writeToFile(createdFile) <> IO.fail(GeneratingError(story.name))
    } yield createdFile.toFile
  }

  def writeStories(targetFolder: File, stories: List[Story]): IO[StorytellerError, Set[File]] =
    IO.foreach(stories)(writeStory(targetFolder)).map(_.toSet)

}
