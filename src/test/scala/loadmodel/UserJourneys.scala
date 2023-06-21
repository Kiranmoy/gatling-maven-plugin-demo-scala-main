package loadmodel

// used for specifying durations with a unit, eg "5 minutes"
import scala.concurrent.duration._
import com.typesafe.scalalogging.Logger
import io.gatling.core.Predef.{doIf, exec, scenario}
import io.gatling.core.session
import io.gatling.core.session.Session
import io.gatling.core.structure.ScenarioBuilder
import loadmodel.pageobjects._
import org.slf4j.LoggerFactory

import scala.::

object UserJourneys {

  val logger: Logger = Logger(LoggerFactory.getLogger("UserJourneys"))

  val workloadScenario: ScenarioBuilder =

    scenario("Basic load test simulation")
      .exec(BaseHelpers.initSession)
      .pause(BaseHelpers.minPause)
      .exec(Home.loadAllProducts)
      .pause(BaseHelpers.minPause)
      .exec(Tables.addToCart)
      .pause(BaseHelpers.minPause, BaseHelpers.maxPause)
      .exec(Chairs.addToCart)
      .pause(BaseHelpers.minPause, BaseHelpers.maxPause)
      .exec(Cart.placeOrder)
      .pause(BaseHelpers.minPause)
      .exec(Checkout.checkout)
      .pause(BaseHelpers.maxPause)


}
