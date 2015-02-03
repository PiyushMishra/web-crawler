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

class Crawler extends WebCrawler {

  val contentTobefilterd = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
    + "|png|tiff?|mid|mp2|mp3|mp4"
    + "|wav|avi|mov|mpeg|ram|m4v|pdf"
    + "|rm|smil|wmv|swf|wma|zip|rar|gz))$")

  @Override def shouldVisitPage(url: WebURL): Boolean = {
    val href = url.getURL().toLowerCase()
    !contentTobefilterd.matcher(href).matches() && href.startsWith("http://www.ics.uci.edu/");
  }

  override def visit(page: Page) {
    val url = page.getWebURL().getURL()
    println("URL: " + url)

    page.getParseData match {

      case htmlParseData: HtmlParseData =>
        val text = htmlParseData.getText
        val html = htmlParseData.getHtml()
        val links = htmlParseData.getOutgoingUrls()
        println("Text length: " + text.length())
        println("Html length: " + html.length())
        println("Number of outgoing links: " + links.size())

      case _ => // do nothing

    }
  }
}

object CrawlerApp extends App {

  val crawlStorageFolder = "/opt/data/crawl/root"
  val numberOfCrawlers = 1

  val config = new CrawlConfig();
  config.setCrawlStorageFolder(crawlStorageFolder)

  val pageFetcher = new PageFetcher(config)
  val robotstxtConfig = new RobotstxtConfig()
  val robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher)
  val controller = new CrawlController(config, pageFetcher, robotstxtServer)

  controller.addSeed("http://www.ics.uci.edu/~welling/")
  controller.addSeed("http://www.ics.uci.edu/~lopes/")
  controller.addSeed("http://www.ics.uci.edu/")

  controller.start(classOf[Crawler], numberOfCrawlers)

}
