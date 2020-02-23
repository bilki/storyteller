package com.lambdarat.storyteller.parser

import atto.Atto._
import atto._
import cats.data.NonEmptyList
import cats.syntax.either._
import com.lambdarat.storyteller.domain.Keyword.{And, Given, Then, When}
import com.lambdarat.storyteller.domain.{Step, Story}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class StoryParserSpec extends AnyFlatSpec with Matchers {

  "Step parser" should "parse given step" in {
    val stepText = "Given a random Person"
    val expected = Step(Given, "a random Person").asRight[String]
    val parsed = StoryParser.step.parseOnly(stepText).either

    parsed shouldBe expected
  }

  it should "parse when step" in {
    val stepText = "When previous Person name starts with P"
    val expected = Step(When, "previous Person name starts with P").asRight[String]
    val parsed = StoryParser.step.parseOnly(stepText).either

    parsed shouldBe expected
  }

  it should "parse then step" in {
    val stepText = "Then previous Person opens account"
    val expected = Step(Then, "previous Person opens account").asRight[String]
    val parsed = StoryParser.step.parseOnly(stepText).either

    parsed shouldBe expected
  }

  it should "parse and step" in {
    val stepText = "And API should return created account"
    val expected = Step(And, "API should return created account").asRight[String]
    val parsed = StoryParser.step.parseOnly(stepText).either

    parsed shouldBe expected
  }

  "Story parser" should "parse single step" in {
    val storyText = "Given a random Person"
    val expected = Story(NonEmptyList.of(Step(Given, "a random Person"))).asRight[String]
    val parsed = StoryParser.story.parseOnly(storyText).either

    parsed shouldBe expected
  }

  it should "parse multiple steps" in {
    val storyText =
      """Given a random Person
        |When previous Person name starts with P
        |""".stripMargin
    val expected = Story(NonEmptyList.of(
      Step(Given, "a random Person"),
      Step(When, "previous Person name starts with P"))
    ).asRight[String]
    val parsed = StoryParser.story.parseOnly(storyText).either

    parsed shouldBe expected
  }

}
