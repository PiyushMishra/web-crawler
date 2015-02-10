package com.imaginea

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.mutable.Map
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.{ HtmlAnchor, HtmlPage }
import Configuration.{ folderWhereEmailsWouldBeDownloaded, yearForWhichMailNeedToBeDownloaded }
import akka.actor.{ Actor, ActorSystem, Props, actorRef2Scala }
import akka.routing.RoundRobinPool
import scala.util.control.Exception._
import scala.collection.mutable.Buffer

class Aggregator extends Actor with FolderManager with Logger {

  val webClient = new WebClient
  val mailDownloadingActor = context.actorOf(RoundRobinPool(Runtime.getRuntime.availableProcessors() * 2).
    props(Props[MailDownloder]), "downloder")

  def receive: PartialFunction[Any, Unit] = {
    case url: String if (url.contains("?")) =>

      val month = findMonthFromUrl(url)

      createFolder(folderWhereEmailsWouldBeDownloaded + month)

      logger.info("downloading mails for : [" + month + "]")

      findAnchors(url).foreach { anchors =>
        anchors foreach { anchor =>
          mailDownloadingActor.tell(DownloadMail(anchor, month), sender)
        }
      }
  }

  /**
   * find month for which mails are getting downloaded
   */
  def findMonthFromUrl(url: String): String = {
    val index = url.indexOf(yearForWhichMailNeedToBeDownloaded)
    url.substring(index, index + 6)
  }

  /**
   * find anchors in a url
   */
  def findAnchors(url: String): Option[Buffer[HtmlAnchor]] = {
    import scala.collection.JavaConversions._
    allCatch.opt {
      val page: HtmlPage = webClient.getPage(url)
      page.getAnchors.filter(_.getHrefAttribute.startsWith("%"))
    }
  }

}

object Aggregator {
  val actorSystem = ActorSystem("mailDownloder")
  val actors = actorSystem.actorOf(Props[Aggregator], "aggregator")
}





