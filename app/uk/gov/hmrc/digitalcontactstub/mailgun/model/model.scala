/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.digitalcontactstub.mailgun.model

import play.api.libs.functional.syntax._
import play.api.libs.json._

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.UUID
import scala.util.Random
case class BounceDelete(address: String, date: Instant = Instant.now)
object BounceDelete {

  implicit val instantWrite: Writes[Instant] = new Writes[Instant] {
    def writes(instant: Instant): JsValue =
      JsString(DateTimeFormatter.ISO_INSTANT.format(instant.truncatedTo(ChronoUnit.MILLIS)))
  }

  implicit val formats: OFormat[BounceDelete] = Json.format[BounceDelete]
}
case class Email(
  from: String,
  to: Option[String],
  subject: String,
  text: Option[String],
  html: Option[String],
  cc: Option[String],
  bcc: Option[String],
  date: Instant,
  tags: List[String],
  openTracking: Option[String]
)
object Email {
  implicit val instantWrite: Writes[Instant] = new Writes[Instant] {
    def writes(instant: Instant): JsValue =
      JsString(DateTimeFormatter.ISO_INSTANT.format(instant.truncatedTo(ChronoUnit.MILLIS)))
  }
  implicit val formats: OFormat[Email] = Json.format[Email]
  def toEmail(
    from: String,
    to: Option[String],
    subject: String,
    text: Option[String],
    html: Option[String],
    cc: Option[String],
    bcc: Option[String],
    tags: List[String],
    openTracking: Option[String]
  ): Email =
    Email(
      from = from,
      to = to,
      subject = subject,
      text = text,
      html = html,
      cc = cc,
      bcc = bcc,
      date = Instant.now,
      tags = tags,
      openTracking = openTracking
    )
  def fromEmail(email: Email): Option[
    (
      String,
      Option[String],
      String,
      Option[String],
      Option[String],
      Option[String],
      Option[String],
      List[String],
      Option[String]
    )
  ] = {
    import email._
    Some((from, to, subject, text, html, cc, bcc, tags, openTracking))
  }
}
case class TestEvent(
  time: Instant,
  address: String,
  code: Option[Int],
  event: Option[String],
  messageId: Option[String],
  severity: Option[String],
  tags: Option[Seq[String]]
) {
  def generateShortId: String = (Random.alphanumeric take 22).mkString
  def toMailgunEvent: MailgunEvent = MailgunEvent(
    id = generateShortId,
    event = event.getOrElse("failed"),
    messageId = messageId.getOrElse(UUID.randomUUID().toString),
    recipient = address,
    timestamp = BigDecimal(time.toEpochMilli / 1000),
    deliveryStatusCode = code,
    severity = severity
  )
}
object TestEvent {

  implicit val instantWrite: Writes[Instant] = new Writes[Instant] {
    def writes(instant: Instant): JsValue =
      JsString(DateTimeFormatter.ISO_INSTANT.format(instant.truncatedTo(ChronoUnit.MILLIS)))
  }
  implicit val formats: OFormat[TestEvent] = Json.format[TestEvent]
}
case class TestEvents(events: Seq[TestEvent])
object TestEvents {
  implicit val formats: OFormat[TestEvents] = Json.format[TestEvents]
}

object MailgunEvent {
  implicit val writes: Writes[MailgunEvent] = (
    (__ \ "id").write[String] and
      (__ \ "event").write[String] and
      (__ \ "message" \ "headers" \ "message-id").write[String] and
      (__ \ "recipient").write[String] and
      (__ \ "timestamp").write[BigDecimal] and
      (__ \ "delivery-status" \ "code").writeNullable[Int] and
      (__ \ "severity").writeNullable[String] and
      (__ \ "tags").write[Seq[String]]
  )(unlift(MailgunEvent.unapply))
}

case class MailgunEvent(
  id: String,
  event: String,
  messageId: String,
  recipient: String,
  timestamp: BigDecimal,
  deliveryStatusCode: Option[Int],
  severity: Option[String],
  tags: Seq[String] = Seq.empty
)
case class MailgunEventsPaging(next: String, previous: String)
object MailgunEventsPaging {
  implicit val formats: OFormat[MailgunEventsPaging] = Json.format[MailgunEventsPaging]
}
case class MailgunEvents(items: Seq[MailgunEvent], paging: MailgunEventsPaging)
object MailgunEvents {
  implicit val writes: OWrites[MailgunEvents] = Json.writes[MailgunEvents]
}
case class EmailSentCount(count: Long)
object EmailSentCount {
  implicit val formats: OFormat[EmailSentCount] = Json.format[EmailSentCount]
}
