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

import play.api.libs.json.{Json, Reads, __}
import play.api.libs.functional.syntax._
import java.util.UUID

final case class DeliveryInfoNotification(deliveryInfo: DeliveryInfo,
                                          subtid: String,
                                          transid: UUID,
                                          callbackData: String,
                                          correlationid: UUID)

object DeliveryInfoNotification {

  implicit val formatReads: Reads[DeliveryInfoNotification] = (
    (__ \ "deliveryInfo").read[DeliveryInfo] and
      (__ \ "subtid").read[String] and
      (__ \ "transid").read[UUID] and
      (__ \ "callbackData").read[String] and
      (__ \ "correlationid").read[UUID]
  )(DeliveryInfoNotification.apply _)

  implicit val formatWrites = Json.format[DeliveryInfoNotification]

}
