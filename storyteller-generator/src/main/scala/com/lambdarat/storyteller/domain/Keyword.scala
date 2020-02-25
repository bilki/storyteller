package com.lambdarat.storyteller.domain

sealed abstract class Keyword(val word: String)

object Keyword {

  case object Given extends Keyword("given")

  case object When extends Keyword("when")

  case object Then extends Keyword("then")

  case object And extends Keyword("and")

}
