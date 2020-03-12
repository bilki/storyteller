package com.lambdarat.storyteller.parser

import com.lambdarat.storyteller.domain.{Keyword, Step, Story}
import com.lambdarat.storyteller.parser.StoryParser.StoryParser

import atto.Atto._
import atto._

import cats.data.NonEmptyList
import cats.implicits._

import zio.{Layer, ZLayer}

object StoryParserImpl {

  val storyParser: Layer[Nothing, StoryParser] = ZLayer.succeed(
    new StoryParser.Service {
      override def parseStory(text: String, name: String): ParseResult[Story] =
        steps.parseOnly(text).map(Story(name, _))
    }
  )

  private[parser] val keyword: Parser[Keyword] = {
    import Keyword._

    stringCI(Given.word).as(Given) |
      stringCI(When.word).as(When) |
      stringCI(Then.word).as(Then) |
      stringCI(And.word).as(And)
  }

  private[parser] val step: Parser[Step] =
    (keyword <~ char(' '), takeWhile(_ != '\n')).mapN(Step.apply)

  private[parser] val steps: Parser[NonEmptyList[Step]] = sepBy1(step, char('\n'))
}
