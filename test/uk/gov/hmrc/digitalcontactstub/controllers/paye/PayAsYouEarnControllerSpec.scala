/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.digitalcontactstub.controllers.paye

import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec
import play.api.http.Status.OK
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.{ Headers, Result }
import play.api.test.Helpers.{ contentAsString, defaultAwaitTimeout, status }
import play.api.test.{ FakeRequest, Helpers }
import uk.gov.hmrc.digitalcontactstub.controllers.paye.PayAsYouEarnController
import uk.gov.hmrc.mongo.test.MongoSupport

import scala.concurrent.Future
import scala.io.Source

class PayAsYouEarnControllerSpec extends PlaySpec with BeforeAndAfterEach with MongoSupport {

  "POST to /paye/individual/print-preferences" must {
    "return OK" in new TestSetUp {
      val result: Future[Result] = controller.hipChangedOutputPreferences()(postFakeRequest)

      val body = contentAsString(result)
      val statusCode: Int = status(result)
      statusCode mustBe OK
    }
  }

  override def beforeEach(): Unit =
    super.beforeEach()

  class TestSetUp {
    def readFile(fileName: String): String = {
      val resource = Source.fromURL(getClass.getResource("/" + fileName))
      val resourceAsString = resource.mkString
      resource.close()
      resourceAsString
    }

    val payload = Json.parse(readFile("hip-print-suppression-ok.json"))

    val postFakeRequest: FakeRequest[JsValue] =
      FakeRequest(
        "POST",
        "/paye/individual/print-preferences",
        Headers((Helpers.CONTENT_TYPE, "application/json")),
        payload
      )
    val controller = new PayAsYouEarnController(Helpers.stubMessagesControllerComponents())
  }
}
