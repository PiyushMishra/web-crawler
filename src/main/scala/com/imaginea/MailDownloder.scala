package com.imaginea

import scala.collection.JavaConversions.asScalaBuffer
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.typesafe.config.ConfigFactory
import CrawlerAndParserConfig._
import akka.actor.Actor
import com.gargoylesoftware.htmlunit.html.HtmlAnchor

/**
 * @author piyushm
 * parser for html/javascript dynamic pages which uses htmlUnit library
 */

class MailDownloder extends Actor with FolderManager with FileContentAppender with Logger {
  var mailCount = 0
  createFolder(folderWhereEmailsWouldBeDownloaded)

  def receive = {
    case DownloadMails(url, month) => downloadMailContent(url, month)
  }

  /**
   * method for downloading mails and then write them into flat file
   */
  def downloadMailContent(anchor: HtmlAnchor, month: String) {
    val MailSubject = anchor.getTextContent.replaceAll("/", ",")
    val page = anchor.click.asInstanceOf[HtmlPage]
    logger.info("[Subject" + MailSubject + "]")
    val pageBody = page.getBody
    if (Option(pageBody) != None)
      appendToFile(folderWhereEmailsWouldBeDownloaded + month + "/" + MailSubject,
       pageBody.asText) else  appendToFile(folderWhereEmailsWouldBeDownloaded + month + "/" + MailSubject,
       "\n")
  }
}

object CrawlerAndParserConfig {
  val config = ConfigFactory.load
  val crawlStorageFolder = config.getString("crawlStorageFolder")
  val numberOfCrawlers = config.getInt("numberOfCrawlers")
  val yearForWhichMailNeedToBeDownloaded = config.getString("year")
  val folderWhereEmailsWouldBeDownloaded = config.getString("mailsDownlaodFolder")
}

case class DownloadMails(url: HtmlAnchor, month: String)
