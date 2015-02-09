package com.imaginea

import org.scalatest.BeforeAndAfterAll
import org.scalatest.Matchers
import org.scalatest.WordSpecLike
import akka.actor.ActorSystem
import akka.actor.actorRef2Scala
import akka.testkit.ImplicitSender
import akka.testkit.TestActors
import akka.testkit.TestKit
import akka.testkit.TestActorRef
import java.io.File

class AggregatorSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("MySpec"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val url = "http://mail-archives.apache.org/mod_mbox/maven-users/201401.mbox/thread"
  val aggregatorRef = TestActorRef[Aggregator]
  val aggregator = aggregatorRef.underlyingActor

  "An Aggregator actor" must {

    "retrive anchors in a url" in {
      assert(aggregator.findAnchors(url, "201401").get.size === 267)
    }

    "extract the month from url" in {
      assert(aggregator.findMonthFromUrl(url) === "201401")
    }

    "download a file corresponding to an anchor in a folder" in {
      val anchors = aggregator.findAnchors(url, "201401").get
      assert(anchors.size !== 0)
      aggregator.receive(url)
      val monthFolder = CrawlerAndParserConfig.folderWhereEmailsWouldBeDownloaded + "201401"
      assert(new File(monthFolder).exists)
      Thread.sleep(10000)
      assert(new File(monthFolder + "/" + anchors.head.getTextContent).exists())
    }

  }

}
