package loadmodel.pageobjects

import com.typesafe.scalalogging.Logger
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import loadmodel.BaseHelpers
import org.slf4j.LoggerFactory

object Checkout {

  val logger: Logger = Logger(LoggerFactory.getLogger("Checkout"))

  val fetchBillingCountryStates: ChainBuilder =
    exec(BaseHelpers.cookies)
      .feed(BaseHelpers.checkoutDetailsFeeder)
      .exec(http("Fetch States based on Country")
        .post("/wp-admin/admin-ajax.php")
        .formParam("action", "ic_state_dropdown")
        .formParam("country_code", "#{cart_country}")
        .formParam("state_code", "")
        .check(status.is(200))
      ).pause(BaseHelpers.minPause)


  val fetchDeliveryCountryStates: ChainBuilder = {
    doIf(session => session("cart_s_country").as[String].nonEmpty) {
      exec(BaseHelpers.cookies)
        .feed(BaseHelpers.checkoutDetailsFeeder)
        .exec(http("Fetch States based on Country")
          .post("/wp-admin/admin-ajax.php")
          .formParam("action", "ic_state_dropdown")
          .formParam("country_code", "#{cart_s_country}")
          .formParam("state_code", "")
          .check(status.is(200))
        ).pause(BaseHelpers.minPause)
    }

  }


  val checkout: ChainBuilder =
    exec(fetchBillingCountryStates)
      .exec(fetchDeliveryCountryStates)
      .exec(BaseHelpers.cookies)
      .feed(BaseHelpers.checkoutDetailsFeeder)
      .exec(http("Fill in all required fields, click 'Place an order': user = #{cart_name}")
        .post("/checkout")
        .formParam("ic_formbuilder_redirect", "http://localhost/thank-you")
        .formParam("cart_content", "{\"#{tableId}__\":#{tableQuantity},\"#{chairId}__\":#{chairQuantity}}")
        .formParam("product_price_#{tableId}__", "#{tableUnitPrice}")
        .formParam("product_price_#{chairId}__", "#{chairUnitPrice}")
        .formParam("total_net", "#{totalNetWorth}")
        .formParam("trans_id", "#{transId}")
        .formParam("shipping", "order")
        .formParam("cart_type", "order")
        .formParam("cart_inside_header_1", "BILLING ADDRESS")
        .formParam("cart_company", "#{cart_company}")
        .formParam("cart_name", "#{cart_name}")
        .formParam("cart_address", "#{cart_address}")
        .formParam("cart_postal", "#{cart_postal}")
        .formParam("cart_city", "#{cart_city}")
        .formParam("cart_country", "#{cart_country}")
        .formParam("cart_state", "#{cart_state}")
        .formParam("cart_phone", "#{cart_phone}")
        .formParam("cart_email", "#{cart_email}")
        .formParam("cart_comment", "#{cart_comment}")
        .formParam("cart_inside_header_2", "DELIVERY ADDRESS (FILL ONLY IF DIFFERENT FROM THE BILLING ADDRESS)")
        .formParam("cart_s_company", "#{cart_s_company}")
        .formParam("cart_s_name", "#{cart_s_name}")
        .formParam("cart_s_address", "#{cart_s_address}")
        .formParam("cart_s_postal", "#{cart_s_postal}")
        .formParam("cart_s_city", "#{cart_s_city}")
        .formParam("cart_s_country", "#{cart_s_country}")
        .formParam("cart_s_state", "#{cart_s_state}")
        .formParam("cart_s_phone", "#{cart_s_phone}")
        .formParam("cart_s_email", "#{cart_s_email}")
        .formParam("cart_s_comment", "#{cart_s_comment}")
        .formParam("cart_submit", "Place Order")
        .check(
          status.is(200),
          css("title").is("Thank You – Performance testing Essentials"),
          css("*[class*='entry-title']").is("Thank You"))
      ).pause(BaseHelpers.minPause)


}
