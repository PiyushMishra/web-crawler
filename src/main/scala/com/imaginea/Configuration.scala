package com.imaginea

import com.typesafe.config.ConfigFactory
import scala.util.control.Exception._
import scala.util.Either
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import scala.util.Failure

/*
 * set to default values in case of exception
 * */

object Configuration extends Logger {

  val defaultYear = 2014
  val defaultNumberOfCrawlers = 1
  val config = ConfigFactory.load

  def handleExceptions[T](body: Try[T], successBody:T=> T, failedBody: Throwable => T): T = {
    body match {
      case Success(value) => successBody(value)
      case Failure(ex)    => failedBody(ex)
    }
  }
  
  val crawlStorageFolder = handleExceptions[String](Try(config.getString("crawlStorageFolder")),
    t => t, ex =>
      {
        logger.info("Logging exception[" + ex.getMessage + "]")
        "/opt/data/crawl/root"
      })

  val numberOfCrawlers = handleExceptions[Int](Try(config.getString("numberOfCrawlers").toInt), t => t, ex =>
    {
      logger.info("Logging exception[" + ex.getMessage + "]")
      defaultNumberOfCrawlers
    })

  val yearForWhichMailNeedToBeDownloaded = handleExceptions[Int](Try(config.getString("year").toInt), t => t, ex =>
    {
      logger.info("Logging exception[" + ex.getMessage + "]")
      defaultYear
    })

  val folderWhereEmailsWouldBeDownloaded = handleExceptions[String](Try(config.getString("mailsDownlaodFolder")),
    t => t, ex =>
      {
        logger.info("Logging exception[" + ex.getMessage + "]")
        "/opt/mails/"
      })

}

