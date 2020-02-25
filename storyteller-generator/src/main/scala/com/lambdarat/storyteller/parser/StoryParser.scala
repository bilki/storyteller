package com.lambdarat.storyteller.parser

import atto.Atto._
import atto._
import cats.implicits._
import com.lambdarat.storyteller.domain.{Keyword, Step, Story}

object StoryParser {

  val keyword: Parser[Keyword] = {
    import Keyword._

    stringCI(Given.word).as(Given) |
      stringCI(When.word).as(When) |
      stringCI(Then.word).as(Then) |
      stringCI(And.word).as(And)
  }

  val step: Parser[Step] = (keyword <~ char(' '), takeWhile(_ != '\n')).mapN(Step.apply)

  def story(name: String): Parser[Story] = sepBy1(step, char('\n')).map(Story(name, _))

  def parse(text: String, name: String): ParseResult[Story] = story(name).parseOnly(text)

}
