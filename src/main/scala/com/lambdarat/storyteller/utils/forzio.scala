package com.lambdarat.storyteller.utils

import atto.ParseResult
import cats.data.NonEmptyList
import com.lambdarat.storyteller.core.StorytellerError
import zio.IO

object forzio {

  implicit class ParseResultOps[A](private val result: ParseResult[A]) {
    def result(story: String): IO[StorytellerError, A] =
      result.either
        .fold(parseErr => IO.fail(StorytellerError.ParsingError(story, parseErr)), IO.succeed)
  }

  implicit class NonEmmptyListOps[A](private val list: List[A]) {
    def toNel[E](error: E): IO[E, NonEmptyList[A]] =
      NonEmptyList
        .fromList(list)
        .fold[IO[E, NonEmptyList[A]]](IO.fail(error))(nonEmpty => IO.succeed(nonEmpty))
  }

}
