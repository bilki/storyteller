package com.lambdarat.storyteller.core

import com.lambdarat.storyteller.core.StoryGenerator.StoryGenerator
import com.lambdarat.storyteller.domain.{Step, Story}

import cats.data.NonEmptyList

import scala.meta._

import zio.ZLayer

object StoryGeneratorImpl {

  val storyGenerator: ZLayer.NoDeps[Nothing, StoryGenerator] = ZLayer.succeed(
    new StoryGenerator.Service {
      override def generateStoryAST(story: Story, basePackage: String, testName: String): Source =
        StoryGeneratorImpl.generateStoryAST(story, basePackage, testName)
    }
  )

  private[core] def toCamelCase(words: NonEmptyList[String]): String =
    (words.head +: words.tail.map(_.capitalize)).mkString

  private[core] def generateFunForStep(step: Step): Stat = {
    val maybeWords = NonEmptyList.fromList(step.text.split(" ").toList)

    val funName = maybeWords.fold(s"random-${System.currentTimeMillis}")(toCamelCase)

    q"def ${Term.Name(funName)}(): Unit"
  }

  private[core] def generateStoryAST(
      story: Story,
      basePackage: String,
      testName: String
  ): Source = {
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
