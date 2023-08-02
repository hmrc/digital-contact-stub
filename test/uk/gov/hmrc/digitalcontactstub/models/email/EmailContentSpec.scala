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

package uk.gov.hmrc.digitalcontactstub.models.email

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import EmailContent.format

class EmailContentSpec extends PlaySpec {
  "EmailContent" must {
    "be deserialized" in {
      val emailContent = EmailContent(
        Channel.EMAIL,
        "test@hmrc.com",
        List(To(List("senderEmail@gmail.com"), "correlationId")),
        "",
        Options(true, true, "HMRC"),
        ContactPolicy("KMdrUZptSrOQbemFdB7WAQ", true, true),
        Seq("submitted",
            "delivered",
            "not verified",
            "invalid",
            "bounced",
            "complaint",
            "read",
            "failed"),
        Content("type",
                "subject",
                Some(EmailAddress("replayTo@gmail.com")),
                "text",
                "html"),
        "https://webhook.site/8517c49d-519e-4823-9ad9-9886c26e9a15"
      )
      val jsonContent = Json.toJson(emailContent)
      val deserializedContent: EmailContent =
        jsonContent.validate[EmailContent].get
      emailContent mustBe deserializedContent
    }
  }
}
