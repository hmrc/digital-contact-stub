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

package uk.gov.hmrc.digitalcontactstub.mailgun

import java.util.UUID
import java.util.concurrent.atomic.AtomicLong
import com.google.common.collect.{ EvictingQueue, Queues }
import model._
import uk.gov.hmrc.digitalcontactstub.mailgun.config.Keys

trait StubState {

  private val sentMessages = Queues.synchronizedQueue(EvictingQueue.create[(UUID, Email)](200))
  private val failedEventsQueue = Queues.synchronizedQueue(EvictingQueue.create[MailgunEvent](200))
  private val bounceDeletes = Queues.synchronizedQueue(EvictingQueue.create[BounceDelete](200))
  private val emailSent = new AtomicLong(0)

  def clearStore(): Unit = {
    sentMessages.clear()
    failedEventsQueue.clear()
    bounceDeletes.clear()
  }

  def reset(): Unit = {
    clearStore()
    emailSent.set(0)
  }

  def emailSentCount: EmailSentCount = EmailSentCount(emailSent.get)

  def recordBounceDelete(bounceDelete: BounceDelete): Boolean = bounceDeletes.add(bounceDelete)

  def getBounceDeletes: List[BounceDelete] = bounceDeletes.toArray(new Array[BounceDelete](0)).toList

  def addSentMessage(message: Email): UUID = {
    val id = UUID.randomUUID
    emailSent.incrementAndGet()
    sentMessages.add(id -> message)
    id
  }

  def getSentMessages: List[(UUID, Email)] = sentMessages.toArray(new Array[(UUID, Email)](0)).toList

  def addFailedEvent(event: MailgunEvent): Boolean = failedEventsQueue.add(event)

  def getFailedEventsQueue: List[MailgunEvent] = failedEventsQueue.toArray(new Array[MailgunEvent](0)).toList

}

object StubState {

  case object ExampleDomainStubState extends StubState
  case object VoaDomainStubState extends StubState

  def apply(domain: String): StubState = domain match {
    case Keys.defaultDomain => ExampleDomainStubState
    case Keys.voaDomain     => VoaDomainStubState
    case _                  => throw new IllegalArgumentException(s"$domain is not a valid domain")
  }

  lazy val all: Map[String, StubState] = Map(
    Keys.defaultDomain -> ExampleDomainStubState,
    Keys.voaDomain     -> VoaDomainStubState
  )

  lazy val allStubs: List[StubState] = StubState.all.values.toList

  def forAll(func: StubState => Unit): Unit = allStubs.foreach(func)
}
