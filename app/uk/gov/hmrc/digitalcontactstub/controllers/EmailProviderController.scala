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

import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import uk.gov.hmrc.digitalcontactstub.models.email.EmailContent
import uk.gov.hmrc.digitalcontactstub.models.email.EmailContent.format
import uk.gov.hmrc.digitalcontactstub.models.email.EmailQueued.emailQueuedFormat
import uk.gov.hmrc.digitalcontactstub.service.EmailQueueService
import uk.gov.hmrc.digitalcontactstub.views.html.ViewEmailQueue
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmailProviderController @Inject()(
    cc: MessagesControllerComponents,
    emailQueueService: EmailQueueService,
    viewEmailQueue: ViewEmailQueue)(implicit ec: ExecutionContext)
    extends FrontendController(cc) {

  def sendEmailToImiQueue: Action[JsValue] = Action.async(parse.json) {
    implicit request =>
      withJsonBody[EmailContent] { email =>
        emailQueueService
          .addToQueue(email)
          .map(result => Created(Json.toJson(result)))
      }
  }

  def resetQueue: Action[AnyContent] = Action.async { implicit request =>
    emailQueueService.reset.map(result => Accepted(Json.toJson(result)))
  }

  def viewQueue: Action[AnyContent] = Action.async { implicit request =>
    emailQueueService.getQueue.map(x => Ok(viewEmailQueue(x)))
  }

  def viewQueueItem(id: String): Action[AnyContent] = Action.async { implicit request =>
    emailQueueService.getQueueItem(id).map(x => Ok(Json.toJson(x)))
  }

  def deleteQueueItem(id: String): Action[AnyContent] = Action.async { implicit request =>
    emailQueueService
      .deleteQueueItem(id)
      .map(_ => Redirect(Call("GET", "/digital-contact-stub/imi/messages")))
  }

}
