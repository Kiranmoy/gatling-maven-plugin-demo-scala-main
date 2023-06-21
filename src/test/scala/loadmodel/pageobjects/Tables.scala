package loadmodel.pageobjects

import com.typesafe.scalalogging.Logger
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import loadmodel.BaseHelpers
import org.slf4j.LoggerFactory


object Tables {

  val logger: Logger = Logger(LoggerFactory.getLogger("Tables"))

  val loadTableProducts: ChainBuilder =
    exec(BaseHelpers.cookies)
      .exec(http("Navigate to 'Tables' tab")
        .get("/tables")
        .check(
          status.is(200),
          css("title").is("Tables – Performance testing Essentials"),
          css("*[class*='entry-title']").is("Tables"))
      ).pause(BaseHelpers.minPause)


  val openProduct: ChainBuilder =
    exec(loadTableProducts)
      .exec(BaseHelpers.cookies)
      .exec(http("Open a table product cart (click on a table) - #{tableName}")
        .get("/products/#{tableUrl}")
        .check(
          status.is(200),
          css("title").is("#{tableName} – Performance testing Essentials"),
          css("*[class*='entry-title']").is("#{tableName}"),
          css("[class*='price-value']").exists.saveAs("table-unit-price"))
      ).exec(session => {
      logger.whenInfoEnabled {
        println("table-unit-price value =" + session("table-unit-price").as[String])
      }
      session
    }).exec(session => {
      val tableUnitPrice = session("table-unit-price").as[String]
      // Remove '$' character from the beginning of the extracted amount value
      session.set("tableUnitPrice", tableUnitPrice.substring(1).toDouble)
    }).exec(session => {
      val totalTablePrice = session("tableUnitPrice").as[Double] * session("tableQuantity").as[Int]
      session.set("totalTablePrice", totalTablePrice)
    }).exec(session => {
      val totalNetWorth = session("totalNetWorth").as[Double] + session("totalTablePrice").as[Double]
      session.set("totalNetWorth", totalNetWorth)
    }).exec(session => {
      logger.whenInfoEnabled {
        println("tableUnitPrice = " + session("tableUnitPrice").as[Double])
        println("tableQuantity = " + session("tableQuantity").as[Int])
        println("totalTablePrice = " + session("totalTablePrice").as[Double])
        println("totalNetWorth = " + session("totalNetWorth").as[Double])
      }
      session
    }).pause(BaseHelpers.minPause)


  val addToCart: ChainBuilder =
    exec(openProduct)
      .exec(BaseHelpers.cookies)
      .exec(http("Add table to Cart (click 'Add to Cart' button) - #{tableId}: Qty=#{tableQuantity}")
        .post("/wp-admin/admin-ajax.php")
        .formParam("action", "ic_add_to_cart")
        .formParam("add_cart_data", "current_product=#{tableId}&cart_content=&current_quantity=#{tableQuantity}")
        .formParam("cart_widget", "0")
        .formParam("cart_container", "0")
        .check(
          status.is(200),
          substring("cart-added-info").exists,
          substring("Added!").exists,
          substring("See your cart").exists)
      ).pause(BaseHelpers.minPause)


}
