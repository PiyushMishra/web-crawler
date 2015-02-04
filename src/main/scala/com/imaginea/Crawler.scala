package com.imaginea

import java.util.regex.Pattern
import edu.uci.ics.crawler4j.crawler.WebCrawler
import edu.uci.ics.crawler4j.url.WebURL
import edu.uci.ics.crawler4j.crawler.Page
import edu.uci.ics.crawler4j.parser.HtmlParseData
import edu.uci.ics.crawler4j.crawler.CrawlConfig
import edu.uci.ics.crawler4j.fetcher.PageFetcher
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer
import edu.uci.ics.crawler4j.crawler.CrawlController
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig
import scala.collection.JavaConversions._
import org.apache.http.HttpEntity

class Crawler extends WebCrawler {

  val contentTobefilterd = Pattern.compile(".*(\\.(bmp|gif|jpe?g"
    + "|png|tiff?|mid|mp2|mp3|mp4"
    + "|wav|avi|mov|mpeg|ram|m4v|pdf"
    + "|rm|smil|wmv|swf|wma|zip|rar|gz))$")

  override def shouldVisit(page: Page, url: WebURL): Boolean = {
    val href = url.getURL().toLowerCase()
    !contentTobefilterd.matcher(href).matches() &&
      href.startsWith("http://mail-archives.apache.org/mod_mbox/maven-users/2014") && href.contains("mbox/thread")
  }

  override def visit(page: Page) {
    val url = page.getWebURL().getURL()
    println("URL: " + url)
    page.getParseData match {
      case htmlParseData: HtmlParseData =>
        val text = htmlParseData.getText
        val html = htmlParseData.getHtml()
        print(html)
        val links = htmlParseData.getOutgoingUrls()
//        println("Number of outgoing links: " + links)

      case _ => // do nothing

    }
  }
}

object CrawlerApp extends App {

  val crawlStorageFolder = "/opt/data/crawl/root"
  val numberOfCrawlers = 1

  val config = new CrawlConfig();
  config.setCrawlStorageFolder(crawlStorageFolder)
  config.setMaxDepthOfCrawling(-1)
  config.setMaxPagesToFetch(-1)
  config.setMaxPagesToFetch(-1)
  //  config.setResumableCrawling(true)
  config.setMaxPagesToFetch(-1)
  val pageFetcher = new PageFetcher(config)
  val robotstxtConfig = new RobotstxtConfig()
  val robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher)
  val controller = new CrawlController(config, pageFetcher, robotstxtServer)

  controller.addSeed("http://mail-archives.apache.org/mod_mbox/maven-users/")

  controller.start(classOf[Crawler], numberOfCrawlers)

}
