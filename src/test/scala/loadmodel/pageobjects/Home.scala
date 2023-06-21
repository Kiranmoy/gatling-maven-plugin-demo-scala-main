package loadmodel.pageobjects

import com.typesafe.scalalogging.Logger
import io.gatling.core.Predef.{css, _}
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import loadmodel.BaseHelpers
import org.slf4j.LoggerFactory

object Home {

  val logger: Logger = Logger(LoggerFactory.getLogger("Home"))

  val loadAllProducts: ChainBuilder =
    exec(http("Open the application")
      .get("/")
      .check(
        status.is(200),
        css("title").is("Performance testing Essentials"),
        css("*[class*='entry-title']").is("All Products"),
        header("Set-Cookie").exists.saveAs("cookie"))
    ).exec(session => {
      // Extract the cookie name and value pair
      val cookie = session("cookie").as[String].split(';')(0)
      // Decode encoded characters in the cookie name
      val cookieName: String = cookie.trim.split("=")(0).replaceAll("%7C", "|")
      logger.whenInfoEnabled {
        println("cookieName = " + cookieName)
      }
      session.set("cookieName", cookieName)
    }).exec(session => {
      // Extract the cookie name and value pair
      val cookie = session("cookie").as[String].split(';')(0)
      // Decode encoded characters in the cookie value
      val cookieValue: String = cookie.trim.split("=")(1).replaceAll("%7C", "|")
      logger.whenInfoEnabled {
        println("cookieValue = " + cookieValue)
      }
      session.set("cookieValue", cookieValue)
    }).pause(BaseHelpers.minPause)

}
