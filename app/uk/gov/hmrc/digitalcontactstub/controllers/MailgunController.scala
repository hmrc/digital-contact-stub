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

package uk.gov.hmrc.digitalcontactstub.controllers

import play.api.Logging
import play.api.data.Form
import play.api.data.Forms.{ list, mapping, optional, text }
import play.api.libs.json.Json
import play.api.mvc.{ Action, AnyContent, ControllerComponents }
import uk.gov.hmrc.digitalcontactstub.mailgun.StubState.forAll
import uk.gov.hmrc.digitalcontactstub.mailgun.model.{ Email, ValidatedDomain }
import uk.gov.hmrc.digitalcontactstub.mailgun.StubState
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendBaseController

import javax.inject.Inject

class MailgunMessageController @Inject() (override val controllerComponents: ControllerComponents)
    extends BackendBaseController with Logging {

  val sendMessageForm: Form[Email] = Form(
    mapping(
      "from"             -> text,
      "to"               -> optional(text),
      "subject"          -> text,
      "text"             -> optional(text),
      "html"             -> optional(text),
      "cc"               -> optional(text),
      "bcc"              -> optional(text),
      "o:tag"            -> list(text),
      "o:tracking-opens" -> optional(text)
    )(Email.toEmail)(Email.fromEmail)
  )

  def sendEmailToMailgunQueue(version: String, domain: ValidatedDomain): Action[AnyContent] = Action {
    implicit request =>
      logger.debug(s"Mailgun send email reqest received for $version")
      sendMessageForm
        .bindFromRequest()
        .fold(
          hasErrors = form => BadRequest(s"""Form fields received were not valid:
                                            |${request.body}
                                            |
                                            |Errors:
                                            |${form.errors}""".stripMargin),
          success = message => {
            val id = StubState(domain.domain).addSentMessage(message)
            Ok(Json.parse(s"""{
                             |"id":"$id",
                             |"message":"Queued. Thank you."
                             |}""".stripMargin))
          }
        )
  }

  def getMailgunQueue(domain: String): Action[AnyContent] = Action {
    Ok(Json.toJson(StubState(domain).getSentMessages.map(_._2)))
  }

  def reset(): Action[AnyContent] = Action {
    forAll(_.reset())
    Ok
  }

}
