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

package uk.gov.hmrc.digitalcontactstub.controllers

import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.{CREATED, OK}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Headers
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.digitalcontactstub.repositories.EmailQueueRepository
import uk.gov.hmrc.mongo.test.MongoSupport

class EmailProviderControllerSpec
    extends PlaySpec
    with GuiceOneAppPerSuite
    with BeforeAndAfterEach
    with MongoSupport {

  "POST to /digital-contact-stub/imi/v2/messages" must {
    "return CREATED" in new TestSetUp {
      val controller = app.injector.instanceOf[EmailProviderController]

      val result = controller.sendEmailToImiQueue(postFakeRequest)

      status(result) mustBe CREATED
    }
  }

  "GET to /digital-contact-stub/imi/messages" must {
    "return Ok" in new TestSetUp {
      val controller = app.injector.instanceOf[EmailProviderController]
      controller.sendEmailToImiQueue(postFakeRequest).futureValue

      val fakeRequest = FakeRequest("GET", "/digital-contact-stub/imi/messages")

      val result = controller.viewQueue(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) must include("senderEmail@gmail.com")
    }
  }

  "GET to digital-contact-stub/imi/messages/:id" must {
    "return Ok" in new TestSetUp {
      val controller = app.injector.instanceOf[EmailProviderController]
      controller.sendEmailToImiQueue(postFakeRequest).futureValue

      val fakeRequest = FakeRequest(
        "GET",
        "digital-contact-stub/imi/messages/1daa430a-e54e-48f8-9fac-dfc0971b85a5")

      val result = controller.viewQueue(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) must include("senderEmail@gmail.com")
    }
  }

  override def beforeEach() = {
    super.beforeEach()
    val repository = app.injector.instanceOf[EmailQueueRepository]
    repository.cleanUp
  }

  class TestSetUp {
    val payloadString =
      """{"channel":"email","from":"test@hmrc.com","to":[{"email":["senderEmail@gmail.com"],"correlationId":"1daa430a-e54e-48f8-9fac-dfc0971b85a5"}],"callbackData":"","options":{"trackClicks":true,"trackOpens":true,"fromName":"HMRC"},"content":{"type":"type","subject":"subject","replyTo":{"value":"replayTo@gmail.com"},"text":"text","html":"html"}}"""

    val payloadJson = Json.parse(payloadString)

    val postFakeRequest: FakeRequest[JsValue] =
      FakeRequest("POST",
                  "/digital-contact-stub/imi/v2/messages",
                  Headers((Helpers.CONTENT_TYPE, "application/json")),
                  payloadJson)

  }
}
