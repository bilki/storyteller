package com.lambdarat.storyteller.app

import com.lambdarat.storyteller.core.StoryGeneratorLive
import com.lambdarat.storyteller.parser.StoryParserLive
import com.lambdarat.storyteller.reader.{StoryReader, StoryReaderLive}
import com.lambdarat.storyteller.writer.{StoryWriter, StoryWriterLive}

import java.io.File

import zio.{Runtime, ZLayer}

object Storyteller {

  def generateStoriesSourceFiles(
      storyFiles: Set[File],
      config: StorytellerConfig
  ): Set[File] = {

    val generation = for {
      stories   <- StoryReader.readStories(storyFiles.toSeq)
      generated <- StoryWriter.writeStories(config.targetFolder, stories, config.basePackage)
    } yield generated

    val configLayer = ZLayer.succeed(config)

    val writingLayer = StoryGeneratorLive.storyGenerator >>> StoryWriterLive.storyWriter
    val readingLayer = (StoryParserLive.storyParser ++ configLayer) >>> StoryReaderLive.storyReader
    val dependencies = writingLayer ++ readingLayer

    Runtime.default.unsafeRun(generation.provideLayer(dependencies))
  }

}
