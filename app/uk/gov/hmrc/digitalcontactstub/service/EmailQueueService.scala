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

import com.ibm.icu.text.SimpleDateFormat
import org.joda.time.{DateTimeZone, LocalDate, LocalDateTime}
import uk.gov.hmrc.digitalcontactstub.models.email.{EmailContent, EmailQueued}
import uk.gov.hmrc.digitalcontactstub.repositories.EmailQueueRepository

import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalUnit
import java.time.{Clock, Instant, ZoneId, ZoneOffset, ZonedDateTime}
import java.util.{TimeZone, UUID}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmailQueueService @Inject()(emailQueueRepository: EmailQueueRepository) {

  private def now = Instant.now()
  private def uuid = UUID.randomUUID()

  def addToQueue(emailContent: EmailContent)(implicit ec: ExecutionContext): Future[EmailQueued] = {

   emailQueueRepository.save(emailContent).map(_ => EmailQueued(now.toString,uuid.toString, emailContent.to.map(_.correlationId).head, "queued"))
  }


}
