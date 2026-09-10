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

package uk.gov.hmrc.digitalcontactstub.models.externalmessageadapter

import play.api.libs.json.{ JsResultException, Json }
import uk.gov.hmrc.digitalcontactstub.utils.SpecBase

class EmailBounceBackRequestSpec extends SpecBase {

  "EmailBounceBackRequest.format" must {
    import EmailBounceBackRequest.format

    "read the json correctly" in new Setup {
      Json.parse(emailBounceBackRequestJsonString).as[EmailBounceBackRequest] mustBe emailBounceBackRequestOb
    }

    "throw exception for invalid json" in new Setup {
      intercept[JsResultException] {
        Json.parse(mailBounceBackRequestInvalidJsonString).as[EmailBounceBackRequest]
      }
    }

    "write the object correctly" in new Setup {
      Json.toJson(emailBounceBackRequestOb) mustBe Json.parse(emailBounceBackRequestJsonString)
    }
  }

  trait Setup {
    val emailBounceBackRequestJsonString: String =
      """{
        |"reason":"EMAIL_BOUNCE",
        |"sourceData":"SGVsbG8gd29ybGQ=",
        |"emailAddress":"test@test.com",
        |"formId":"CH(A)1700",
        |"externalRefId":"9d7f1d675d544b009d6c3a8f7fb2e1c4"
        |}""".stripMargin

    val mailBounceBackRequestInvalidJsonString: String =
      """{
        |"reason":"EMAIL_BOUNCE",
        |"sourceData":"SGVsbG8gd29ybGQ=",
        |"emailAddress":"test@test.com",
        |"formId":"CH(A)1700"
        |}""".stripMargin

    val emailBounceBackRequestOb = EmailBounceBackRequest(
      reason = "EMAIL_BOUNCE",
      sourceData = "SGVsbG8gd29ybGQ=",
      emailAddress = "test@test.com",
      formId = Some("CH(A)1700"),
      properties = None,
      externalRefId = "9d7f1d675d544b009d6c3a8f7fb2e1c4"
    )
  }
}
