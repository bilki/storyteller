package com.lambdarat.storyteller.utils

import com.lambdarat.storyteller.core.StorytellerError

import atto.ParseResult
import cats.data.NonEmptyList

import zio.IO

object forzio {

  implicit class ParseResultOps[A](private val result: ParseResult[A]) {
    def result(story: String): IO[StorytellerError, A] =
      IO.fromEither(result.either).mapError(StorytellerError.ParsingError(story, _))
  }

  implicit class NonEmmptyListOps[A](private val list: List[A]) {
    def toNel[E](error: E): IO[E, NonEmptyList[A]] =
      NonEmptyList
        .fromList(list)
        .fold[IO[E, NonEmptyList[A]]](IO.fail(error))(nonEmpty => IO.succeed(nonEmpty))
  }

}
