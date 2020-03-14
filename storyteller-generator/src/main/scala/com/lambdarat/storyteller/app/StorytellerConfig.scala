package com.lambdarat.storyteller.app

import com.lambdarat.storyteller.core.StorytellerError
import com.lambdarat.storyteller.core.StorytellerError.ErrorValidatingDomainPackages

import scala.meta._

import java.io.File

import cats.implicits._

case class StorytellerConfig(
    targetFolder: File,
    storySuffix: String,
    basePackage: String,
    domainPackages: Seq[Importer]
)

object StorytellerConfig {

  def buildConfig(
      targetFolder: File,
      storySuffix: String,
      basePackage: String,
      domainPackages: Seq[String]
  ): Either[StorytellerError, StorytellerConfig] = {

    val importersAttempt =
      domainPackages.toList
        .map(_.parse[Importer].toEither)
        .sequence[Either[Parsed.Error, *], Importer]

    importersAttempt.bimap(
      parserErr => ErrorValidatingDomainPackages(domainPackages, parserErr.message),
      importers => StorytellerConfig(targetFolder, storySuffix, basePackage, importers)
    )
  }

}
