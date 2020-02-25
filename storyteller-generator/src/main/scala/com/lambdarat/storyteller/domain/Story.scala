package com.lambdarat.storyteller.domain

import cats.data.NonEmptyList

case class Story(name: String, steps: NonEmptyList[Step])
