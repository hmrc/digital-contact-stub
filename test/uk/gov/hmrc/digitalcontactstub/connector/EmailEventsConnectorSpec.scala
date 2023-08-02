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
import play.api.libs.json.Writes
import uk.gov.hmrc.digitalcontactstub.models.email.DeliveryStatus.Submitted
import uk.gov.hmrc.digitalcontactstub.models.email.{
  DeliveryDescription,
  DeliveryInfo,
  DeliveryInfoNotification,
  Event
}
import uk.gov.hmrc.http.{HttpClient, HttpResponse}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class EmailEventsConnectorSpec extends PlaySpec {
  "markSent" must {
    "return transId" in new Setup {
      val connector = new EmailEventsConnector(servicesConfig, httpClient)
      val result = connector.markSent(UUID.randomUUID().toString).futureValue
      result mustBe transId

    }
  }
  "send" must {
    "return 201 status" in new Setup {
      val connector = new EmailEventsConnector(servicesConfig, httpClient)
      val result = connector
        .send(
          Event(DeliveryInfoNotification(
            DeliveryInfo(LocalDateTime.parse("2022-12-07T14:40:46.886"),
                         DeliveryDescription.Delivered,
                         "7501",
                         "email",
                         "",
                         "test.dc@digital.hmrc.gov.uk",
                         "email",
                         Submitted),
            "",
            UUID.fromString("4310b3f8-9d89-47a3-9c72-4482f9ef14c9"),
            "",
            UUID.fromString("4310b3f8-9d89-47a3-9c72-4482f9ef14c9")
          )))
        .futureValue
      result mustBe 201

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
