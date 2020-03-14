package com.lambdarat.storyteller.app

import com.lambdarat.storyteller.core.{StoryGeneratorImpl, StorytellerError}
import com.lambdarat.storyteller.parser.StoryParserImpl
import com.lambdarat.storyteller.reader.{StoryReader, StoryReaderImpl}
import com.lambdarat.storyteller.writer.{StoryWriter, StoryWriterImpl}

import java.io.File

import zio.{IO, Runtime, ZLayer}

object Storyteller {

  def generateStoriesSourceFiles(
      storyFiles: Set[File],
      config: StorytellerConfig
  ): Either[StorytellerError, Set[File]] = {

    val generation = for {
      stories   <- StoryReader.readStories(storyFiles.toSeq)
      generated <- StoryWriter.writeStories(stories)
    } yield generated

    val configLayer = ZLayer.succeed(config)

    val generateLayer = configLayer >>> StoryGeneratorImpl.storyGenerator.passthrough
    val writingLayer  = generateLayer >>> StoryWriterImpl.storyWriter

    val parsingLayer = StoryParserImpl.storyParser
    val readingLayer = configLayer ++ parsingLayer >>> StoryReaderImpl.storyReader

    val dependencies = writingLayer ++ readingLayer

    val execution: IO[StorytellerError, Set[File]] = generation.provideLayer(dependencies)

    Runtime.default.unsafeRun(execution.either)
  }

}
