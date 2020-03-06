package com.lambdarat.storyteller.core

import com.lambdarat.storyteller.domain.Story

import scala.meta.Source

import zio.{Has, ZIO}

object StoryGenerator {

  type StoryGenerator = Has[StoryGenerator.Service]

  trait Service {
    def generateStoryAST(story: Story, basePackage: String, testName: String): Source
  }

  def generateStoryAST(
      story: Story,
      basePackage: String,
      testName: String
  ): ZIO[StoryGenerator, Nothing, Source] =
    ZIO.access(_.get.generateStoryAST(story, basePackage, testName))

}
