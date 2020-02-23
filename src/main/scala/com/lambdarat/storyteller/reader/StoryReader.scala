package com.lambdarat.storyteller.reader

import java.net.URL
import java.util.jar.{JarEntry, JarFile}

import cats.data.NonEmptyList
import com.lambdarat.storyteller.core.StorytellerError
import com.lambdarat.storyteller.core.StorytellerError._
import com.lambdarat.storyteller.domain.Story
import com.lambdarat.storyteller.parser.StoryParser
import com.lambdarat.storyteller.utils.forzio._
import zio._

import scala.io.Source
import scala.jdk.CollectionConverters._

object StoryReader {

  def cleanJarPath(jarPath: URL): String =
    jarPath.getFile
      .replaceAll("file:", "")
      .replaceAll("\\.jar.*", ".jar")

  def storyFileEntriesFromJar(entries: List[JarEntry], folder: String): List[String] =
    entries
      .filter(entry => entry.getName.endsWith(".story") && entry.getName.startsWith(folder))
      .map(_.getName)

  def listStoryEntries(folder: String): Task[List[String]] =
    for {
      jarPath    <- Task(getClass.getResource(s"/$folder"))
      jarFile    <- Task(new JarFile(cleanJarPath(jarPath)))
      jarEntries <- Task(jarFile.entries())
    } yield storyFileEntriesFromJar(jarEntries.asScala.toList, folder)

  def parseStories(folder: String): IO[StorytellerError, NonEmptyList[Story]] =
    for {
      storyPaths <- listStoryEntries(folder).mapError(_ => FailureReadingResources)
      story      <- IO.foreach(storyPaths)(parseStory).flatMap(_.toNel(NonExistingStories))
    } yield story

  def parseStory(path: String): IO[StorytellerError, Story] =
    for {
      storyText <- IO(Source.fromResource(path).mkString) <> IO.fail(NonExistingStory(path))
      story     <- StoryParser.parse(storyText).result(storyText)
    } yield story

}
