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

package uk.gov.hmrc.digitalcontactstub.controllers.imi

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.PlaySpec
import play.api.http.Status.CREATED
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.{ Headers, Result }
import play.api.test.Helpers.{ defaultAwaitTimeout, status }
import play.api.test.{ FakeRequest, Helpers }
import uk.gov.hmrc.digitalcontactstub.controllers.imi.EmailProviderController
import uk.gov.hmrc.digitalcontactstub.models.email.*
import uk.gov.hmrc.digitalcontactstub.service.{ ConsentQueueService, EmailQueueService }
import uk.gov.hmrc.digitalcontactstub.views.html.ViewEmailQueue
import uk.gov.hmrc.mongo.test.MongoSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ ExecutionContext, Future }
import scala.io.Source

class EmailProviderControllerSpec extends PlaySpec with BeforeAndAfterEach with MongoSupport {

  "POST to /digital-contact-stub/imi/v2/messages" must {
    "return CREATED" in new TestSetUp {
      when(emailQueueService.addToQueue(any[EmailContent])(any[ExecutionContext]))
        .thenReturn(Future.successful(EmailQueued("", "", "", "")))
      val result: Future[Result] = controller.sendEmailToImiQueue(postFakeRequest)
      val statusCode: Int = status(result)
      statusCode mustBe CREATED
    }
  }

  override def beforeEach(): Unit =
    super.beforeEach()

  class TestSetUp {
    val emailQueueService = mock[EmailQueueService]
    val consentQueueService = mock[ConsentQueueService]
    val viewEmailQueue = mock[ViewEmailQueue]

    def readFile(fileName: String): String = {
      val resource = Source.fromURL(getClass.getResource("/" + fileName))
      val resourceAsString = resource.mkString
      resource.close()
      resourceAsString
    }

    val payload = Json.parse(readFile("imi-payload.json"))

    val postFakeRequest: FakeRequest[JsValue] =
      FakeRequest(
        "POST",
        "/digital-contact-stub/imi/v2/messages",
        Headers((Helpers.CONTENT_TYPE, "application/json")),
        payload
      )
    val controller = new EmailProviderController(
      Helpers.stubMessagesControllerComponents(),
      emailQueueService,
      consentQueueService,
      viewEmailQueue
    )
  }
}
