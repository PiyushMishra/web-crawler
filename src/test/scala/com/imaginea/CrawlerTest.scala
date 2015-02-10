package com.imaginea

import org.scalatest.FunSuite
import edu.uci.ics.crawler4j.crawler.Page
import edu.uci.ics.crawler4j.url.WebURL

class CrawlerTest extends FunSuite {

  val crawler = new Crawler

  val webUrl = new WebURL()
  webUrl.setURL("http://mail-archives.apache.org/mod_mbox/maven-users/201401.mbox/thread?1")
  val page = new Page(webUrl)

  test("""crawler should visit pages which contains "thread" keyword""") {
    val testWebUrl = new WebURL()
    testWebUrl.setURL("http://mail-archives.apache.org/mod_mbox/maven-users/201401.mbox/thread?2")
    assert(crawler.shouldVisit(page, webUrl))
  }

  test("""crawler should not visit pages which does not contains "thread" keyword""") {
    val testWebUrl = new WebURL()
    testWebUrl.setURL("http://mail-archives.apache.org/mod_mbox/maven-users/201401.mbox/browser")
    assert(!crawler.shouldVisit(page, testWebUrl))
  }

}