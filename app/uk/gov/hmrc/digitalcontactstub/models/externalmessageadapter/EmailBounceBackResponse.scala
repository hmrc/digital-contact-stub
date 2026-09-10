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

package uk.gov.hmrc.digitalcontactstub.models.externalmessageadapter

import play.api.libs.json.{ Format, JsError, JsString, JsSuccess, Json, OFormat, Reads, Writes }

enum HIPOrigin {
  case HIP, HoD
  private def entryName: String = this.toString.toLowerCase
}

object HIPOrigin {

  implicit val reads: Reads[HIPOrigin] = Reads {
    case JsString(value) =>
      values.find(_.entryName.equalsIgnoreCase(value)) match {
        case Some(status) => JsSuccess(status)
        case _            => JsError(s"Unknown HIPOrigin: $value")
      }

    case _ => JsError("HIPOrigin must be a string")
  }

  implicit val writes: Writes[HIPOrigin] = Writes(status => JsString(status.entryName))
  implicit val format: Format[HIPOrigin] = Format(reads, writes)
}

case class EmailBounceBackFailureResponse(failureType: Option[String] = None, reason: String)
object EmailBounceBackFailureResponse {
  implicit val format: OFormat[EmailBounceBackFailureResponse] = Json.format[EmailBounceBackFailureResponse]
}

case class EmailBounceBackFailuresResponse(failures: List[EmailBounceBackFailureResponse])

object EmailBounceBackFailuresResponse {
  implicit val format: OFormat[EmailBounceBackFailuresResponse] = Json.format[EmailBounceBackFailuresResponse]
}

case class EmailBounceBackResponseBody(origin: HIPOrigin, response: Option[EmailBounceBackFailuresResponse])

object EmailBounceBackResponseBody {
  implicit val format: OFormat[EmailBounceBackResponseBody] = Json.format[EmailBounceBackResponseBody]
}

case class EmailBounce4xxResponse(message: String)

object EmailBounce4xxResponse {
  implicit val format: OFormat[EmailBounce4xxResponse] = Json.format[EmailBounce4xxResponse]
}
