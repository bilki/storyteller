package com.lambdarat.storyteller.reader

import com.lambdarat.storyteller.core.StorytellerError
import com.lambdarat.storyteller.domain.Story

import java.io.File

import zio._

object StoryReader {

  type StoryReader = Has[StoryReader.Service]

  trait Service {
    def readStories(storyFiles: Seq[File], storySuffix: String): IO[StorytellerError, List[Story]]
  }

  def readStories(
      storyFiles: Seq[File],
      storySuffix: String
  ): ZIO[StoryReader, StorytellerError, List[Story]] =
    ZIO.accessM(_.get.readStories(storyFiles, storySuffix))

}
