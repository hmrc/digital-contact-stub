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

import play.api.Logging
import play.api.libs.json.{ JsString, JsValue, Json }
import play.api.mvc.{ Action, AnyContent, Headers, MessagesControllerComponents }
import play.mvc.Results.ok
import uk.gov.hmrc.digitalcontactstub.models.email.SendEmailRequest
import uk.gov.hmrc.digitalcontactstub.models.externalmessageadapter.{ EmailBounceBackFailuresResponse, EmailBounceBackRequest, EmailBounceBackResponseBody }
import uk.gov.hmrc.digitalcontactstub.utils.Utils
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{ Inject, Singleton }
import scala.concurrent.Future
import scala.util.matching.Regex
import uk.gov.hmrc.digitalcontactstub.models.externalmessageadapter.EmailBounceBackResponseBody.format

@Singleton
class EmailBounceBackHandlerController @Inject() (cc: MessagesControllerComponents)
    extends BackendController(cc) with Logging {

  def processEmailBounceBack: Action[JsValue] =
    Action.async(parse.json) { implicit request =>
      withJsonBody[EmailBounceBackRequest] { emailBounceReq =>
        val emptyString = ""
        val requestHeaders: Headers = request.headers

        val authHeaderName = "Authorization"
        val correlationIdHeaderName = "correlationid"

        val authHeaderValue = requestHeaders.get(authHeaderName).getOrElse(emptyString)

        if (
          authHeaderValue.isEmpty || !requestHeaders.hasHeader(
            correlationIdHeaderName
          ) || !isAuthHeaderValueInCorrectFormat(authHeaderValue)
        ) {
          Future.successful(Unauthorized)
        } else {
          val correlationIdHeaderValue = requestHeaders.get(correlationIdHeaderName).getOrElse(emptyString)

          if (isCorrelationIdInCorrectFormat(correlationIdHeaderValue)) {
            processPayloadAfterHeadersCheck(emailBounceReq)
          } else {
            Future.successful(BadRequest)
          }
        }
      }
    }

  private def isCorrelationIdInCorrectFormat(id: String) = {
    val correlationIdRegex: Regex = """[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}""".r

    correlationIdRegex.matches(id)
  }

  private def isAuthHeaderValueInCorrectFormat(id: String) = id.startsWith("Basic")

  private def processPayloadAfterHeadersCheck(request: EmailBounceBackRequest) =
    Utils.decodeStringFromBase64(request.sourceData) match {
      case "InternalServerError" => Future.successful(InternalServerError(Json.toJson(create500ErrorResponse)))
      case "ServiceUnavailable"  => Future.successful(ServiceUnavailable(Json.toJson(create503ErrorResponse)))
      case "NotFound"            => Future.successful(NotFound)
      case "BadRequest"          => Future.successful(BadRequest(Json.toJson(create400ErrorResponse)))
      case "Forbidden"           => Future.successful(Forbidden)
      case _ =>
        if (isExternalRefIdInCorrectFormat(request.externalRefId)) {
          Future.successful(Ok(JsString("Request successfully processed")))
        } else {
          Future.successful(BadRequest(Json.toJson(createInvalidExternalRef400ErrorResponse)))
        }
    }

  private def isExternalRefIdInCorrectFormat(externalId: String) = {
    val externalRefRegEx: Regex = """^[^\\s]{1,40}$""".r

    externalRefRegEx.matches(externalId)
  }

  private def create500ErrorResponse: EmailBounceBackResponseBody = {
    val responseString =
      """{
        |  "origin": "HIP",
        |  "response": {
        |    "failures": [
        |      {
        |        "type": "server error",
        |        "reason": "server error"
        |      }
        |    ]
        |  }
        |}""".stripMargin

    Json.parse(responseString).as[EmailBounceBackResponseBody]
  }

  private def create503ErrorResponse: EmailBounceBackResponseBody = {
    val responseString =
      """{
        |  "origin": "HIP",
        |  "response": {
        |    "failures": [
        |      {
        |        "type": "service unavailable",
        |        "reason": "service unavailable"
        |      }
        |    ]
        |  }
        |}""".stripMargin

    Json.parse(responseString).as[EmailBounceBackResponseBody]
  }

  private def create400ErrorResponse: EmailBounceBackResponseBody = {
    val responseString =
      """{
        |  "origin": "HIP",
        |  "response": {
        |    "failures": [
        |      {
        |        "type": "body.schema.pattern",
        |        "reason": "Path '/emailAddress' validation failed."
        |      }
        |    ]
        |  }
        |}""".stripMargin

    Json.parse(responseString).as[EmailBounceBackResponseBody]
  }

  private def createInvalidExternalRef400ErrorResponse: EmailBounceBackResponseBody = {
    val responseString =
      """{
        |  "origin": "HIP",
        |  "response": {
        |    "failures": [
        |      {
        |        "type": "body.schema.pattern",
        |        "reason": "invalid externalRef id"
        |      }
        |    ]
        |  }
        |}""".stripMargin

    Json.parse(responseString).as[EmailBounceBackResponseBody]
  }
}
