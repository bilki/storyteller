package com.lambdarat.storyteller

import cats.syntax.show._
import com.lambdarat.storyteller.core.StorytellerError._
import com.lambdarat.storyteller.reader.StoryReader
import zio._
import zio.console._

object Storyteller extends App {

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    val processStories = for {
      stories <- StoryReader.parseStories("stories")
      _       <- ZIO.foreach(stories.toList)(story => putStrLn(story.toString))
    } yield ()

    processStories.foldM(
      err => putStrLn(s"Storyteller failing with error [${err.show}]") *> ZIO.succeed(1),
      _ => ZIO.succeed(0)
    )
  }
}
