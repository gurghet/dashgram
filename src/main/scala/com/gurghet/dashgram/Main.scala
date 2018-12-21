package com.gurghet.dashgram

import com.softwaremill.sttp._
import io.circe._
import io.circe.parser._
import better.files._
import java.io.{File => JFile}
import java.time.ZoneId

import scala.annotation.tailrec
import scala.collection.parallel.Task
import scala.util.{Success, Try}

object Main {
  def main(args: Array[String]): Unit = {
    if (args.isDefinedAt(0)) {
      writeFollowersAt(args(0))
    } else {
      println("Please specify filename in which to write data")
    }

    def writeFollowersAt(filename: String): Unit = {
      val query = "suh_pig"
      val request = sttp.get(uri"https://www.instagram.com/web/search/topsearch/?query=$query")
      implicit val backend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend()
      val response = request.send()
      val body = response.unsafeBody
      val doc = parse(body).getOrElse(Json.Null)
      val followersCountOrMinusOne = doc
        .hcursor
        .downField("users")
        .downArray
        .first
        .downField("user")
        .downField("follower_count")
        .as[Int] match {
        case Right(followersCount) => followersCount.toString
        case Left(e) => e.message
      }
      val inDublin = ZoneId.of("Europe/Dublin")
      val timestamp = java.time.LocalDateTime.now(inDublin).toString
      val file = File(filename)
      withRetry (5){
        println(s"sleeping...")
        Thread.sleep(1000)
        println(s"trying...")
        file
          .appendLine()
          .append(s"$timestamp, $followersCountOrMinusOne")
      }
    }
  }

  @tailrec
  final def withRetry[T](retries: Int)(fn: => T): Try[T] = {
    Try(fn) match {
      case x: Success[T] => x
      case _ if retries > 1 => withRetry(retries - 1)(fn)
      case f => f
    }
  }
}
