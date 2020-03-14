package com.lambdarat.storyteller.core

import cats.Show

sealed abstract class StorytellerError(val msg: String) extends Exception

object StorytellerError {

  case class ErrorValidatingDomainPackages(packages: Seq[String], error: String)
      extends StorytellerError(
        s"[$error] happened while validating domain packages: [${packages.mkString(",\n")}]"
      )

  case class FailureReadingResources(storiesFolder: String)
      extends StorytellerError(s"Failing while opening stories folder [$storiesFolder]")

  case class ErrorOpeningStory(storyPath: String)
      extends StorytellerError(s"Error while opening [$storyPath] story file")

  case class ParsingError(storyPath: String, error: String)
      extends StorytellerError(s"[$error] happened while parsing story [$storyPath]")

  case class ErrorGeneratingSourceFiles(storyPath: String)
      extends StorytellerError(s"Error while generating source file for story [$storyPath]")

  implicit val showInstance: Show[StorytellerError] =
    Show.show(se => s"[Storyteller] ${se.msg} caused by: ${se.getStackTrace}")

}
