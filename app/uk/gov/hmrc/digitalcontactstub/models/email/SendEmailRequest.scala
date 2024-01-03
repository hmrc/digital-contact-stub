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

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class SendEmailRequest(
    to: List[String],
    templateId: String,
    parameters: Map[String, String],
    tags: Map[String, String] = Map.empty[String, String],
    force: Boolean,
    eventUrl: Option[String],
    onSendUrl: Option[String],
    auditData: Map[String, String],
    alertQueue: Option[String] = None,
    emailSource: Option[String] = None,
    replyToAddress: Option[EmailAddress] = None,
    enrolment: Option[String] = None
)

object SendEmailRequest {

  implicit val format: Format[SendEmailRequest] = new Format[SendEmailRequest] {

    def reads(json: JsValue): JsResult[SendEmailRequest] =
      (
        (__ \ "to").read[List[String]] and
          (__ \ "templateId").read[String] and
          (__ \ "parameters")
            .readNullable[Map[String, String]]
            .map(_.getOrElse(Map.empty)) and
          (__ \ "tags")
            .readNullable[Map[String, String]]
            .map(_.getOrElse(Map.empty)) and
          (__ \ "force").readNullable[Boolean].map(_.getOrElse(false)) and
          (__ \ "eventUrl").readNullable[String] and
          (__ \ "onSendUrl").readNullable[String] and
          (__ \ "auditData")
            .readNullable[Map[String, String]]
            .map(_.getOrElse(Map.empty)) and
          (__ \ "alertQueue").readNullable[String] and
          (__ \ "emailSource").readNullable[String] and
          (__ \ "replyToAddress").readNullable[EmailAddress] and
          (__ \ "enrolment").readNullable[String]
      )(SendEmailRequest.apply _)
        .reads(json)
        .flatMap(sendEmailRequest => {
          if (sendEmailRequest.to.isEmpty) {
            JsError(__ \ "to", "recipients list is empty")
          } else {
            JsSuccess(sendEmailRequest)
          }
        })

    def writes(o: SendEmailRequest): JsValue =
      Json.writes[SendEmailRequest].writes(o)
  }
}
