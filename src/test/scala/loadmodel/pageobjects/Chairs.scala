package loadmodel.pageobjects

import com.typesafe.scalalogging.Logger
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import loadmodel.BaseHelpers
import org.slf4j.LoggerFactory

object Chairs {

  val logger: Logger = Logger(LoggerFactory.getLogger("Chairs"))

  val loadChairProducts: ChainBuilder =
    exec(BaseHelpers.cookies)
      .exec(http("Click 'Chairs' tab")
        .get("/chairs")
        .check(
          status.is(200),
          css("title").is("Chairs – Performance testing Essentials"),
          css("*[class*='entry-title']").is("Chairs"))
      ).pause(BaseHelpers.minPause)


  val openProduct: ChainBuilder =
    exec(loadChairProducts)
      .exec(BaseHelpers.cookies)
      .exec(http("Open random chair - #{chairName}")
        .get("/products/#{chairUrl}")
        .check(
          status.is(200),
          css("title").is("#{chairName} – Performance testing Essentials"),
          css("*[class*='entry-title']").is("#{chairName}"),
          css("td[class*='price-value']").exists.saveAs("chair-unit-price"))
      ).exec(session => {
      val chairUnitPrice = session("chair-unit-price").as[String]
      // Remove '$' character from the beginning of the extracted amount value
      session.set("chairUnitPrice", chairUnitPrice.substring(1).toDouble)
    }).exec(session => {
      val totalChairPrice = session("chairUnitPrice").as[Double] * session("chairQuantity").as[Int]
      session.set("totalChairPrice", totalChairPrice)
    }).exec(session => {
      val totalNetWorth = session("totalNetWorth").as[Double] + session("totalChairPrice").as[Double]
      session.set("totalNetWorth", totalNetWorth)
    }).exec(session => {
      logger.whenInfoEnabled {
        println("chairUnitPrice = " + session("chairUnitPrice").as[Double])
        println("chairQuantity = " + session("chairQuantity").as[Int])
        println("totalChairPrice = " + session("totalChairPrice").as[Double])
        println("totalNetWorth = " + session("totalNetWorth").as[Double])
      }
      session
    }).pause(BaseHelpers.minPause)


  val addToCart: ChainBuilder =
    exec(openProduct)
      .exec(BaseHelpers.cookies)
      .exec(http("Add chair to cart - #{chairId}: Qty=#{chairQuantity}")
        .post("/wp-admin/admin-ajax.php")
        .formParam("action", "ic_add_to_cart")
        .formParam("add_cart_data", "current_product=#{chairId}&cart_content={\"#{tableId}__\":#{tableQuantity}}&current_quantity=#{chairQuantity}")
        .formParam("cart_widget", "0")
        .formParam("cart_container", "0")
        .check(
          status.is(200),
          substring("cart-added-info").exists,
          substring("Added!").exists,
          substring("See your cart").exists)
      ).pause(BaseHelpers.minPause)


}
