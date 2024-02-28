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

package uk.gov.hmrc.digitalcontactstub.connector

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.mockito.MockitoSugar.mock
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.libs.ws.WSClient
import uk.gov.hmrc.http.{HttpClient, HttpResponse}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}

class EmailEventsConnectorSpec extends PlaySpec with GuiceOneAppPerTest {
  "markSent" must {
    "return transId" in new Setup {


      lazy val wsClient: WSClient = app.injector.instanceOf[WSClient]

      def send(id: Int): Future[Int] = {
        Thread.sleep(50)
        val body: JsValue = Json.parse ((s"""{
                                            |  "channel": "email",
                                            |  "consent": true,
                                            |  "address": "test$id@gmail.com",
                                            |  "reason": "auto"
                                            |}""".stripMargin))

        val result = wsClient.url("https://contactpolicy.imiconnect.eu/v1/groups/yk7_hM8eQQeFwA3zHyfRgg/members").withHttpHeaders(("authorization", "Bearer XXX"), ("content-type", "application/json")).post(body)

        result.map(x => x.status)

      }
      val ids = (1 to 20000).toList


      val finalResult = Future.traverse(ids)(send)


      Await.result(finalResult, 20.minutes)





      val connector = new EmailEventsConnector(servicesConfig, httpClient)
      val result = connector.markSent(UUID.randomUUID().toString).futureValue
      result mustBe transId

    }
  }

  class Setup {
    val servicesConfig = mock[ServicesConfig]
    val httpClient = mock[HttpClient]
    val transId = UUID.randomUUID().toString
    when(
      httpClient.doPost[String](
        any[String],
        any[String],
        any[Seq[(String, String)]])(any[Writes[String]], any[ExecutionContext]))
      .thenReturn(Future.successful(HttpResponse(201, transId)))
  }
}
