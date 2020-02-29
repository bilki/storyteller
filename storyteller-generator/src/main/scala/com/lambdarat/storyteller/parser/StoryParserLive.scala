package com.lambdarat.storyteller.parser

import zio.UIO

import atto.Atto._
import atto._
import cats.data.NonEmptyList
import cats.implicits._
import com.lambdarat.storyteller.domain.{Keyword, Step, Story}

trait StoryParserLive extends StoryParser {
  import StoryParserLive._

  final val storyParser: StoryParser.Service[Any] = new StoryParser.Service[Any] {
    override def parseStory(text: String, name: String): UIO[ParseResult[Story]] =
      UIO.succeed(steps.parseOnly(text).map(Story(name, _)))
  }
}

object StoryParserLive {
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
