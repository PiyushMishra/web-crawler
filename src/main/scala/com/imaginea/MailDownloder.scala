package com.imaginea

import scala.collection.JavaConversions.asScalaBuffer
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.typesafe.config.ConfigFactory
import Configuration._
import akka.actor.Actor
import com.gargoylesoftware.htmlunit.html.HtmlAnchor

/**
 * @author piyushm
 * parser for html/javascript dynamic pages which uses htmlUnit library
 */

class MailDownloder extends Actor with FolderManager with FileContentAppender with Logger {

  def receive = {
    case DownloadMail(url, month) => downloadMailContent(url, month)
  }

  /**
   * method for downloading mails and then write them into flat file
   */
  def downloadMailContent(anchor: HtmlAnchor, month: String): Unit = {
    val (mailSubject, pageBody) = (anchor.getTextContent.replaceAll("/", ","), anchor.click.asInstanceOf[HtmlPage].getBody)
    logger.info("[Subject" + mailSubject + "]")
    Option(pageBody) foreach { body => appendToFile(folderWhereEmailsWouldBeDownloaded + month + "/" + mailSubject, body.asText) }
  }
  
}

case class DownloadMail(url: HtmlAnchor, month: String)
