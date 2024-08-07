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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{ times, verify, when }
import org.mockito.MockitoSugar.mock
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import uk.gov.hmrc.digitalcontactstub.models.email._
import uk.gov.hmrc.digitalcontactstub.repositories.EmailQueueRepository
import uk.gov.hmrc.digitalcontactstub.utils.Encryption
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EmailQueueServiceSpec extends PlaySpec with ScalaFutures {

  "EmailQueueService" should {
    "save email content to repository and send events" in new TestSetup {
      when(mockEmailQueueRepository.save(any[EmailContent]))
        .thenReturn(Future.successful(true))

      emailQueueService.addToQueue(emailContent).futureValue
      verify(mockEmailQueueRepository, times(1)).save(emailContent)
    }

    class TestSetup {

      val mockEmailQueueRepository = mock[EmailQueueRepository]

      val encryption = mock[Encryption]

      val emailContent = EmailContent(
        Channel.EMAIL,
        "test@gmail.com",
        List(To(List("sentto@gmail.com"), "1daa430a-e54e-48f8-9fac-dfc0971b85a5")),
        "",
        Options(true, false, "name"),
        ContactPolicy("KMdrUZptSrOQbemFdB7WAQ", true, true),
        Seq("submitted", "delivered", "bounce", "complaint", "read"),
        Content("type", "subject", None, "text", "html"),
        "https://webhook.site/8517c49d-519e-4823-9ad9-9886c26e9a15"
      )
      val emailQueueService = new EmailQueueService(mockEmailQueueRepository, encryption)
    }
  }
}
