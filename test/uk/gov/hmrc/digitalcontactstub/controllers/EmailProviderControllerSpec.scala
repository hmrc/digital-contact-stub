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

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.{CREATED, OK}
import play.api.libs.json.Json
import play.api.mvc.Headers
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}

class EmailProviderControllerSpec extends PlaySpec with GuiceOneAppPerSuite {

  "POST to /digital-contact-stub/imi/v2/messages" must {
    "return CREATED" in {

      val payloadString =
        """{"channel":"email","from":"test@hmrc.com","to":[{"email":["senderEmail@gmail.com"],"correlationId":"correlationId"}],"callbackData":"","options":{"trackClicks":true,"trackOpens":true,"fromName":"HMRC"},"content":{"type":"type","subject":"subject","replyTo":{"value":"replayTo@gmail.com"},"text":"text","html":"html"}}"""

      val payloadJson = Json.parse(payloadString)

      val fakeRequest =
        FakeRequest("POST",
                    "/digital-contact-stub/imi/v2/messages",
                    Headers((Helpers.CONTENT_TYPE, "application/json")),
                    payloadJson)

      val controller = app.injector.instanceOf[EmailProviderController]

      val result = controller.sendEmailToImiQueue(fakeRequest)

      status(result) mustBe CREATED
    }
  }

  "GET to /digital-contact-stub/imi/messages" must {
    "return Ok" in {

      val fakeRequest = FakeRequest("GET", "/digital-contact-stub/imi/messages")

      val controller = app.injector.instanceOf[EmailProviderController]

      val result = controller.viewQueue(fakeRequest)

      status(result) mustBe OK
    }
  }

}
