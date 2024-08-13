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

package uk.gov.hmrc.digitalcontactstub.models.email

import play.api.libs.json.{ Format, JsError, JsResult, JsString, JsSuccess, JsValue }

enum DeliveryDescription {
  case Submitted
  case Read
  case Delivered
  case Transient_ContentRejected
  case Transient_General
  case Recipient_not_consented
  case Complained
}

object DeliveryDescription {

  implicit val format: Format[DeliveryDescription] = new Format[DeliveryDescription] {
    def reads(json: JsValue): JsResult[DeliveryDescription] = json match {
      case JsString("Submitted")                 => JsSuccess(Submitted)
      case JsString("Read")                      => JsSuccess(Read)
      case JsString("Delivered")                 => JsSuccess(Delivered)
      case JsString("Transient_ContentRejected") => JsSuccess(Transient_ContentRejected)
      case JsString("Transient_General")         => JsSuccess(Transient_General)
      case JsString("Recipient_not_consented")   => JsSuccess(Recipient_not_consented)
      case JsString("Complained")                => JsSuccess(Complained)
      case _                                     => JsError("Invalid DeliveryDescription")
    }

    def writes(deliveryDescription: DeliveryDescription): JsValue = JsString(deliveryDescription.toString)
  }

}
