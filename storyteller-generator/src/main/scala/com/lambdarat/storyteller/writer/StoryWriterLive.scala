package com.lambdarat.storyteller.writer

import com.lambdarat.storyteller.core.StoryGenerator.StoryGenerator
import com.lambdarat.storyteller.core.{StoryGenerator, StorytellerError}
import com.lambdarat.storyteller.core.StorytellerError.ErrorGeneratingSourceFiles
import com.lambdarat.storyteller.domain.Story
import com.lambdarat.storyteller.writer.StoryWriter.{Service, StoryWriter}

import scala.meta.Source

import java.io.File
import java.nio.file.{Files, Path}

import zio.{IO, ZIO, ZLayer}

object StoryWriterLive {

  val storyWriter: ZLayer[StoryGenerator, Nothing, StoryWriter] = ZLayer.fromFunction {
    storyGenerator =>
      new Service {
        override def writeStories(
            targetFolder: File,
            stories: List[Story],
            basePackage: String
        ): IO[StorytellerError, Set[File]] =
          StoryWriterLive.writeStories(targetFolder, stories, basePackage).provide(storyGenerator)
      }
  }

  private[writer] def writeStories(
      targetFolder: File,
      stories: List[Story],
      basePackage: String
  ): ZIO[StoryGenerator, StorytellerError, Set[File]] =
    ZIO.foreach(stories)(writeStory(targetFolder, basePackage)).map(_.toSet)

  private[writer] def writeStory(folder: File, basePackage: String)(
      story: Story
  ): ZIO[StoryGenerator, StorytellerError, File] = {
    val cleanStoryName = story.name.replaceAll(".story", "")
    val createFile     = IO(Files.createFile(folder.toPath.resolve(s"$cleanStoryName.scala")))
    def writeToFile(createdFile: Path, source: Source) =
      IO(Files.write(createdFile, source.syntax.getBytes))

    for {
      createdFile <- createFile <> IO.fail(ErrorGeneratingSourceFiles(story.name))
      source      <- StoryGenerator.generateStoryAST(story, basePackage, cleanStoryName)
      _           <- writeToFile(createdFile, source) <> IO.fail(ErrorGeneratingSourceFiles(story.name))
    } yield createdFile.toFile
  }

}
