package loadmodel.pageobjects

import com.typesafe.scalalogging.Logger
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import loadmodel.BaseHelpers
import org.slf4j.LoggerFactory

import java.text.NumberFormat
import java.util.Locale


object Cart {

  val logger: Logger = Logger(LoggerFactory.getLogger("Cart"))

  val openCart: ChainBuilder =
    exec(BaseHelpers.cookies)
      .exec(http("Open Cart")
        .get("/cart")
        .check(
          status.is(200),
          css("title").is("Cart – Performance testing Essentials"),
          css("*[class*='entry-title']").is("Cart"),
          css("[class*='td-name']").count.is(2),
          css("[href='http://localhost/products/#{tableUrl}']").is(session => session("tableName").as[String]),
          css("[data-p_id='#{tableId}']", "value").is(session => session("tableQuantity").as[String]),
          css("[data-p_id='#{tableId}']", "data-price").is(session => {
            val expected = session("tableUnitPrice").as[Double]
            // Format Number in standard form. Example: 100000 will be 1,00,000
            val formattedTablePrice = NumberFormat.getNumberInstance(Locale.ENGLISH).format(expected)
            logger.whenInfoEnabled {
              println("Formatted tablePrice = " + formattedTablePrice)
            }
            formattedTablePrice + ".00"
          }),
          css("[class*='#{tableId}_total product_total td-total']").is(session => {
            val expected = session("totalTablePrice").as[Double]
            // Format Number in standard form. Example: 100000 will be 1,00,000
            val formattedTotalTablePrice = NumberFormat.getNumberInstance(Locale.ENGLISH).format(expected)
            logger.whenInfoEnabled {
              println("Formatted totalTablePrice = " + formattedTotalTablePrice)
            }
            formattedTotalTablePrice + ".00"
          }),
          css("[href='http://localhost/products/#{chairUrl}']").is(session => session("chairName").as[String]),
          css("[data-p_id='#{chairId}']", "value").is(session => session("chairQuantity").as[String]),
          css("[data-p_id='#{chairId}']", "data-price").is(session => {
            val expected = session("chairUnitPrice").as[Double]
            // Format Number in standard form. Example: 100000 will be 1,00,000
            val formattedChairPrice = NumberFormat.getNumberInstance(Locale.ENGLISH).format(expected)
            logger.whenInfoEnabled {
              println("Formatted chairPrice = " + formattedChairPrice)
            }
            formattedChairPrice + ".00"
          }),
          css("[class*='#{chairId}_total product_total td-total']").is(session => {
            val expected = session("totalChairPrice").as[Double]
            // Format Number in standard form. Example: 100000 will be 1,00,000
            val formattedTotalChairPrice = NumberFormat.getNumberInstance(Locale.ENGLISH).format(expected)
            logger.whenInfoEnabled {
              println("Formatted totalChairPrice = " + formattedTotalChairPrice)
            }
            formattedTotalChairPrice + ".00"
          }),
          css("[class*='total_net']").is(session => {
            val expected = session("totalNetWorth").as[Double]
            // Format Number in standard form. Example: 100000 will be 1,00,000
            val formattedTotalNetWorth = NumberFormat.getNumberInstance(Locale.ENGLISH).format(expected)
            logger.whenInfoEnabled {
              println("Formatted totalNetWorth = " + formattedTotalNetWorth)
            }
            formattedTotalNetWorth + ".00"
          }),
          css("[name*='trans_id']", "value").exists.saveAs("trans-id")
        )
      ).exec(session => {
      val transId = session("trans-id").as[String].trim()
      logger.whenInfoEnabled {
        println("transId = " + transId)
      }
      session.set("transId", transId)
    }).pause(BaseHelpers.minPause)

  val placeOrder: ChainBuilder =
    exec(openCart)
      .exec(BaseHelpers.cookies)
      .exec(http("Click 'Place an order'")
        .post("/checkout")
        .formParam("cart_content", "%7B\"#{tableId}__\":#{tableQuantity},\"#{chairId}__\":#{chairQuantity}%7D")
        .formParam("p_id[]", "#{tableId}__")
        .formParam("p_quantity[]", "#{tableQuantity}")
        .formParam("p_id[]", "#{chairId}__")
        .formParam("p_quantity[]", "#{chairQuantity}")
        .formParam("total_net", "#{totalNetWorth}")
        .formParam("trans_id", "#{transId}")
        .formParam("shipping", "order")
        .check(
          status.is(200),
          css("title").is("Checkout – Performance testing Essentials"),
          css("*[class*='entry-title']").is("Checkout"))
      ).pause(BaseHelpers.minPause)


}
