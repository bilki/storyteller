package com.lambdarat.storyteller.writer

import com.lambdarat.storyteller.app.StorytellerConfig
import com.lambdarat.storyteller.core.StoryGenerator.StoryGenerator
import com.lambdarat.storyteller.core.StorytellerError.ErrorGeneratingSourceFiles
import com.lambdarat.storyteller.core.{StoryGenerator, StorytellerError}
import com.lambdarat.storyteller.domain.Story
import com.lambdarat.storyteller.writer.StoryWriter.{Service, StoryWriter}

import scala.meta.Source

import java.io.File
import java.nio.file.{Files, Path}

import zio.{Has, IO, ZIO, ZLayer}

object StoryWriterImpl {

  type StoryWriterDeps = StoryGenerator with Has[StorytellerConfig]

  val storyWriter: ZLayer[StoryWriterDeps, Nothing, StoryWriter] = ZLayer.fromFunction {
    storyWriterDeps =>
      new Service {
        val fileStoryWriter = new FileStoryWriter(storyWriterDeps.get[StorytellerConfig])

        override def writeStories(stories: List[Story]): IO[StorytellerError, Set[File]] =
          fileStoryWriter.writeStories(stories).provide(storyWriterDeps)
      }
  }

  private[writer] class FileStoryWriter(config: StorytellerConfig) {

    def writeStories(stories: List[Story]): ZIO[StoryGenerator, StorytellerError, Set[File]] =
      ZIO.foreach(stories)(writeStory).map(_.toSet)

    def writeStory(story: Story): ZIO[StoryGenerator, StorytellerError, File] = {
      val cleanStoryName = story.name.replaceAll(config.storySuffix, "")
      val storyFileName  = config.targetFolder.toPath.resolve(s"$cleanStoryName.scala")
      val createFile     = IO(Files.createFile(storyFileName))
      def writeToFile(createdFile: Path, source: Source) =
        IO(Files.write(createdFile, source.syntax.getBytes))

      for {
        createdFile <- createFile <> IO.fail(ErrorGeneratingSourceFiles(story.name))
        source      <- StoryGenerator.generateStoryAST(story, cleanStoryName)
        _           <- writeToFile(createdFile, source) <> IO.fail(ErrorGeneratingSourceFiles(story.name))
      } yield createdFile.toFile
    }

  }

}
