package com.imaginea

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.TextPage
import scala.collection.JavaConversions._
import java.io.FileWriter
import java.io.PrintWriter
import org.slf4j.LoggerFactory
import java.io.File

/**
 * @author piyushm
 * parser for html/javascript dynamic pages which uses htmlUnit library
 */

object HtmlParser extends Logger {

  val webClient = new WebClient

  val folderWhereEmailsWouldbeDownloaded = "/opt/mails"
  new File(folderWhereEmailsWouldbeDownloaded).mkdirs

  def downloadMailContent(url: String) = {
    logger.info("downloading mails from url :" + url)
    val page: HtmlPage = webClient.getPage(url)
    page.getAnchors.toList.filter(_.getHrefAttribute.startsWith("%")) foreach { a =>
      "subject" + a.getTextContent.replaceAll("//", ",")
      Writer.appendToFile("/opt/mails/" + "subject" + a.getTextContent.replaceAll("/", ",")  , a.click.asInstanceOf[HtmlPage].getBody.asText)
    }
  }
}

/**
 * utility for writing data into file
 */

object Writer {

  def using[A <: { def close(): Unit }, B](param: A)(f: A => B): B =
    try { f(param) } finally { param.close() }

  /**
   * this method append the data into files
   */

  def appendToFile(fileName: String, textData: String) =
    using(new FileWriter(fileName, true)) {
      fileWriter =>
        using(new PrintWriter(fileWriter)) {
          printWriter => printWriter.println(textData)
        }
    }

}