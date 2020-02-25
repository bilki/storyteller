package com.lambdarat.storyteller.core

import cats.Show

sealed abstract class StorytellerError(val msg: String) extends Exception

object StorytellerError {

  case class FailureReadingResources(storiesFolder: String)
      extends StorytellerError(s"Failing while opening stories folder [$storiesFolder]")

  case object NonExistingStories extends StorytellerError(s"No  is empty")

  case class ErrorOpeningStory(storyPath: String)
      extends StorytellerError(s"Error while opening [$storyPath] story file")

  case class ParsingError(storyPath: String, error: String)
      extends StorytellerError(s"[$error] happened while parsing story [$storyPath]")

  case class GeneratingError(storyPath: String)
      extends StorytellerError(s"Error while generating file [$storyPath]")

  implicit val showInstance: Show[StorytellerError] =
    Show.show(se => s"[Storyteller] ${se.msg} caused by: ${se.getStackTrace}")

}
