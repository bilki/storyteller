package com.lambdarat.storyteller.writer

import com.lambdarat.storyteller.core.StorytellerError
import com.lambdarat.storyteller.domain.Story

import java.io.File

import zio.{Has, IO, ZIO}

object StoryWriter {

  type StoryWriter = Has[StoryWriter.Service]

  trait Service {
    def writeStories(stories: List[Story]): IO[StorytellerError, Set[File]]
  }

  def writeStories(stories: List[Story]): ZIO[StoryWriter, StorytellerError, Set[File]] =
    ZIO.accessM(_.get.writeStories(stories))

}
