package com.lambdarat.storyteller.reader

import com.lambdarat.storyteller.core.StorytellerError
import com.lambdarat.storyteller.core.StorytellerError._
import com.lambdarat.storyteller.domain.Story
import com.lambdarat.storyteller.parser.StoryParser
import com.lambdarat.storyteller.utils.forzio._

import scala.io.{BufferedSource, Source}

import java.io.File

import zio._

object StoryReader {

  def parseStories(
      storyFiles: Seq[File],
      storySuffix: String
  ): ZIO[StoryParser, StorytellerError, List[Story]] =
    ZIO.foreach(storyFiles)(parseStory(storySuffix))

  def parseStory(
      storySuffix: String
  )(storyFile: File): ZIO[StoryParser, StorytellerError, Story] = {
    val openStory = IO(Source.fromFile(storyFile)) <> IO.fail(ErrorOpeningStory(storyFile.getPath))
    val readStory = (source: BufferedSource) =>
      IO(source.mkString) <> IO.fail(ErrorOpeningStory(storyFile.getPath))
    val closeStoryFile = (source: BufferedSource) => UIO.succeed(source.close)
    val cleanStoryName = storyFile.getName.replace(storySuffix, "")

    for {
      storyText   <- openStory.bracket(closeStoryFile, readStory)
      parseResult <- ZIO.access[StoryParser](_.storyParser.parseStory(storyText, cleanStoryName))
      story       <- parseResult.result(cleanStoryName)
    } yield story
  }

}
