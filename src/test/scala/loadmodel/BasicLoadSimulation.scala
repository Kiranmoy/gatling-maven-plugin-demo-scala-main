package loadmodel

import io.gatling.core.Predef._

import scala.annotation.switch
// used for specifying durations with a unit, eg "5 minutes"
import scala.concurrent.duration._

class BasicLoadSimulation extends Simulation {

  def userCount:Int = BaseHelpers.getProperty("USERS", "5").toInt
  def rampDuration:Int = BaseHelpers.getProperty("RAMP_DURATION", "10").toInt

  def workloadModel: Char = BaseHelpers.getProperty("WORKLOAD_MODEL", "X")
                              .toUpperCase.toCharArray.headOption.get

  (workloadModel: @switch) match {

    case 'O' => {
      // Open Workload model
      setUp(UserJourneys.workloadScenario.inject(
        rampUsers(userCount).during(rampDuration.seconds)
      )).protocols(BaseHelpers.httpProtocol)
    }

    case 'C' => {
      // Closed Workload Model
      setUp(UserJourneys.workloadScenario.inject(
        rampConcurrentUsers(0).to(userCount).during(rampDuration.seconds)
      )).protocols(BaseHelpers.httpProtocol)
    }

    case _ => {
      // Open Workload model ( DEFAULT WORKLOAD MODEL)
      setUp(UserJourneys.workloadScenario.inject(
        atOnceUsers(userCount)
      )).protocols(BaseHelpers.httpProtocol)
    }


  }





}
