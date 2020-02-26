package com.lambdarat.storyteller.parser

import com.lambdarat.storyteller.domain.{Keyword, Step, Story}

import atto.Atto._
import atto._
import cats.data.NonEmptyList
import cats.implicits._

trait StoryParser {
  def parseStory(text: String, name: String): ParseResult[Story]
}

object StoryParser extends StoryParser {

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

  def parseStory(text: String, name: String): ParseResult[Story] =
    steps.parseOnly(text).map(Story(name, _))

}
