package com.lambdarat.storyteller.app

import com.lambdarat.storyteller.core.StoryGenerator
import com.lambdarat.storyteller.domain.Story
import com.lambdarat.storyteller.parser.StoryParser
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
      storySuffix: String
  ): Set[File] = {
    val generation = for {
      stories   <- StoryReader.parseStories(storyFiles.toSeq, storySuffix)
      generated <- StoryWriter.writeStories(targetFolder, stories)
    } yield generated

    type Dependencies = StoryParser with StoryGenerator

    val dependencies: Dependencies = new StoryParser with StoryGenerator {
      override def parseStory(text: String, name: String): ParseResult[Story] =
        StoryParser.parseStory(text, name)

      override def generateStoryAST(story: Story, testName: String): Source =
        StoryGenerator.generateStoryAST(story, testName)
    }

    val generationWithDependencies = generation
      .provide(dependencies)

    unsafeRun(generationWithDependencies)
  }

}
