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

import play.api.libs.json.Json
import uk.gov.hmrc.digitalcontactstub.models.email.{ EmailContent, EmailQueued, QueueItem }
import uk.gov.hmrc.digitalcontactstub.repositories.EmailQueueRepository
import uk.gov.hmrc.digitalcontactstub.utils.Encryption

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.{ Base64, UUID }
import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Success, Try }

@Singleton
class EmailQueueService @Inject() (
  emailQueueRepository: EmailQueueRepository,
  encryption: Encryption
)(implicit ec: ExecutionContext) {

  private def timeStamp = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    LocalDateTime.now().format(formatter)
  }

  private def decodeString(s: String) = {
    val decodedBytes = Base64.getDecoder.decode(s)
    val decodedString = new String(decodedBytes)
    Json.parse(decodedString).as[Map[String, String]]
  }

  private def decryptTags(tags: Map[String, String]) =
    tags.map { case (key, value) =>
      val decryptedValue: String = Try(encryption.decrypt(value)) match {
        case Success(text) => text.value
        case _             => value
      }
      (key, decryptedValue)
    }

  def addToQueue(emailContent: EmailContent)(implicit ec: ExecutionContext): Future[EmailQueued] = {
    val transId = UUID.randomUUID()
    for {
      _ <- emailQueueRepository.save(emailContent)
      queued = EmailQueued(timeStamp, transId.toString, emailContent.to.map(_.correlationId).head, "queued")
    } yield queued
  }

  def reset: Future[Boolean] = emailQueueRepository.deleteAll

  def getQueue = emailQueueRepository.findAll
  def getQueueItem(id: String) = emailQueueRepository.findItem(id)
  def getQueueItemByEmail(email: String) =
    emailQueueRepository
      .findItemByEmail(email)
      .map(list =>
        list.map(item =>
          QueueItem(
            item.channel,
            item.from,
            item.to,
            decryptTags(decodeString(item.callbackData)),
            item.options,
            item.contactPolicy,
            item.requestedReceipts,
            item.content,
            item.notifyUrl
          )
        )
      )
  def deleteQueueItem(id: String) = emailQueueRepository.deleteItem(id)
}
