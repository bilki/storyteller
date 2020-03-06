package com.lambdarat.storyteller.parser

import com.lambdarat.storyteller.domain.Story

import atto._

import zio.{Has, ZIO}

object StoryParser {

  type StoryParser = Has[StoryParser.Service]

  trait Service {
    def parseStory(text: String, name: String): ParseResult[Story]
  }

  def parseStory(text: String, name: String): ZIO[StoryParser, Nothing, ParseResult[Story]] =
    ZIO.access(_.get.parseStory(text, name))

}
