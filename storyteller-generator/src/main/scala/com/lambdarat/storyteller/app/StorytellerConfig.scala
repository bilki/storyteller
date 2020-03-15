package com.lambdarat.storyteller.app

import com.lambdarat.storyteller.core.StorytellerError
import com.lambdarat.storyteller.core.StorytellerError.{
  ErrorValidatingBasePackage,
  ErrorValidatingDomainPackages
}

import scala.meta._

import java.io.File

import cats.implicits._

case class StorytellerConfig(
    targetFolder: File,
    storySuffix: String,
    basePackage: Term.Select,
    domainPackages: Seq[Term.Select]
)

object StorytellerConfig {

  private def invalidBasePkgMsg(value: String) = s"No valid syntax for base package [$value]"
  private def invalidBasePkgError(basePkg: String)(value: String): ErrorValidatingBasePackage = {
    val errorMsg = invalidBasePkgMsg(value)
    ErrorValidatingBasePackage(basePkg, errorMsg)
  }

  private def invalidDomainPkgMsg(value: String) = s"No valid syntax for domain package [$value]"
  private def invalidDomainPkgError(
      packages: Seq[String]
  )(value: String): ErrorValidatingDomainPackages = {
    val errorMsg = invalidDomainPkgMsg(value)
    ErrorValidatingDomainPackages(packages, errorMsg)
  }

  def buildConfig(
      targetFolder: File,
      storySuffix: String,
      basePackage: String,
      domainPackages: Seq[String]
  ): Either[StorytellerError, StorytellerConfig] = {

    val invalidBasePkg = invalidBasePkgError(basePackage) _
    val basePackageAttempt =
      basePackage
        .parse[Term]
        .toEither
        .leftMap(err => invalidBasePkg(err.message))
        .flatMap {
          case t: Term.Select => t.asRight[ErrorValidatingBasePackage]
          case other          => invalidBasePkg(other.toString).asLeft[Term.Select]
        }

    val invalidDomainPkg = invalidDomainPkgError(domainPackages) _
    val domainPkgsAttempt =
      domainPackages.toList
        .map(_.parse[Term].toEither)
        .traverse[Either[ErrorValidatingDomainPackages, *], Term.Select] {
          case Right(t: Term.Select) => t.asRight[ErrorValidatingDomainPackages]
          case Right(t)              => invalidDomainPkg(t.toString).asLeft[Term.Select]
          case Left(err)             => invalidDomainPkg(err.message).asLeft[Term.Select]
        }

    for {
      basePkg    <- basePackageAttempt
      domainPkgs <- domainPkgsAttempt
    } yield StorytellerConfig(targetFolder, storySuffix, basePkg, domainPkgs)
  }

}
