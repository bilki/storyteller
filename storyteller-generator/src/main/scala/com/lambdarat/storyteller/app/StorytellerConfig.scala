package com.lambdarat.storyteller.app

import java.io.File

case class StorytellerConfig(
    targetFolder: File,
    storySuffix: String,
    basePackage: String,
    domainPackages: Seq[String]
)
