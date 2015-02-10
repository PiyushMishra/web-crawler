package com.imaginea

import java.util.regex.Pattern

import Aggregator.actors
import Configuration._
import akka.actor.actorRef2Scala
import edu.uci.ics.crawler4j.crawler.{ CrawlConfig, CrawlController, Page, WebCrawler }
import edu.uci.ics.crawler4j.fetcher.PageFetcher
import edu.uci.ics.crawler4j.robotstxt.{ RobotstxtConfig, RobotstxtServer }
import edu.uci.ics.crawler4j.url.WebURL

/**
 * author piyushm
 *
 */

class Crawler extends WebCrawler with Logger {

  /*
   *  content which is not required to visit
   */

  val contentNotToVisit = Pattern.compile(".*(\\.(bmp|gif|jpe?g"
    + "|png|tiff?|mid|mp2|mp3|mp4"
    + "|wav|avi|mov|mpeg|ram|m4v|pdf"
    + "|rm|smil|wmv|swf|wma|zip|rar|gz))$")

  /**
   *  visit specific url's which start with like "http://mail-archives.apache.org/mod_mbox/maven-users/2014"
   *  and contains mbox/thread as part of url
   */

  override def shouldVisit(page: Page, url: WebURL): Boolean = {
    val href = url.getURL().toLowerCase()
    !contentNotToVisit.matcher(href).matches() &&
      href.startsWith("http://mail-archives.apache.org/mod_mbox/maven-users/" + yearForWhichMailNeedToBeDownloaded) &&
      href.contains("mbox/thread")
  }

  /**
   * visit pages and download mails
   */

  override def visit(page: Page) {
    val url = page.getWebURL().getURL()
    logger.debug("visiting url  " + url)
    actors ! (url)
  }
}

/**
 *  application startup point
 *  It crawls the web and fetch url's recursively
 */

object CrawlerApp extends App with FolderManager with Logger {
  val config = new CrawlConfig()
  config.setCrawlStorageFolder(crawlStorageFolder)
  createFolder(crawlStorageFolder)
  config.setMaxDepthOfCrawling(-1)
  config.setMaxPagesToFetch(-1)
  val pageFetcher = new PageFetcher(config)
  val robotstxtConfig = new RobotstxtConfig()
  val robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher)
  val controller = new CrawlController(config, pageFetcher, robotstxtServer)

  controller.addSeed("http://mail-archives.apache.org/mod_mbox/maven-users/")

  controller.startNonBlocking(classOf[Crawler], numberOfCrawlers)

  logger.info("downloading mails, please check in " + folderWhereEmailsWouldBeDownloaded)

}
