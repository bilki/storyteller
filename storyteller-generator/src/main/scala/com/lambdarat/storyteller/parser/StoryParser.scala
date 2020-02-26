package com.lambdarat.storyteller.parser

import com.lambdarat.storyteller.domain.{Keyword, Step, Story}
import com.lambdarat.storyteller.parser.StoryParser.steps

import atto.Atto._
import atto._
import cats.data.NonEmptyList
import cats.implicits._

trait StoryParser {
  val storyParser: StoryParser.Service
}

object StoryParser {

  trait Service {
    def parseStory(text: String, name: String): ParseResult[Story]
  }

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

trait StoryParserLive extends StoryParser {
  final val storyParser: StoryParser.Service = new StoryParser.Service {
    override def parseStory(text: String, name: String): ParseResult[Story] =
      steps.parseOnly(text).map(Story(name, _))
  }
}
