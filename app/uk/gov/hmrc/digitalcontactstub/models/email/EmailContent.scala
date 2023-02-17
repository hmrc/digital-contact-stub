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

import play.api.libs.json.{Json, OFormat}

final case class EmailContent(
    channel: String,
    from: String,
    to: List[To],
    callbackData: String,
    options: Options,
    content: Content
)

final case class EmailAddress(value: String)

object EmailAddress {
  implicit val format: OFormat[EmailAddress] = Json.format[EmailAddress]
}

final case class To(email: List[String], correlationId: String)
object To {
  implicit val format: OFormat[To] = Json.format[To]

}

final case class Content(`type`: String,
                         subject: String,
                         replyTo: Option[EmailAddress],
                         text: String,
                         html: String)

object Content {

  implicit val format: OFormat[Content] = Json.format[Content]
}

final case class Options(trackClicks: Boolean,
                         trackOpens: Boolean,
                         fromName: String)

object Options {

  implicit val format: OFormat[Options] = Json.format[Options]
}

object EmailContent {
  implicit val format: OFormat[EmailContent] = Json.format[EmailContent]
}

object Channel {
  val EMAIL = "email"
}
