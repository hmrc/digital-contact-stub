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
import uk.gov.hmrc.digitalcontactstub.models.email.{ EmailContent, EmailQueued }
import uk.gov.hmrc.digitalcontactstub.repositories.EmailQueueRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class EmailQueueService @Inject() (
  emailQueueRepository: EmailQueueRepository,
  emailEventsConnector: EmailEventsConnector
) {

  private def timeStamp = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    LocalDateTime.now().format(formatter)
  }

  def addToQueue(emailContent: EmailContent)(implicit ec: ExecutionContext): Future[EmailQueued] = {
    val transId = UUID.randomUUID()
    for {
      _ <- emailQueueRepository.save(emailContent)
      _ <- emailEventsConnector.markSent(transId.toString)
      queued = EmailQueued(timeStamp, transId.toString, emailContent.to.map(_.correlationId).head, "queued")
    } yield queued
  }

  def reset: Future[Boolean] = emailQueueRepository.deleteAll

  def getQueue = emailQueueRepository.findAll
  def getQueueItem(id: String) = emailQueueRepository.findItem(id)
  def getQueueItemByEmail(email: String) =
    emailQueueRepository.findItemByEmail(email)
  def deleteQueueItem(id: String) = emailQueueRepository.deleteItem(id)
}
