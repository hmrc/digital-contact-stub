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
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc._
import uk.gov.hmrc.digitalcontactstub.models.email.EmailContent.format
import uk.gov.hmrc.digitalcontactstub.models.email.EmailQueued.emailQueuedFormat
import uk.gov.hmrc.digitalcontactstub.models.email.{ ConsentItem, EmailContent, ImiConsent }
import uk.gov.hmrc.digitalcontactstub.service.{ ConsentQueueService, EmailQueueService }
import uk.gov.hmrc.digitalcontactstub.views.html.ViewEmailQueue
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class EmailProviderController @Inject() (
  cc: MessagesControllerComponents,
  emailQueueService: EmailQueueService,
  consentQueueService: ConsentQueueService,
  viewEmailQueue: ViewEmailQueue
)(implicit ec: ExecutionContext)
    extends FrontendController(cc) with Logging {

  def sendEmailToImiQueue: Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[EmailContent] { email =>
      emailQueueService
        .addToQueue(email)
        .map(result => Created(Json.toJson(result)))
    }
  }

  def saveContactPolicyItem(groupId: String): Action[JsValue] =
    Action.async(parse.json) { implicit request =>
      logger.info(s"ContactPolicyItemSaved for groupId $groupId")
      withJsonBody[ImiConsent] { consent =>
        consentQueueService.save(consent) match {
          case _ => Future.successful(NoContent)
        }

      }
    }

  def getContactPolicyItem(groupId: String, address: Option[String]): Action[AnyContent] =
    Action.async { _ =>
      logger.info(s"Contact policy item for $groupId and $address")
      val data = address match {
        case Some(address) =>
          consentQueueService.queue
            .find(_.address == address)
            .toList
            .map(i =>
              ConsentItem(i.channel, i.address, i.consent, i.reason, Instant.now.truncatedTo(ChronoUnit.MILLIS))
            )
        case None =>
          consentQueueService.queue.toList
            .map(i =>
              ConsentItem(i.channel, i.address, i.consent, i.reason, Instant.now.truncatedTo(ChronoUnit.MILLIS))
            )
      }
      Future.successful(Ok(Json.toJson(data)))
    }

  def deleteContactPolicyItem(groupId: String, address: String) = Action.async { _ =>
    logger.info(s"Contact policy delete for $groupId and $address")
    Future.successful(Ok(consentQueueService.remove(address).getOrElse(false).toString))
  }

  def resetContactPolicyItem = Action.async { _ =>
    consentQueueService.resetQueue()
    Future.successful(Ok("queue cleared"))
  }

  def resetQueue: Action[AnyContent] = Action.async { _ =>
    emailQueueService.reset.map(result => Accepted(Json.toJson(result)))
  }

  def viewQueue: Action[AnyContent] = Action.async { implicit request =>
    emailQueueService.getQueue.map(x => Ok(viewEmailQueue(x)))
  }

  def getQueue: Action[AnyContent] = Action.async { _ =>
    emailQueueService.getQueue.map(x => Ok(Json.toJson(x)))
  }

  def viewQueueItem(id: String): Action[AnyContent] = Action.async { _ =>
    emailQueueService.getQueueItem(id).map(x => Ok(Json.toJson(x)))
  }

  def viewQueueItemByEmail(email: String): Action[AnyContent] = Action.async { _ =>
    emailQueueService.getQueueItemByEmail(email).map(x => Ok(Json.toJson(x)))
  }

  def deleteQueueItem(id: String): Action[AnyContent] = Action.async { _ =>
    emailQueueService
      .deleteQueueItem(id)
      .map(_ => Redirect(Call("GET", "/digital-contact-stub/imi/viewMessages")))
  }

}
