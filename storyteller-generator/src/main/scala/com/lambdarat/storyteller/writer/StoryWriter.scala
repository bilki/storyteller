package com.lambdarat.storyteller.writer

import com.lambdarat.storyteller.core.{StoryGenerator, StorytellerError}
import com.lambdarat.storyteller.core.StorytellerError.GeneratingError
import com.lambdarat.storyteller.domain.Story

import java.io.File
import java.nio.file.{Files, Path}

import zio.IO

object StoryWriter {

  def writeStory(folder: File)(story: Story): IO[StorytellerError, File] = {
    val cleanStoryName = story.name.replaceAll(".story", "")
    val createFile     = IO(Files.createFile(folder.toPath.resolve(s"$cleanStoryName.scala")))
    def writeToFile(createdFile: Path)(code: String) =
      IO(Files.write(createdFile, code.getBytes))
    val generatedCode = StoryGenerator.generateStoryAST(story, cleanStoryName)

    for {
      createdFile <- createFile <> IO.fail(GeneratingError(story.name))
      _           <- writeToFile(createdFile)(generatedCode.syntax) <> IO.fail(GeneratingError(story.name))
    } yield createdFile.toFile
  }

  def writeStories(targetFolder: File, stories: List[Story]): IO[StorytellerError, Set[File]] =
    IO.foreach(stories)(writeStory(targetFolder)).map(_.toSet)

}
