package com.imaginea

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.TextPage
import scala.collection.JavaConversions._

/**
 * @author piyushm
 */

object HtmlCrawler extends App {

  val webClient = new WebClient

  def downloadMailContent(url: String) = {
	  val page: HtmlPage = webClient.getPage(url)
    page.getAnchors.toList.filter(_.getHrefAttribute.startsWith("%")) foreach { a =>
      println(a.click.asInstanceOf[HtmlPage].getBody.asText)
      println("############################################################")
    }
  }

  downloadMailContent("http://mail-archives.apache.org/mod_mbox/maven-users/201401.mbox/date?0")

}