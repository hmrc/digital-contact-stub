/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.digitalcontactstub.models.paye

import play.api.libs.json.{ JsError, JsResult, JsString, JsSuccess, JsValue, Json, Reads, Writes, __ }
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import uk.gov.hmrc.domain.Nino

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.{ Failure, Success, Try }

case class HipNpsPrintSuppressionUpdateRequest(
  nationalInsuranceNumber: Nino,
  bouncedFlag: Boolean,
  currentOptimisticLock: Short,
  printPreferences: Seq[PrintPreference]
)

object HipNpsPrintSuppressionUpdateRequest {
  private implicit val itemReads: Reads[Nino] = Nino.ninoRead

  given reads: Reads[HipNpsPrintSuppressionUpdateRequest] = (
    (__ \ "nationalInsuranceNumber").read[Nino] and
      (__ \ "bouncedFlag").read[Boolean] and
      (__ \ "currentOptimisticLock").read[Short] and
      (__ \ "printPreferences").read[Seq[PrintPreference]]
  )(HipNpsPrintSuppressionUpdateRequest.apply _)
}

case class PrintPreference(outputFormType: OutputFormType, printStatus: PrintStatus, lastUpdatedDate: LocalDate)

object PrintPreference {

  private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

  implicit val localDateReads: Reads[LocalDate] = Reads {
    case JsString(dateString) =>
      Try(LocalDate.parse(dateString, dateFormatter)) match {
        case Success(date) => JsSuccess(date)
        case Failure(_) =>
          JsError(s"Expected date in format ${dateFormatter.toString}, but got '$dateString'")
      }
    case other =>
      JsError(s"Expected date as JsString, but got ${other.getClass.getSimpleName}")
  }

  implicit val itemReads: Reads[PrintPreference] = Json.reads[PrintPreference]
  implicit val seqItemReads: Reads[Seq[PrintPreference]] = Reads.seq[PrintPreference](itemReads)
}

enum PrintStatus(val value: String) {
  case PAPER extends PrintStatus("PAPER")
  case DIGITAL extends PrintStatus("DIGITAL")
}
object PrintStatus {
  def from(messageDeliveryFormat: String): PrintStatus =
    messageDeliveryFormat.toLowerCase match {
      case "paper"   => PAPER
      case "digital" => DIGITAL
    }

  given Reads[PrintStatus] with
    def reads(json: JsValue): JsResult[PrintStatus] =
      json match
        case JsString("PAPER")   => JsSuccess(PrintStatus.PAPER)
        case JsString("DIGITAL") => JsSuccess(PrintStatus.DIGITAL)
        case _                   => JsError(s"Invalid PrintStatus value. Expected 'PAPER' or 'DIGITAL', got: $json")
}

enum OutputFormType(val value: String) {
  // There are many form types described in the api
  // AFAIK these are the only ones likely to apply to DC
  case NOT_KNOWN extends OutputFormType("NOT KNOWN")
  case P2 extends OutputFormType("P2")
}
object OutputFormType {
//  implicit val reads: Reads[OutputFormType] = Json.reads[OutputFormType]

  given Reads[OutputFormType] with
    def reads(json: JsValue): JsResult[OutputFormType] =
      json match
        case JsString("NOT KNOWN") => JsSuccess(OutputFormType.NOT_KNOWN)
        case JsString("P2")        => JsSuccess(OutputFormType.P2)
        case _                     => JsError(s"Invalid OutputFormType value. Expected 'NOT KNOWN' or 'P2', got: $json")
}
