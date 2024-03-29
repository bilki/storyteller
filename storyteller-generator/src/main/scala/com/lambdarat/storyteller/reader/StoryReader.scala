package com.lambdarat.storyteller.reader

import com.lambdarat.storyteller.core.StorytellerError
import com.lambdarat.storyteller.domain.Story

import java.io.File

import zio.{Has, IO, ZIO}

object StoryReader {

  type StoryReader = Has[StoryReader.Service]

  trait Service {
    def readStories(storyFiles: Seq[File]): IO[StorytellerError, List[Story]]
  }

  def readStories(storyFiles: Seq[File]): ZIO[StoryReader, StorytellerError, List[Story]] =
    ZIO.accessM(_.get.readStories(storyFiles))

}
