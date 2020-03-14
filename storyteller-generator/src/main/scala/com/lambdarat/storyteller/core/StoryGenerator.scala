package com.lambdarat.storyteller.core

import com.lambdarat.storyteller.domain.Story

import scala.meta.Source

import zio.{Has, ZIO}

object StoryGenerator {

  type StoryGenerator = Has[StoryGenerator.Service]

  trait Service {
    def generateStoryAST(story: Story, testName: String): Source
  }

  def generateStoryAST(
      story: Story,
      testName: String
  ): ZIO[StoryGenerator, StorytellerError, Source] =
    ZIO.access(_.get.generateStoryAST(story, testName))

}
