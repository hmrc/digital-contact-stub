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

package uk.gov.hmrc.digitalcontactstub.service

import uk.gov.hmrc.digitalcontactstub.connector.EmailEventsConnector
import uk.gov.hmrc.digitalcontactstub.models.email.{
  DeliveryDescription,
  DeliveryInfo,
  DeliveryInfoNotification,
  DeliveryStatus,
  EmailContent,
  EmailQueued,
  Event,
  To
}
import uk.gov.hmrc.digitalcontactstub.repositories.EmailQueueRepository

import java.time.LocalDateTime
import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import java.time.format.DateTimeFormatter
import java.util

@Singleton
class EmailQueueService @Inject()(
    emailQueueRepository: EmailQueueRepository,
    emailEventsConnector: EmailEventsConnector)(implicit ec: ExecutionContext) {

  private def timeStamp = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    LocalDateTime.now().format(formatter)
  }

  private def generateEvent(receipt: String,
                            to: List[To],
                            callbackData: String): Event = {
    val firstDestination = to.head
    val deliveryInfo = DeliveryInfo(LocalDateTime.now(),
                                    DeliveryDescription.Submitted,
                                    "",
                                    "email",
                                    "",
                                    firstDestination.email.head,
                                    "email",
                                    DeliveryStatus.Submitted)

    val deliveryInfoNotification = DeliveryInfoNotification(
      deliveryInfo,
      "",
      UUID.randomUUID(),
      callbackData,
      UUID.fromString(firstDestination.correlationId),
    )

    val event = Event(deliveryInfoNotification)

    receipt match {
      case "submitted" =>
        event.copy(
          deliveryInfoNotification.copy(
            deliveryInfo.copy(Description = DeliveryDescription.Submitted,
                              code = "7501",
                              deliveryStatus = DeliveryStatus.Submitted)))
      case "read" =>
        event.copy(
          deliveryInfoNotification.copy(
            deliveryInfo.copy(Description = DeliveryDescription.Read,
                              code = "7501",
                              deliveryStatus = DeliveryStatus.Read)))
      case "delivered" =>
        event.copy(
          deliveryInfoNotification.copy(
            deliveryInfo.copy(Description = DeliveryDescription.Delivered,
                              code = "7501",
                              deliveryStatus = DeliveryStatus.Delivered)))
      case "bounced" =>
        event.copy(
          deliveryInfoNotification.copy(
            deliveryInfo.copy(Description =
                                DeliveryDescription.Transient_General,
                              code = "7501",
                              deliveryStatus = DeliveryStatus.Bounce)))
      case "complaint" =>
        event.copy(
          deliveryInfoNotification.copy(
            deliveryInfo.copy(Description = DeliveryDescription.Submitted,
                              code = "7501",
                              deliveryStatus = DeliveryStatus.Submitted)))
      case _ => throw new RuntimeException("receipt not recognised")

    }
  }

  private def sendEvents(receipts: Seq[String],
                         to: List[To],
                         callbackData: String) = {
    Future
      .sequence(receipts.map { r =>
        emailEventsConnector.send(generateEvent(r, to, callbackData))
      })
      .map(_ => ())
  }

  def addToQueue(emailContent: EmailContent)(
      implicit ec: ExecutionContext): Future[EmailQueued] = {
    for {
      _ <- emailQueueRepository.save(emailContent)
      _ <- sendEvents(emailContent.requestedReceipts,
                      emailContent.to,
                      emailContent.callbackData)
      queued = EmailQueued(timeStamp,
                           UUID.randomUUID().toString,
                           emailContent.to.map(_.correlationId).head,
                           "queued")
    } yield queued
  }

  def reset: Future[Boolean] = emailQueueRepository.cleanUp

  def getQueue = emailQueueRepository.findAll
  def getQueueItem(id: String) = emailQueueRepository.findItem(id)
  def deleteQueueItem(id: String) = emailQueueRepository.deleteItem(id)
}
