package com.imaginea

import org.scalatest.FunSuite
import Configuration._

class ConfigurationTest extends FunSuite {

  test("check if configuration is being read correctly") {
    assert(crawlStorageFolder === "src/test/resources/data/crawl/root")
    assert(numberOfCrawlers === 1)
    assert(yearForWhichMailNeedToBeDownloaded === "2014")
    assert(folderWhereEmailsWouldBeDownloaded === "src/test/resources/mails/")
  }

}