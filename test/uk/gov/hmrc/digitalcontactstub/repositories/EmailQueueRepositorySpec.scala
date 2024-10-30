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

package uk.gov.hmrc.digitalcontactstub.repositories

import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec
import uk.gov.hmrc.digitalcontactstub.models.email.*
import uk.gov.hmrc.mongo.test.MongoSupport
import org.mongodb.scala.ObservableFuture
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.duration._

import scala.annotation.nowarn
import scala.concurrent.ExecutionContext.Implicits.global

class EmailQueueRepositorySpec extends PlaySpec with MongoSupport with BeforeAndAfterEach with ScalaFutures {

  implicit val defaultPatience:PatienceConfig = PatienceConfig(timeout = 5.seconds, interval = 100.millis)
  "save" must {
    "add to email_queue" in new SetUp {
      emailQueueRepository.save(emailContent).futureValue mustBe true
    }
  }

  "cleanUp" must {
    "remove all documents in collection" in new SetUp {
      emailQueueRepository.save(emailContent).futureValue mustBe true
      emailQueueRepository.deleteAll.futureValue mustBe true
      emailQueueRepository.collection.find().toFuture().futureValue mustBe Nil
    }
  }

  "findAll" must {
    "get all documents in collection" in new SetUp {
      emailQueueRepository.save(emailContent).futureValue mustBe true
      emailQueueRepository.save(emailContent).futureValue mustBe true
      emailQueueRepository.collection
        .find()
        .toFuture()
        .futureValue
        .size mustBe 2
    }

    "findItem" must {
      "get document in collection that matches collectionId" in new SetUp {
        emailQueueRepository.save(emailContent).futureValue mustBe true

        val result =
          emailQueueRepository.findItem("1daa430a-e54e-48f8-9fac-dfc0971b85a5")
        result.futureValue.head.to
          .map(_.correlationId)
          .head mustBe "1daa430a-e54e-48f8-9fac-dfc0971b85a5"
      }
    }
  }

  @nowarn("msg=discarded non-Unit value")
  override def afterEach(): Unit = {
    super.beforeEach()
    val setup = new SetUp
    setup.emailQueueRepository.deleteAll.futureValue
  }

  class SetUp {
    val emailContent = EmailContent(
      Channel.EMAIL,
      "test@gmail.com",
      List(To(List("sentto@gmail.com"), "1daa430a-e54e-48f8-9fac-dfc0971b85a5")),
      "",
      Options(true, false, "name"),
      ContactPolicy("KMdrUZptSrOQbemFdB7WAQ", true, true),
      Seq("submitted", "delivered", "not verified", "invalid", "bounced", "complaint", "read", "failed"),
      Content("type", "subject", None, "text", "html"),
      "https://webhook.site/8517c49d-519e-4823-9ad9-9886c26e9a15"
    )
    val emailQueueRepository = new EmailQueueRepository(mongoComponent)
  }
}
