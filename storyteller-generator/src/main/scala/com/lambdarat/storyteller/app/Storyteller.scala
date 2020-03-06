package com.lambdarat.storyteller.app

import com.lambdarat.storyteller.core.StoryGeneratorLive
import com.lambdarat.storyteller.parser.StoryParserLive
import com.lambdarat.storyteller.reader.{StoryReader, StoryReaderLive}
import com.lambdarat.storyteller.writer.{StoryWriter, StoryWriterLive}

import java.io.File

import zio.Runtime

object Storyteller {

  def generateStoriesSourceFiles(
      storyFiles: Set[File],
      targetFolder: File,
      storySuffix: String,
      basePackage: String
  ): Set[File] = {

    val generation = for {
      stories   <- StoryReader.readStories(storyFiles.toSeq, storySuffix)
      generated <- StoryWriter.writeStories(targetFolder, stories, basePackage)
    } yield generated

    val writingLayer = StoryGeneratorLive.storyGenerator >>> StoryWriterLive.storyWriter
    val readingLayer = StoryParserLive.storyParserLayer >>> StoryReaderLive.storyReader
    val dependencies = writingLayer ++ readingLayer

    Runtime.default.unsafeRun(generation.provideLayer(dependencies))
  }

}
