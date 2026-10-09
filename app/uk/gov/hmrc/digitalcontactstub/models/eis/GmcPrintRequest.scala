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

package uk.gov.hmrc.digitalcontactstub.models.eis

import play.api.Logging
import play.api.libs.json.{ JsValue, Json, OFormat }

case class GmcPrintRequest(
  reason: String,
  sourceData: String,
  emailAddress: String,
  formId: Option[String] = None,
  properties: Option[JsValue] = None
)

object GmcPrintRequest extends Logging {
  implicit val format: OFormat[GmcPrintRequest] = Json.format[GmcPrintRequest]
}

case class GmcPrintResponse(status: Int, message: String)
case class GmcPrintResponseBody(failures: List[GmcPrintFailureResponse])

object GmcPrintResponseBody {
  implicit val failureFormat: OFormat[GmcPrintFailureResponse] = Json.format[GmcPrintFailureResponse]
  implicit val format: OFormat[GmcPrintResponseBody] = Json.format[GmcPrintResponseBody]

}
case class GmcPrintFailureResponse(reason: String, code: Option[String])

object GmcPrintResponse {

  val UNKNOWN_EIS_ERROR = "Unknown eis error"
  def unknownGmcPrintResponse(status: Int): GmcPrintResponse =
    GmcPrintResponse(status, UNKNOWN_EIS_ERROR)

}
