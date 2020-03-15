package com.lambdarat.storyteller.core

import com.lambdarat.storyteller.app.StorytellerConfig
import com.lambdarat.storyteller.core.StoryGenerator.StoryGenerator
import com.lambdarat.storyteller.domain.{Step, Story}

import scala.meta._

import cats.data.NonEmptyList

import zio.{Has, ZLayer}

object StoryGeneratorImpl {

  val storyGenerator: ZLayer[Has[StorytellerConfig], Nothing, StoryGenerator] = ZLayer.fromService(
    config =>
      new StoryGenerator.Service {
        val storyGenerator = new DefaultStoryGenerator(config)

        override def generateStoryAST(story: Story, testName: String): Source =
          storyGenerator.generateStoryAST(story, testName)
      }
  )

  private[core] def toCamelCase(words: NonEmptyList[String]): String =
    (words.head +: words.tail.map(_.capitalize)).mkString

  // Repaths term to hold a wildcard importee
  private[core] def wildcardImporter(select: Term.Select): Importer = {
    val wildcard = List(Importee.Wildcard())
    Importer(select, wildcard)
  }

  private[core] class DefaultStoryGenerator(config: StorytellerConfig) {

    def generateFunForStep(step: Step): Stat = {
      val maybeWords = NonEmptyList.fromList(step.text.split(" ").toList)

      val funName = maybeWords.fold(s"random-${System.currentTimeMillis}")(toCamelCase)

      q"def ${Term.Name(funName)}(): Unit"
    }

    def generateStoryAST(story: Story, testName: String): Source = {
      val stepsFuns = story.steps.map(generateFunForStep).toList
      val imports   = config.domainPackages.map(wildcardImporter).toList

      source"""
        package ${config.basePackage}

        import org.scalatest.flatspec.AnyFlatSpec
        import org.scalatest.matchers.should.Matchers

        import ..$imports

        abstract class ${Type.Name(testName)} extends AnyFlatSpec with Matchers {
          ..$stepsFuns
        }
      """

    }

  }
}
