package com.imaginea

import com.typesafe.config.ConfigFactory

object Configuration {
  val config = ConfigFactory.load
  val crawlStorageFolder = config.getString("crawlStorageFolder")
  val numberOfCrawlers = config.getString("numberOfCrawlers").toInt
  val yearForWhichMailNeedToBeDownloaded = config.getString("year")
  val folderWhereEmailsWouldBeDownloaded = config.getString("mailsDownlaodFolder")
}