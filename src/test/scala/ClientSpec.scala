package test

import org.specs2.mutable._
import scala.concurrent.Await
import scala.concurrent.duration._
import wabisabi._

class ClientSpec extends Specification {

  sequential

  "Client" should {
    "index and fetch a document" in {
      val client = new Client("http://localhost:9200")

      Await.result(client.index(
        index = "foo", `type` = "foo", id = Some("foo"),
        data = "{\"foo\":\"bar\"}", refresh = true
      ), Duration(1, "second")) match {
        case Left(x) => failure("Failed to index: " + x.getMessage)
        case Right(body) => body must contain("\"_version\"")
      }

      Await.result(client.get("foo", "foo", "foo"), Duration(1, "second")) match {
        case Left(x) => failure("Failed to get: " + x.getMessage)
        case Right(body) => body must contain("\"foo\"")
      }

      Await.result(client.delete("foo", "foo", "foo"), Duration(1, "second")) match {
        case Left(x) => failure("Failed to delete: " + x.getMessage)
        case Right(body) => body must contain("\"found\"")
      }

      1 must beEqualTo(1)
    }

    "index and search for a document" in {
      val client = new Client("http://localhost:9200")

      Await.result(client.index(
        index = "foo", `type` = "foo", id = Some("foo2"),
        data = "{\"foo\":\"bar\"}", refresh = true
      ), Duration(1, "second")) match {
        case Left(x) => failure("Failed to index: " + x.getMessage)
        case Right(body) => body must contain("\"_version\"")
      }

      Await.result(client.search("foo", "{\"query\": { \"match_all\": {} }"), Duration(1, "second")) match {
        case Left(x) => failure("Failed to search: " + x.getMessage)
        case Right(body) => body must contain("\"foo2\"")
      }

      Await.result(client.delete("foo", "foo", "foo2"), Duration(1, "second")) match {
        case Left(x) => failure("Failed to delete: " + x.getMessage)
        case Right(body) => body must contain("\"found\"")
      }

      1 must beEqualTo(1)
    }
  }
}