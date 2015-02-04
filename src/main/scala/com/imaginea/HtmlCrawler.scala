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

  val page: HtmlPage = (webClient.getPage("http://mail-archives.apache.org/mod_mbox/maven-users/201401.mbox/date?0"))

  println(page.getAnchors.toList.filter(_.getHrefAttribute.startsWith("%")))

  page.getAnchors.toList.filter(_.getHrefAttribute.startsWith("%")) foreach { a =>
    println(a.click.asInstanceOf[HtmlPage].getBody.asText)
    println("############################################################")
  }

  val d = page.getBody.getChildElements.toList(2).getChildElements.toList(1).
    getHtmlElementDescendants.toList
  //  println(d)

  val a = page.getBody.getChildElements.toList(2).getChildElements.toList(1).
    getHtmlElementDescendants.toList(3).click().asInstanceOf[HtmlPage]

  //  println(a.getBody.asText())

}