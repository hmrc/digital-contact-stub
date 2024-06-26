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

import enumeratum.{ Enum, EnumEntry, PlayEnum }

sealed trait DeliveryDescription extends EnumEntry

object DeliveryDescription extends Enum[DeliveryDescription] with PlayEnum[DeliveryDescription] {
  override def values: IndexedSeq[DeliveryDescription] = findValues
  case object Submitted extends DeliveryDescription
  case object Read extends DeliveryDescription
  case object Delivered extends DeliveryDescription
  case object Transient_ContentRejected extends DeliveryDescription
  case object Transient_General extends DeliveryDescription
  case object Recipient_not_consented extends DeliveryDescription
  case object Complained extends DeliveryDescription
}
