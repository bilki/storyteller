package com.lambdarat.storyteller.domain

import cats.data.NonEmptyList

case class Story(steps: NonEmptyList[Step])
