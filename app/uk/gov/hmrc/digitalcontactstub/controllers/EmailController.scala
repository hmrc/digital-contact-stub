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

package uk.gov.hmrc.digitalcontactstub.controllers

import play.api.Logging
import play.api.libs.json.JsValue
import play.api.mvc.{ Action, AnyContent, ControllerComponents }
import uk.gov.hmrc.digitalcontactstub.models.email.SendEmailRequest
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.digitalcontactstub.models.email.SendEmailRequest._

import javax.inject.{ Inject, Singleton }
import scala.concurrent.Future

@Singleton()
class EmailController @Inject() (cc: ControllerComponents) extends BackendController(cc) with Logging {

  def sendTemplatedEmail: Action[AnyContent] = Action.async {
    // Example body
    // {
    //  "to": ["example@domain.com"],
    //  "templateId": "my-lovely-template",
    //  "parameters": {
    //    "name": "Mr Joe Bloggs",
    //    "verificationLink": "http://some.url/test"
    //  },
    //  "force": false,
    //  "eventUrl": "http://some.other/url",
    //  "onSendUrl": "http://some/send/check/url"
    // }
    // Responds with 202 status if the request is valid and has been queued for sending.

    Future.successful(Accepted)
  }

  def sendEmail(domain: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[SendEmailRequest] { _ =>
      logger.debug(s"request received to send email for domain $domain")
      Future.successful(Accepted)
    }
  }

}
