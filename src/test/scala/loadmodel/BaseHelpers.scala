package loadmodel

import com.typesafe.scalalogging.Logger
import io.gatling.core.Predef._
// used for specifying durations with a unit, eg "5 minutes"
import scala.concurrent.duration._
import io.gatling.core.feeder.BatchableFeederBuilder
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import org.slf4j.LoggerFactory

import scala.concurrent.duration.FiniteDuration

object BaseHelpers {

  val logger: Logger = Logger(LoggerFactory.getLogger("BaseHelpers"))

  val domain = "localhost"

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("http://" + domain)

  val minPause: FiniteDuration = 500.milliseconds

  val maxPause: FiniteDuration = 1000.milliseconds

  val tableFeeder: BatchableFeederBuilder[String] =
    csv("loadmodel/tables.csv").random

  val chairFeeder: BatchableFeederBuilder[String] =
    csv("loadmodel/chairs.csv").random

  val checkoutDetailsFeeder: BatchableFeederBuilder[String] =
    csv("loadmodel/user-checkout-details.csv").random

  // Initializing parameters required for request creation and assertions
  val globalParametersInitialization: ChainBuilder =
    exec(session => {
      session.set("tableUnitPrice", 0.00)
    }).exec(session => {
      session.set("chairUnitPrice", 0.00)
    }).exec(session => {
      session.set("totalTablePrice", 0.00)
    }).exec(session => {
      session.set("totalChairPrice", 0.00)
    }).exec(session => {
      session.set("totalNetWorth", 0.00)
    }).exec(session => {
      session.set("transId", BigInt(0))
    })

  // Loading Tables csv feeder data for the session
  val tableParametersInitialization: ChainBuilder =
    feed(tableFeeder)
      .exec(session => {
        val tableId = session("table-id").as[Int]
        session.set("tableId", tableId)
      }).exec(session => {
      val tableName = session("table-name").as[String]
      session.set("tableName", tableName)
    }).exec(session => {
      val tableQuantity = session("table-quantity").as[Int]
      session.set("tableQuantity", tableQuantity)
    }).exec(session => {
      val tableUrl = session("table-url").as[String]
      session.set("tableUrl", tableUrl)
    })

  // Loading Chair csv feeder data for the session
  val chairParametersInitialization: ChainBuilder =
    feed(chairFeeder)
      .exec(session => {
        val chairId = session("chair-id").as[Int]
        session.set("chairId", chairId)
      }).exec(session => {
      val chairName = session("chair-name").as[String]
      session.set("chairName", chairName)
    }).exec(session => {
      val chairQuantity = session("chair-quantity").as[Int]
      session.set("chairQuantity", chairQuantity)
    }).exec(session => {
      val chairUrl = session("chair-url").as[String]
      session.set("chairUrl", chairUrl)
    })

  // Logging all available session parameters that are generated as part of initialization process
  val logAllSessionParameters: ChainBuilder =
    exec(session => {
      logger.whenInfoEnabled {
        logger.debug("tableId = " + session("tableId").as[Int])
        logger.debug("tableName = " + session("tableName").as[String])
        logger.debug("tableQuantity = " + session("tableQuantity").as[Int])
        logger.debug("tableUrl = " + session("tableUrl").as[String])
        logger.debug("chairId = " + session("chairId").as[Int])
        logger.debug("chairName = " + session("chairName").as[String])
        logger.debug("chairQuantity = " + session("chairQuantity").as[Int])
        logger.debug("chairUrl = " + session("chairUrl").as[String])
        logger.debug("tableUnitPrice = " + session("tableUnitPrice").as[Double])
        logger.debug("chairUnitPrice = " + session("chairUnitPrice").as[Double])
        logger.debug("totalTablePrice = " + session("totalTablePrice").as[Double])
        logger.debug("totalChairPrice = " + session("totalChairPrice").as[Double])
        logger.debug("totalNetWorth = " + session("totalNetWorth").as[Double])
        logger.debug("chairQuantity = " + session("chairQuantity").as[Int])
        logger.debug("transId = " + session("transId").as[BigInt])
      }
      session
    })

  val initSession: ChainBuilder =
    exec(flushCookieJar)
      .exec(flushHttpCache)
      .exec(globalParametersInitialization)
      .exec(tableParametersInitialization)
      .exec(chairParametersInitialization)
      .exec(logAllSessionParameters)

  val cookies: ChainBuilder =
    exec(addCookie(Cookie("#{cookieName}", "#{cookieValue}").withDomain(domain)))

  def getProperty(propertyName:String, defaultValue:String): String = {
    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }

}
