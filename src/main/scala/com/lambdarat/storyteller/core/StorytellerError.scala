package com.lambdarat.storyteller.core

import cats.Show

sealed abstract class StorytellerError(val msg: String) extends Exception

object StorytellerError {

  case object FailureReadingResources
      extends StorytellerError("Failing while opening stories folder")

  case object NonExistingStories extends StorytellerError("Stories folder is empty")

  case class NonExistingStory(storyPath: String)
      extends StorytellerError(s"Error while opening [${storyPath}] story file")

  case class ParsingError(storyPath: String, error: String)
      extends StorytellerError(s"Error [${error}] while parsing story [${storyPath}]")

  implicit val showInstance: Show[StorytellerError] = Show.show {
    case FailureReadingResources => FailureReadingResources.msg
    case NonExistingStories      => NonExistingStories.msg
    case nes: NonExistingStory   => nes.msg
    case pe: ParsingError        => pe.msg
  }

}
