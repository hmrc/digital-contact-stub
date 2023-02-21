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

import play.api.i18n.I18nSupport
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import uk.gov.hmrc.digitalcontactstub.models.email.EmailQueued.emailQueuedFormat
import uk.gov.hmrc.digitalcontactstub.models.email.{EmailContent, EmailQueued}
import uk.gov.hmrc.digitalcontactstub.service.EmailQueueService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import EmailContent._
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EmailProviderController @Inject()(cc: MessagesControllerComponents, emailQueueService: EmailQueueService
                                        )(implicit ec: ExecutionContext)
  extends FrontendController(cc) with I18nSupport {

  def sendEmailToImi: Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[EmailContent]{email =>
      emailQueueService.addToQueue(email) map {
        case true =>  Created(Json.toJson(EmailQueued("time", "id", "c", "s")))
        case false => ServiceUnavailable

      }

    }

  }

}
