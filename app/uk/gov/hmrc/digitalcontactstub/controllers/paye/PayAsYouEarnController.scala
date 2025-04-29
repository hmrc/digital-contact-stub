/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.digitalcontactstub.controllers.paye

import play.api.Logging
import play.api.libs.json.{ JsValue, Json }
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.mvc.{ Action, MessagesControllerComponents }
import uk.gov.hmrc.digitalcontactstub.models.paye.{ HipNpsPrintSuppressionUpdateRequest, PayeOutput }
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{ Inject, Singleton }
import scala.concurrent.Future
@Singleton
class PayAsYouEarnController @Inject() (
  cc: MessagesControllerComponents
) extends BackendController(cc) with Logging {

  def changedOutputPreferences(nino: String): Action[JsValue] =
    Action.async(parse.json) { implicit request =>
      withJsonBody[PayeOutput] { body =>
        logger.info(s"Received request: [$request] body: [$body]")
        processDES(nino)
      }
    }

  def hipChangedOutputPreferences(): Action[JsValue] =
    Action.async(parse.json) { implicit request =>
      withJsonBody[HipNpsPrintSuppressionUpdateRequest] { body =>
        logger.info(s"Received request: [$request] body: [$body]")
        processHIP(ninoStr = body.nationalInsuranceNumber.value, lockValue = (body.currentOptimisticLock + 1).toShort)
      }
    }

  private def processDES(ninoStr: String) =
    Future.successful {
      ninoStr.take(8) match {
        case "YY000200" => Ok
        case "YY000400" => BadRequest
        case "YY000404" => NotFound
        case "YY000502" => BadGateway
        case "YY000503" => ServiceUnavailable
        case _ =>
          InternalServerError("""
                                |Send the corresponding Nino for a response:
                                |case "YY000200" => Ok
                                |case "YY000400" => BadRequest
                                |case "YY000404" => NotFound
                                |case "YY000500" => InternalServerError
                                |case "YY000502" => BadGateway
                                |case "YY000503" => ServiceUnavailable
                                |case _          => InternalServerError
                                |""".stripMargin)
      }
    }

  private def processHIP(ninoStr: String, lockValue: Short) =
    Future.successful {
      ninoStr.take(8) match {
        case "YY000200" => Ok(Json.parse(s"""{ "updatedOptimisticLock": $lockValue }"""))
        case "YY000400" => BadRequest
        case "YY000403" => Forbidden
        case "YY000404" => NotFound
        case "YY000409" => Conflict
        case "YY000422" => UnprocessableEntity
        case "YY000500" => InternalServerError
        case "YY000502" => BadGateway
        case "YY000503" => ServiceUnavailable
        case _ =>
          InternalServerError("""
                                |Send the corresponding Nino for a response:
                                |  case "YY000200" => Ok(currentOptimisticLock + 1)
                                |  case "YY000400" => BadRequest
                                |  case "YY000403" => Forbidden
                                |  case "YY000404" => NotFound
                                |  case "YY000409" => Conflict
                                |  case "YY000422" => UnprocessableEntity
                                |  case "YY000500" => InternalServerError
                                |  case "YY000502" => BadGateway
                                |  case "YY000503" => ServiceUnavailable
                                |""".stripMargin)
      }
    }
}
