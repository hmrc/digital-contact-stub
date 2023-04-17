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
import uk.gov.hmrc.digitalcontactstub.models.email.{DeliveryDescription, DeliveryInfo, DeliveryInfoNotification, DeliveryStatus, EmailContent, EmailQueued, Event}
import uk.gov.hmrc.digitalcontactstub.repositories.EmailQueueRepository

import java.time.LocalDateTime
import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import java.time.format.DateTimeFormatter
import java.util

@Singleton
class EmailQueueService @Inject()(emailQueueRepository: EmailQueueRepository, emailEventsConnector: EmailEventsConnector)(implicit ec: ExecutionContext) {

  private def timeStamp = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    LocalDateTime.now().format(formatter)
  }

  private def eventGenerator(receipts: Seq[String]) = {


    val fList: Seq[Future[Int]] = receipts.map { r =>
      emailEventsConnector.send(Event(DeliveryInfoNotification(
        DeliveryInfo(LocalDateTime.now(), DeliveryDescription.Delivered, "", "", "email", "", "", DeliveryStatus.Delivered),
        "",
        UUID.randomUUID(),
        Map.empty,
        UUID.randomUUID(),
      )))
    }
    val r: Future[Seq[Int]] = Future.sequence(fList)

r.map(_ => ())
  }





  def addToQueue(emailContent: EmailContent)(
      implicit ec: ExecutionContext): Future[EmailQueued] = {
    for {
      _ <- emailQueueRepository.save(emailContent)
      _ <- eventGenerator(emailContent.requestedReceipts)
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
