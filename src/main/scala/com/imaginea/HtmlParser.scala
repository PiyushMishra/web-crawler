package com.imaginea

import scala.collection.JavaConversions.asScalaBuffer
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.typesafe.config.ConfigFactory
import CrawlerAndParserConfig._
import MailContentCache._

/**
 * @author piyushm
 * parser for html/javascript dynamic pages which uses htmlUnit library
 */

object HtmlParser extends FolderManager with FileContentAppender with Logger {
  var i = 0
  createFolder(folderWhereEmailsWouldBeDownloaded)

  val webClient = new WebClient

  /**
   * find month for which mails are getting downloaded
   */
  def findMonthFromUrl(url: String) = {
    val index = url.indexOf(yearForWhichMailNeedToBeDownloaded)
    url.substring(index, index + 6)
  }

  /**
   * method for downloading mails and then write them into flat file
   */
  def downloadMailContent(url: String) {
    val page: HtmlPage = webClient.getPage(url)
    val month = findMonthFromUrl(url)
    createFolder(folderWhereEmailsWouldBeDownloaded + month)
    logger.info("downloading mails from url : [" + url + "]")
    page.getAnchors.toList.filter(_.getHrefAttribute.startsWith("%")) foreach { anchor =>
      i = i + 1
      val MailSubject = anchor.getTextContent.replaceAll("/", ",")
      logger.info("subject [" + MailSubject + "]" + i)
      storeInMap(MailSubject, anchor.click.asInstanceOf[HtmlPage].getBody.asText)
    }

    //    store foreach {
    //      case (subject, content) =>
    //        appendToFile(folderWhereEmailsWouldBeDownloaded + month + "/subject : " + subject,
    //          content)
    //    }

  }
}

object CrawlerAndParserConfig {
  val config = ConfigFactory.load
  val crawlStorageFolder = config.getString("crawlStorageFolder")
  val numberOfCrawlers = config.getInt("numberOfCrawlers")
  val yearForWhichMailNeedToBeDownloaded = config.getString("year")
  val folderWhereEmailsWouldBeDownloaded = config.getString("mailsDownlaodFolder")
}

/**
 * store data in memory before writing to file system
 */

object MailContentCache {
  import scala.collection.mutable.Map

  val store: Map[String, String] = Map[String, String]()

  def storeInMap(subject: String, content: String) = if (store.contains(subject)) {
    store.put(subject, store.get(subject) + "\n\n" + content)
  } else {
    store.put(subject, content)

  }
}
