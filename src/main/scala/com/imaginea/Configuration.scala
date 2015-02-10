package com.imaginea

import com.typesafe.config.ConfigFactory
import scala.util.control.Exception._
import scala.util.Either
import scala.util.Failure
import scala.util.Success

/*
 * set to default values in case of exception
 * */

object Configuration extends Logger {

  val defaultYear = 2014
  val defaultNumberOfCrawlers = 1
  val config = ConfigFactory.load

  def handleExceptions[T](either: Either[Throwable, T], successBody: T => T, failedBody: Throwable => T): T = {
    either match {
      case Right(value) => successBody(value)
      case Left(ex)     => failedBody(ex)
    }
  }

  val crawlStorageFolder = handleExceptions[String](allCatch.either(config.getString("crawlStorageFolder")),
    t => t, ex =>
      {
        logger.info("Logging exception[" + ex.getMessage + "]")
        "/opt/data/crawl/root"
      })

  val numberOfCrawlers = handleExceptions[Int](allCatch.either(config.getString("numberOfCrawlers").toInt), t => t, ex =>
    {
      logger.info("Logging exception[" + ex.getMessage + "]")
      defaultNumberOfCrawlers
    })

  val yearForWhichMailNeedToBeDownloaded = handleExceptions[Int](allCatch.either(config.getString("year").toInt), t => t, ex =>
    {
      logger.info("Logging exception[" + ex.getMessage + "]")
      defaultYear
    })

  val folderWhereEmailsWouldBeDownloaded = handleExceptions[String](allCatch.either(config.getString("mailsDownlaodFolder")),
    t => t, ex =>
      {
        logger.info("Logging exception[" + ex.getMessage + "]")
        "/opt/mails/"
      })

}

