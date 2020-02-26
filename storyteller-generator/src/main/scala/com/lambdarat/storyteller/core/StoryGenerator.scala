package com.lambdarat.storyteller.core

import com.lambdarat.storyteller.domain.{Step, Story}

import cats.data.NonEmptyList

import scala.meta._

trait StoryGenerator {
  def generateStoryAST(story: Story, basePackage: String, testName: String): Source
}

object StoryGenerator extends StoryGenerator {

  private[core] def toCamelCase(words: NonEmptyList[String]): String =
    (words.head +: words.tail.map(_.capitalize)).mkString

  private[core] def generateFunForStep(step: Step): Stat = {
    val maybeWords = NonEmptyList.fromList(step.text.split(" ").toList)

    val funName = maybeWords.fold(s"random-${System.currentTimeMillis}")(toCamelCase)

    q"def ${Term.Name(funName)}(): Unit"
  }

  def generateStoryAST(story: Story, basePackage: String, testName: String): Source = {
    val stepsFuns = story.steps.map(generateFunForStep).toList

    source"""
      package ${Term.Name(basePackage)}

      import org.scalatest.flatspec.AnyFlatSpec
      import org.scalatest.matchers.should.Matchers

      abstract class ${Type.Name(testName)} extends AnyFlatSpec with Matchers {
        ..$stepsFuns
      }
    """
  }
}
