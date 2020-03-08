package com.lambdarat.storyteller.reader

import com.lambdarat.storyteller.app.StorytellerConfig
import com.lambdarat.storyteller.core.StorytellerError
import com.lambdarat.storyteller.core.StorytellerError.ErrorOpeningStory
import com.lambdarat.storyteller.domain.Story
import com.lambdarat.storyteller.parser.StoryParser
import com.lambdarat.storyteller.parser.StoryParser.StoryParser
import com.lambdarat.storyteller.reader.StoryReader.StoryReader
import com.lambdarat.storyteller.utils.forzio._

import scala.io.{BufferedSource, Source}

import java.io.File

import zio.{Has, IO, UIO, ZIO, ZLayer}

object StoryReaderLive {

  val storyReader: ZLayer[StoryParser with Has[StorytellerConfig], Nothing, StoryReader] =
    ZLayer.fromFunction { storyParserWithConfig =>
      new StoryReader.Service {
        val fileStoryReader = new FileStoryReader(storyParserWithConfig.get[StorytellerConfig])

        override def readStories(storyFiles: Seq[File]): IO[StorytellerError, List[Story]] =
          fileStoryReader.readStories(storyFiles).provide(storyParserWithConfig)
      }
    }

  private[reader] class FileStoryReader(config: StorytellerConfig) {

    def readStories(storyFiles: Seq[File]): ZIO[StoryParser, StorytellerError, List[Story]] =
      ZIO.foreach(storyFiles)(readStory)

    def readStory(storyFile: File): ZIO[StoryParser, StorytellerError, Story] = {
      val openStory = IO(Source.fromFile(storyFile)) <> IO.fail(
        ErrorOpeningStory(storyFile.getPath)
      )
      val readStory = (source: BufferedSource) =>
        IO(source.mkString) <> IO.fail(ErrorOpeningStory(storyFile.getPath))
      val closeStoryFile = (source: BufferedSource) => UIO.succeed(source.close)
      val storyName      = storyFile.getName.replace(config.storySuffix, "")

      for {
        storyText   <- openStory.bracket(closeStoryFile, readStory)
        parseResult <- StoryParser.parseStory(storyText, storyName)
        story       <- parseResult.result(storyName)
      } yield story
    }

  }

}
