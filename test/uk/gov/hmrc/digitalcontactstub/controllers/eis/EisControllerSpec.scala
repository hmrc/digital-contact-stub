/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.digitalcontactstub.controllers.eis

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ BeforeAndAfterEach, OptionValues }
import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatestplus.play.{ OneAppPerSuite, PlaySpec }
import org.scalatestplus.play.components.OneAppPerSuiteWithComponents
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.BuiltInComponents
import play.api.mvc.{ Headers, Result }
import play.api.test.{ FakeRequest, Helpers }
import uk.gov.hmrc.digitalcontactstub.models.eis.{ EisFailure, EisFailures, GmcPrintRequest }
import uk.gov.hmrc.mongo.test.MongoSupport
import play.api.libs.json.{ JsValue, Json }
import uk.gov.hmrc.digitalcontactstub.utils.SpecBase
import play.api.test.*
import play.api.test.Helpers.*

import scala.concurrent.Future

class EisControllerSpec extends PlaySpec with OptionValues with ScalaFutures {

  private val eisController = new EisController(Helpers.stubControllerComponents())

  object Header {
    val validAuth: (String, String) = (AUTHORIZATION, "Bearer: xxxxx")
    val validCorrelationId: (String, String) = ("X-Correlation-ID", "0177f8d6-e50a-41f0-a230-ad48e44f5a29")
    val validEnvironment: (String, String) = ("Environment", "xxxx")

    val validHeaders: List[(String, String)] = List(validAuth, validCorrelationId, validEnvironment)
  }

  val validGmcPrintRequestJson: JsValue = Json.toJson(
    GmcPrintRequest(
      reason = "",
      sourceData = "",
      emailAddress = ""
    )
  )

  "sendSuppressionLetter  POST /eis/sa-forms/suppression/send-letter" must {
    def callSendSuppressionLetter(payload: JsValue, additionalHeaders: (String, String)*): Future[Result] = {
      val request = FakeRequest(
        "POST",
        "/eis/sa-forms/suppression/send-letter",
        Headers((Helpers.CONTENT_TYPE, "application/json") +: additionalHeaders: _*),
        payload
      )

      eisController.sendSuppressionLetter(request)
    }

    "succeed when all headers exist and there is a valld paylpad" in {
      val gmcPrintRequestJson: JsValue = Json.toJson(
        GmcPrintRequest(
          reason = "",
          sourceData = "",
          emailAddress = ""
        )
      )

      val result = callSendSuppressionLetter(
        gmcPrintRequestJson,
        Header.validAuth,
        Header.validCorrelationId,
        Header.validEnvironment
      )

      status(result) mustBe OK
    }

    "fail when there is" must {

      val emptyJson = Json.parse("{}")

      "no expected headers" in {
        val result = callSendSuppressionLetter(emptyJson)
        status(result) mustBe UNAUTHORIZED

      }

      "no authorisation header" in {
        val result = callSendSuppressionLetter(emptyJson, Header.validCorrelationId, Header.validEnvironment)
        status(result) mustBe UNAUTHORIZED
      }

      "an empty authorisation header" in {
        val result =
          callSendSuppressionLetter(emptyJson, (AUTHORIZATION, ""), Header.validCorrelationId, Header.validEnvironment)
        status(result) mustBe UNAUTHORIZED
      }

      "an invalid formatted authorisation header" in {
        val result = callSendSuppressionLetter(
          validGmcPrintRequestJson,
          (AUTHORIZATION, "wrong format"),
          Header.validCorrelationId,
          Header.validEnvironment
        )
        status(result) mustBe UNAUTHORIZED
      }

      "no correlation id header" in {
        val result = callSendSuppressionLetter(validGmcPrintRequestJson, Header.validAuth, Header.validEnvironment)
        status(result) mustBe BAD_REQUEST

        contentAsJson(result).as[EisFailures] mustBe EisFailures(
          List(
            EisFailure.InvalidCorrelationIdError
          )
        )
      }

      "no correlation id in a parsable format" in {
        val result = callSendSuppressionLetter(
          validGmcPrintRequestJson,
          Header.validAuth,
          Header.validEnvironment,
          ("X-Correlation-ID", "wrong format")
        )
        status(result) mustBe BAD_REQUEST
        contentAsJson(result).as[EisFailures] mustBe EisFailures(
          List(
            EisFailure.InvalidCorrelationIdError
          )
        )
      }

      "no environment" in {
        val result = callSendSuppressionLetter(
          validGmcPrintRequestJson,
          Header.validAuth,
          Header.validCorrelationId
        )
        status(result) mustBe BAD_REQUEST
        contentAsJson(result).as[EisFailures] mustBe EisFailures(
          List(
            EisFailure.InvalidRequestError
          )
        )
      }

      "an empty environment" in {
        val result = callSendSuppressionLetter(
          validGmcPrintRequestJson,
          Header.validAuth,
          Header.validCorrelationId,
          ("Environment", " ")
        )
        status(result) mustBe BAD_REQUEST
        contentAsJson(result).as[EisFailures] mustBe EisFailures(
          List(
            EisFailure.InvalidRequestError
          )
        )
      }

      "all valid headers and an invalid payload" in {
        val result = callSendSuppressionLetter(
          emptyJson,
          Header.validAuth,
          Header.validCorrelationId,
          Header.validEnvironment
        )
        status(result) mustBe BAD_REQUEST
        contentAsJson(result).as[EisFailures] mustBe EisFailures(
          List(
            EisFailure.InvalidPayload
          )
        )
      }

      "authorized with everything else invalid" in {
        val result = callSendSuppressionLetter(
          emptyJson,
          Header.validAuth
        )
        status(result) mustBe BAD_REQUEST
        contentAsJson(result).as[EisFailures] mustBe EisFailures(
          List(
            EisFailure.InvalidCorrelationIdError,
            EisFailure.InvalidRequestError,
            EisFailure.InvalidPayload
          )
        )
      }
    }
  }
}
