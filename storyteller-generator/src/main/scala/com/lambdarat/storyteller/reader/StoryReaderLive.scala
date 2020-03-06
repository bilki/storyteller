package com.lambdarat.storyteller.reader

import com.lambdarat.storyteller.core.StorytellerError
import com.lambdarat.storyteller.core.StorytellerError.ErrorOpeningStory
import com.lambdarat.storyteller.domain.Story
import com.lambdarat.storyteller.parser.StoryParser
import com.lambdarat.storyteller.parser.StoryParser.StoryParser
import com.lambdarat.storyteller.reader.StoryReader.StoryReader
import com.lambdarat.storyteller.utils.forzio._

import scala.io.{BufferedSource, Source}

import java.io.File

import zio.{IO, UIO, ZIO, ZLayer}

object StoryReaderLive {

  val storyReader: ZLayer[StoryParser, Nothing, StoryReader] = ZLayer.fromFunction { storyParser =>
    new StoryReader.Service {
      override def readStories(
          storyFiles: Seq[File],
          storySuffix: String
      ): IO[StorytellerError, List[Story]] =
        StoryReaderLive.readStories(storyFiles, storySuffix).provide(storyParser)
    }
  }

  private[reader] def readStories(
      storyFiles: Seq[File],
      storySuffix: String
  ): ZIO[StoryParser, StorytellerError, List[Story]] =
    ZIO.foreach(storyFiles)(readStory(storySuffix))

  private[reader] def readStory(
      storySuffix: String
  )(storyFile: File): ZIO[StoryParser, StorytellerError, Story] = {
    val openStory = IO(Source.fromFile(storyFile)) <> IO.fail(ErrorOpeningStory(storyFile.getPath))
    val readStory = (source: BufferedSource) =>
      IO(source.mkString) <> IO.fail(ErrorOpeningStory(storyFile.getPath))
    val closeStoryFile = (source: BufferedSource) => UIO.succeed(source.close)
    val cleanStoryName = storyFile.getName.replace(storySuffix, "")

    for {
      storyText   <- openStory.bracket(closeStoryFile, readStory)
      parseResult <- StoryParser.parseStory(storyText, cleanStoryName)
      story       <- parseResult.result(cleanStoryName)
    } yield story
  }

}
