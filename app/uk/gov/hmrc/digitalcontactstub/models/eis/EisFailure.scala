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

import play.api.http.Writeable
import play.api.libs.json.*

object EisFailures {
  implicit val format: OFormat[EisFailures] = Json.format[EisFailures]
}
case class EisFailures(failures: Seq[EisFailure])

object EisFailure {
  implicit val format: OFormat[EisFailure] = Json.format[EisFailure]

  val InvalidCorrelationIdError =
    EisFailure("INVALID_CORRELATIONID", "Submission has not passed validation. Invalid header CorrelationId.")

  val InvalidPayload =
    EisFailure("INVALID_PAYLOAD", "Submission has not passed validation. Invalid payload.")

  val InvalidRequestError =
    EisFailure("INVALID_REQUEST", "The remote endpoint has indicated that the request is invalid.")
}

case class EisFailure(code: String, reason: String)
