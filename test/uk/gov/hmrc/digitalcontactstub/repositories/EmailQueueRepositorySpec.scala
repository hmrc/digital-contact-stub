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

import org.scalatestplus.play.PlaySpec
import uk.gov.hmrc.digitalcontactstub.models.email._
import uk.gov.hmrc.mongo.test.MongoSupport
import scala.concurrent.ExecutionContext.Implicits.global

class EmailQueueRepositorySpec extends PlaySpec with MongoSupport {

  "save" must {
    "add to email_queue" in new SetUp {
      emailQueueRepository.save(emailContent).futureValue mustBe true
    }
  }

  "cleanUp" must {
    "remove all documents in collection" in new SetUp {
      emailQueueRepository.save(emailContent).futureValue mustBe true
      emailQueueRepository.cleanUp.futureValue mustBe true
      emailQueueRepository.collection.find().toFuture().futureValue mustBe Nil
    }
  }

  "findAll" must {
    "get all documents in collection" in new SetUp {
      emailQueueRepository.cleanUp.futureValue mustBe true
      emailQueueRepository.save(emailContent).futureValue mustBe true
      emailQueueRepository.save(emailContent).futureValue mustBe true
      emailQueueRepository.collection
        .find()
        .toFuture()
        .futureValue
        .size mustBe 2
      emailQueueRepository.cleanUp.futureValue mustBe true
    }
  }

  class SetUp {
    val emailContent = EmailContent(
      Channel.EMAIL,
      "test@gmail.com",
      List(To(List(("sentto@gmail.com")), "correlationId")),
      "",
      Options(true, false, "name"),
      Content("type", "subject", None, "text", "html")
    )
    val emailQueueRepository = new EmailQueueRepository(mongoComponent)
  }
}
