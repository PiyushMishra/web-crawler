package com.imaginea

import java.io.File

import org.scalatest.BeforeAndAfterAll
import org.scalatest.Matchers
import org.scalatest.WordSpecLike

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage

import akka.actor.ActorSystem
import akka.testkit.ImplicitSender
import akka.testkit.TestActorRef
import akka.testkit.TestKit

import Configuration._

class MailDownloaderSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  val webClient = new WebClient
  val url = "http://mail-archives.apache.org/mod_mbox/maven-users/201401.mbox/thread?1"
  val anchor = webClient.getPage(url).asInstanceOf[HtmlPage].getAnchors.get(10)
  val monthFolder = Configuration.folderWhereEmailsWouldBeDownloaded + "201401"

  val mailDownloaderRef = TestActorRef[MailDownloder]
  val mailDownloader = mailDownloaderRef.underlyingActor

  def this() = this(ActorSystem("DownloadingSpec"))

  override def beforeAll {
    (new File(monthFolder + "/" + anchor.getTextContent.replaceAll("/", ","))).delete()
    new File(folderWhereEmailsWouldBeDownloaded).delete()
  }

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A MailDownloader actor" must {
    "download a file corresponding to an anchor in a folder" in {
      new File(monthFolder).mkdirs
      mailDownloader.receive(DownloadMail(anchor, "201401"))
      assert(new File(monthFolder + "/" + anchor.getTextContent.replaceAll("/", ",")).exists)
    }
  }

}
