package com.lambdarat.storyteller.app

import com.lambdarat.storyteller.core.StoryGeneratorImpl
import com.lambdarat.storyteller.parser.StoryParserImpl
import com.lambdarat.storyteller.reader.{StoryReader, StoryReaderImpl}
import com.lambdarat.storyteller.writer.{StoryWriter, StoryWriterImpl}

import java.io.File

import zio.{Runtime, ZLayer}

object Storyteller {

  def generateStoriesSourceFiles(
      storyFiles: Set[File],
      config: StorytellerConfig
  ): Set[File] = {

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

    Runtime.default.unsafeRun(generation.provideLayer(dependencies))
  }

}
