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

  def parseStories(storyFiles: Seq[File]): IO[StorytellerError, List[Story]] =
    IO.foreach(storyFiles)(parseStory)

  def parseStory(storyFile: File): IO[StorytellerError, Story] = {
    val openStory = IO(Source.fromFile(storyFile)) <> IO.fail(ErrorOpeningStory(storyFile.getPath))
    val readStory = (source: BufferedSource) =>
      IO(source.mkString) <> IO.fail(ErrorOpeningStory(storyFile.getPath))
    val closeStoryFile = (source: BufferedSource) => UIO.succeed(source.close)

    for {
      storyText <- openStory.bracket(closeStoryFile, readStory)
      story     <- StoryParser.parse(storyText, storyFile.getName).result(storyText)
    } yield story
  }

}
