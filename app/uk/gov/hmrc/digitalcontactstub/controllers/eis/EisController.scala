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

import play.api.Logging
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.*
import uk.gov.hmrc.digitalcontactstub.models.eis.{ EisFailure, EisFailures, GmcPrintRequest }
import uk.gov.hmrc.digitalcontactstub.models.email.SendEmailRequest
import uk.gov.hmrc.digitalcontactstub.models.email.SendEmailRequest.*
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import scala.concurrent.Future
import scala.util.Try

@Singleton()
class EisController @Inject() (cc: ControllerComponents) extends BackendController(cc) with Logging {

  def sendSuppressionLetter: Action[JsValue] = Action.async(parse.json) { implicit request: Request[JsValue] =>
    val hasValidAuthHeader = request.headers
      .get(AUTHORIZATION)
      .exists(_.startsWith("Bearer: "))

    if (!hasValidAuthHeader) {
      logger.debug(s"A valid AUTHORIZATION header was not received")
      Future.successful(Unauthorized)
    } else {
      val failures = validateRequestContents(request)
      if (failures.nonEmpty) {
        Future.successful(BadRequest(Json.toJson(EisFailures(failures))))
      } else {
        Future.successful(Ok)
      }
    }
  }

  private def validateRequestContents(request: Request[JsValue]): Seq[EisFailure] = {
    val headers = request.headers
    val hasValidCorrelationIdHeader =
      headers.get("X-Correlation-ID").exists(idText => Try(UUID.fromString(idText)).isSuccess)
    val hasEnvironmentHeader =
      headers.get("Environment").exists(_.trim.nonEmpty)

    val isValidPayload = request.body.validate[GmcPrintRequest].isSuccess

    val validationResultsWithPotentialError = List(
      (hasValidCorrelationIdHeader, EisFailure.InvalidCorrelationIdError),
      (hasEnvironmentHeader, EisFailure.InvalidRequestError),
      (isValidPayload, EisFailure.InvalidPayload)
    )

    validationResultsWithPotentialError.collect { case (wasSuccessful, error) if !wasSuccessful => error }
  }

}
