package com.lambdarat.storyteller.core

import com.lambdarat.storyteller.domain.{Step, Story}

import cats.data.NonEmptyList

import scala.meta._

object StoryGenerator {

  def toCamelCase(words: NonEmptyList[String]): String =
    (words.head +: words.tail.map(_.capitalize)).mkString

  def generateFunForStep(step: Step): Stat = {
    val maybeWords = NonEmptyList.fromList(step.text.split(" ").toList)

    val funName = maybeWords.fold(s"random-${System.currentTimeMillis}")(toCamelCase)

    q"def ${Term.Name(funName)}(): F[_]"
  }

  def generateStoryAST(story: Story, testName: String): Source = {
    val stepsFuns = story.steps.map(generateFunForStep).toList

    source"""
      import org.scalatest.flatspec.AnyFlatSpec
      import org.scalatest.matchers.should.Matchers

      class ${Type.Name(testName)} extends AnyFlatSpec with Matchers {
        ..$stepsFuns
      }
    """
  }
}
