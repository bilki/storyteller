package com.lambdarat.storyteller.app

import com.lambdarat.storyteller.core.StoryGenerator
import com.lambdarat.storyteller.domain.Story
import com.lambdarat.storyteller.parser.{StoryParser, StoryParserLive}
import com.lambdarat.storyteller.reader.StoryReader
import com.lambdarat.storyteller.writer.StoryWriter

import atto.ParseResult

import scala.meta.Source

import java.io.File

import zio.DefaultRuntime

object Storyteller extends DefaultRuntime {

  def generateStoriesSourceFiles(
      storyFiles: Set[File],
      targetFolder: File,
      storySuffix: String,
      basePackage: String
  ): Set[File] = {
    val generation = for {
      stories   <- StoryReader.parseStories(storyFiles.toSeq, storySuffix)
      generated <- StoryWriter.writeStories(targetFolder, stories, basePackage)
    } yield generated

    type Dependencies = StoryParser with StoryGenerator

    val dependencies: Dependencies = new StoryParserLive with StoryGenerator {
      override def generateStoryAST(story: Story, basePackage: String, testName: String): Source =
        StoryGenerator.generateStoryAST(story, basePackage, testName)
    }

    val generationWithDependencies = generation
      .provide(dependencies)

    unsafeRun(generationWithDependencies)
  }

}
