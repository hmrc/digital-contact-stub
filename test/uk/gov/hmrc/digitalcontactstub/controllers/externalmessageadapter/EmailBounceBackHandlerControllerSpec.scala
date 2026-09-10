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

package uk.gov.hmrc.digitalcontactstub.controllers.externalmessageadapter

import play.api.libs.json.{ JsString, Json }
import play.api.mvc.{ MessagesControllerComponents, Result }
import play.api.test.FakeRequest
import play.api.test.Helpers.{ POST, route, status }
import uk.gov.hmrc.digitalcontactstub.models.externalmessageadapter.{ EmailBounceBackFailureResponse, EmailBounceBackFailuresResponse, EmailBounceBackRequest, EmailBounceBackResponseBody }
import uk.gov.hmrc.digitalcontactstub.utils.SpecBase
import uk.gov.hmrc.http.{ Authorization, HeaderCarrier }

import java.util.UUID
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.matching.Regex
import play.api.test.*
import play.api.http.Status.UNAUTHORIZED
import play.api.test.Helpers.*
import uk.gov.hmrc.digitalcontactstub.models.externalmessageadapter.HIPOrigin.HIP

class EmailBounceBackHandlerControllerSpec extends SpecBase {

  "processEmailBounceBack" must {
    import uk.gov.hmrc.digitalcontactstub.models.externalmessageadapter.EmailBounceBackRequest.format

    "return OK response when source data value is Ok" in new Setup {
      val request = FakeRequest(
        POST,
        "/emailBounceback",
        FakeHeaders(
          Seq(("correlationid", correlationId), ("Authorization", "Basic 12345"), ("Csrf-Token", "nocheck"))
        ),
        Json.toJson(emailBounceBackRequest.copy(sourceData = "T2s="))
      )

      val result: Future[Result] = route(application, request).get
      status(result) mustBe OK
      contentAsJson(result) mustBe JsString("Request successfully processed")
    }

    "return InternalServerError with failure response when source data value is InternalServerError" in new Setup {
      val request = FakeRequest(
        POST,
        "/emailBounceback",
        FakeHeaders(
          Seq(("correlationid", correlationId), ("Authorization", "Basic 12345"), ("Csrf-Token", "nocheck"))
        ),
        Json.toJson(emailBounceBackRequest.copy(sourceData = "SW50ZXJuYWxTZXJ2ZXJFcnJvcg=="))
      )

      val emailBounceFailureResponse =
        EmailBounceBackFailuresResponse(List(EmailBounceBackFailureResponse(reason = "server error")))
      val responseOb = EmailBounceBackResponseBody(origin = HIP, response = Some(emailBounceFailureResponse))

      val result: Future[Result] = route(application, request).get
      status(result) mustBe INTERNAL_SERVER_ERROR
      contentAsJson(result) mustBe Json.toJson(responseOb)
    }

    "return ServiceUnavailable with failure response when source data value is ServiceUnavailable" in new Setup {
      val request = FakeRequest(
        POST,
        "/emailBounceback",
        FakeHeaders(
          Seq(("correlationid", correlationId), ("Authorization", "Basic 12345"), ("Csrf-Token", "nocheck"))
        ),
        Json.toJson(emailBounceBackRequest.copy(sourceData = "U2VydmljZVVuYXZhaWxhYmxl"))
      )

      val emailBounceFailureResponse =
        EmailBounceBackFailuresResponse(List(EmailBounceBackFailureResponse(reason = "service unavailable")))

      val responseOb = EmailBounceBackResponseBody(origin = HIP, response = Some(emailBounceFailureResponse))

      val result: Future[Result] = route(application, request).get
      status(result) mustBe SERVICE_UNAVAILABLE
      contentAsJson(result) mustBe Json.toJson(responseOb)
    }

    "return NotFound with failure response when source data value is NotFound" in new Setup {
      val request = FakeRequest(
        POST,
        "/emailBounceback",
        FakeHeaders(
          Seq(("correlationid", correlationId), ("Authorization", "Basic 12345"), ("Csrf-Token", "nocheck"))
        ),
        Json.toJson(emailBounceBackRequest.copy(sourceData = "Tm90Rm91bmQ="))
      )

      val result: Future[Result] = route(application, request).get
      status(result) mustBe NOT_FOUND
    }

    "return Forbidden with failure response when source data value is Forbidden" in new Setup {
      val request = FakeRequest(
        POST,
        "/emailBounceback",
        FakeHeaders(
          Seq(("correlationid", correlationId), ("Authorization", "Basic 12345"), ("Csrf-Token", "nocheck"))
        ),
        Json.toJson(emailBounceBackRequest.copy(sourceData = "Rm9yYmlkZGVu"))
      )

      val result: Future[Result] = route(application, request).get
      status(result) mustBe FORBIDDEN
    }

    "return Unauthorized" when {
      "mandatory header correlationid is missing" in new Setup {
        val request = FakeRequest(
          POST,
          "/emailBounceback",
          FakeHeaders(Seq()),
          Json.toJson(emailBounceBackRequest)
        )

        val result: Future[Result] = route(application, request).get
        status(result) mustBe UNAUTHORIZED
      }

      "mandatory header authorization is missing" in new Setup {
        val request = FakeRequest(
          POST,
          "/emailBounceback",
          FakeHeaders(Seq(("correlationid", correlationId))),
          Json.toJson(emailBounceBackRequest)
        )

        val result: Future[Result] = route(application, request).get
        status(result) mustBe UNAUTHORIZED
      }
    }

    "return BAD_REQUEST" when {
      "header correlationid is of incorrect format" in new Setup {
        implicit val hc: HeaderCarrier = HeaderCarrier(authorization = Some(Authorization("Bearer 12345")))

        val invalidCorrelationId: String = UUID.randomUUID().toString.replace("-", "")

        val request = FakeRequest(
          POST,
          "/emailBounceback",
          FakeHeaders(
            Seq(("correlationid", invalidCorrelationId), ("Authorization", "Basic 12345"), ("Csrf-Token", "nocheck"))
          ),
          Json.toJson(emailBounceBackRequest)
        )

        val result: Future[Result] = route(application, request).get
        status(result) mustBe BAD_REQUEST
      }

      "with failure response when source data value is BadRequest" in new Setup {
        val request = FakeRequest(
          POST,
          "/emailBounceback",
          FakeHeaders(
            Seq(("correlationid", correlationId), ("Authorization", "Basic 12345"), ("Csrf-Token", "nocheck"))
          ),
          Json.toJson(emailBounceBackRequest.copy(sourceData = "QmFkUmVxdWVzdA=="))
        )

        val emailBounceFailureResponse =
          EmailBounceBackFailuresResponse(
            List(EmailBounceBackFailureResponse(reason = "Path '/emailAddress' validation failed."))
          )

        val responseOb = EmailBounceBackResponseBody(origin = HIP, response = Some(emailBounceFailureResponse))

        val result: Future[Result] = route(application, request).get
        status(result) mustBe BAD_REQUEST
        contentAsJson(result) mustBe Json.toJson(responseOb)
      }

      "with failure response when externalRefId is invalid" in new Setup {
        val request = FakeRequest(
          POST,
          "/emailBounceback",
          FakeHeaders(
            Seq(("correlationid", correlationId), ("Authorization", "Basic 12345"), ("Csrf-Token", "nocheck"))
          ),
          Json.toJson(
            emailBounceBackRequest
              .copy(sourceData = "QmFkUmVxdWV", externalRefId = "9drtsdfyftdftftfsgggg7f1d675d544b009d6c3a8f7fb2e1c4")
          )
        )

        val emailBounceFailureResponse =
          EmailBounceBackFailuresResponse(
            List(EmailBounceBackFailureResponse(reason = "invalid externalRef id"))
          )

        val responseOb = EmailBounceBackResponseBody(origin = HIP, response = Some(emailBounceFailureResponse))

        val result: Future[Result] = route(application, request).get
        status(result) mustBe BAD_REQUEST
        contentAsJson(result) mustBe Json.toJson(responseOb)
      }
    }
  }

  trait Setup {
    val correlationidRegex: Regex = """[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}""".r
    val correlationId: String = UUID.randomUUID().toString

    val emailBounceBackRequest = EmailBounceBackRequest(
      reason = "EMAIL_BOUNCE",
      sourceData = "SGVsbG8gd29ybGQ=",
      emailAddress = "test@test.com",
      formId = Some("CH(A)1700"),
      properties = None,
      externalRefId = "9d7f1d675d544b009d6c3a8f7fb2e1c4"
    )

    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  }
}
