package com.imaginea

import akka.actor.Actor
import com.gargoylesoftware.htmlunit.html.HtmlPage
import CrawlerAndParserConfig._
import com.gargoylesoftware.htmlunit.WebClient
import scala.collection.mutable.Map
import com.gargoylesoftware.htmlunit.html.HtmlAnchor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.routing.RoundRobinPool

class Aggregator extends Actor with FolderManager with Logger {
  var urls = List[String]()
  val webClient = new WebClient
  var monthWiseMails = Map[String, List[HtmlAnchor]]()
  val actors = context.actorOf(RoundRobinPool(4).props(Props[MailDownloder]), "downloder")

  def receive = {
    case url: String =>
      if (url.contains("?")) {
        val month = findMonthFromUrl(url)

        createFolder(folderWhereEmailsWouldBeDownloaded)

        val anchors = findAnchors(url, month)
        if(anchors.isDefined) anchors.get foreach { anchor => actors ! DownloadMails(anchor, month) }
      }
  }

  /**
   * find month for which mails are getting downloaded
   */
  def findMonthFromUrl(url: String) = {
    val index = url.indexOf(yearForWhichMailNeedToBeDownloaded)
    url.substring(index, index + 6)
  }

  def findAnchors(url: String, month: String) = {
    try {
      import scala.collection.JavaConversions._
      val page: HtmlPage = webClient.getPage(url)
      createFolder(folderWhereEmailsWouldBeDownloaded + month)
      logger.info("downloading mails for : [" + month + "]")
      val anchors = page.getAnchors
      if (Option(anchors) != None) Some(anchors.toList.filter(_.getHrefAttribute.startsWith("%"))) else None
    } catch {
      case ex: Exception => None
    }
  }

}

object Aggregator {
  val actorSystem = ActorSystem("mailDownloder")
  val actors = actorSystem.actorOf(Props[Aggregator], "aggregator")
}





