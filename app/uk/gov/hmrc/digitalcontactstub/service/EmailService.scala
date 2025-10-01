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

package uk.gov.hmrc.digitalcontactstub.service

import play.api.Logging
import play.api.libs.json.{ JsValue, Json, Writes }
import play.api.libs.json.Json.{ parse, stringify }
import uk.gov.hmrc.digitalcontactstub.EmailConnector
import uk.gov.hmrc.digitalcontactstub.models.email.{ EmailContent, EmailQueued, QueueItem, SendEmailRequest }
import uk.gov.hmrc.digitalcontactstub.repositories.EmailQueueRepository
import uk.gov.hmrc.digitalcontactstub.utils.Encryption
import uk.gov.hmrc.http.{ HeaderCarrier, HttpResponse }

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.{ Base64, UUID }
import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Success, Try }

@Singleton
class EmailService @Inject() (
  emailConnector: EmailConnector
)(implicit ec: ExecutionContext)
    extends Logging {

  def sendEmail(request: SendEmailRequest)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val parametersSize: Int = request.parameters("rowSize").toInt
    def paramRow(rows: Int): Seq[Row] = {
      val range: Seq[Int] = 1 to rows
      range.map(i => Row(s"Field$i", s"Validation Error returned in Piller2 Submission REF-GB2025-$i"))
    }

    val generateParamRows = stringify(Json.toJson(paramRow(parametersSize))).getBytes("UTF-8")
    val size: Double = generateParamRows.map(_.toInt).sum.toDouble
    logger.warn(s"Send Email Parameters size in bytes $size")
    val generateParameters = Map(
      "submission_errors" -> Base64.getEncoder.encodeToString(generateParamRows)
    )
    val requestWithParams = SendEmailRequest(
      request.to,
      request.templateId,
      generateParameters,
      Map.empty,
      false,
      None,
      None,
      Map.empty
    )
    emailConnector.sendEmail(requestWithParams)
  }
}

final case class Row(
  fieldName: String,
  fieldError: String
)

object Row {
  implicit val rowWrites: Writes[Row] = Json.writes[Row]
}
