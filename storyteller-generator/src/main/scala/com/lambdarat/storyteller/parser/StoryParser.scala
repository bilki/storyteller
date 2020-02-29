package com.lambdarat.storyteller.parser

import zio.ZIO

import atto._
import com.lambdarat.storyteller.domain.Story

trait StoryParser {
  val storyParser: StoryParser.Service[Any]
}

object StoryParser {

  trait Service[R] {
    def parseStory(text: String, name: String): ZIO[R, Nothing, ParseResult[Story]]
  }

  def parseStory(text: String, name: String): ZIO[StoryParser, Nothing, ParseResult[Story]] =
    ZIO.accessM(_.storyParser.parseStory(text, name))

}
