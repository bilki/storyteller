package com.lambdarat.storyteller.app

import com.lambdarat.storyteller.reader.StoryReader
import com.lambdarat.storyteller.writer.StoryWriter

import java.io.File

import zio.DefaultRuntime

object Storyteller extends DefaultRuntime {

  def generateStoryFiles(storyFiles: Set[File], targetFolder: File): Set[File] = {
    val generation = for {
      stories   <- StoryReader.parseStories(storyFiles.toSeq)
      generated <- StoryWriter.writeStories(targetFolder, stories)
    } yield generated

    unsafeRun(generation)
  }

}
