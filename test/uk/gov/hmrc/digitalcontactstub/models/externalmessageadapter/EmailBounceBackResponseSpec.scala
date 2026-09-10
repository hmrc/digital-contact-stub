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

import play.api.http.Status.{ BAD_REQUEST, SERVICE_UNAVAILABLE }
import play.api.libs.json.{ JsNumber, JsResultException, JsString, Json }
import uk.gov.hmrc.digitalcontactstub.utils.SpecBase
import HIPOrigin.*

class EmailBounceBackResponseSpec extends SpecBase {

  "HIPOrigin" should {
    import HIPOrigin.format

    "read the correct enum value" in {
      JsString("hip").as[HIPOrigin] mustBe HIP
      JsString("hod").as[HIPOrigin] mustBe HoD
      JsString("HIP").as[HIPOrigin] mustBe HIP
      JsString("HoD").as[HIPOrigin] mustBe HoD
    }

    "throw JsError for invalid string value" in {
      intercept[JsResultException] {
        JsString("DES").as[HIPOrigin]
      }.errors.head._2.head.message mustBe "Unknown HIPOrigin: DES"

      intercept[JsResultException] {
        JsString("UNKNOWN").as[HIPOrigin]
      }.errors.head._2.head.message mustBe "Unknown HIPOrigin: UNKNOWN"
    }

    "throw JsError for value other than String" in {
      intercept[JsResultException] {
        JsNumber(BigDecimal(5.0)).as[HIPOrigin]
      }.errors.head._2.head.message mustBe "HIPOrigin must be a string"
    }

    "write the correct value" in {
      Json.toJson(HIP) mustBe JsString("hip")
      Json.toJson(HoD) mustBe JsString("hod")
    }
  }

  "EmailBounceBackFailureResponse.format" must {
    import EmailBounceBackFailureResponse.format

    "read the json correctly" in new Setup {
      Json
        .parse(emailBounceBackFailureResJsonString)
        .as[EmailBounceBackFailureResponse] mustBe emailBounceBackFailureResOb

      Json
        .parse("""{"reason":"correlationId failed in validation"}""")
        .as[EmailBounceBackFailureResponse] mustBe emailBounceBackFailureResOb.copy(failureType = None)
    }

    "throw exception for invalid json" in new Setup {
      intercept[JsResultException] {
        Json.parse(emailBounceBackFailureResInvalidJsonString).as[EmailBounceBackFailureResponse]
      }
    }

    "write the object correctly" in new Setup {
      Json.toJson(emailBounceBackFailureResOb) mustBe Json.parse(emailBounceBackFailureResJsonString)
    }
  }

  "EmailBounceBackFailuresResponse.format" must {
    import EmailBounceBackFailuresResponse.format

    "read the json correctly" in new Setup {
      Json
        .parse(emailBounceBackFailuresResJsonString)
        .as[EmailBounceBackFailuresResponse] mustBe emailBounceBackFailuresResOb
    }

    "throw exception for invalid json" in new Setup {
      intercept[JsResultException] {
        Json.parse(emailBounceBackFailuresResInvalidJsonString).as[EmailBounceBackFailuresResponse]
      }
    }

    "write the object correctly" in new Setup {
      Json.toJson(emailBounceBackFailuresResOb) mustBe Json.parse(emailBounceBackFailuresResWithOnlyReasonJsonString)
    }
  }

  "EmailBounceBackResponseBody.format" must {
    import EmailBounceBackResponseBody.format

    "read the json correctly" in new Setup {
      Json
        .parse(emailBounceBackResBodyJsonString)
        .as[EmailBounceBackResponseBody] mustBe emailBounceBackReseBodyOb
    }

    "throw exception for invalid json" in new Setup {
      intercept[JsResultException] {
        Json.parse(emailBounceBackResBodyInvalidJsonString).as[EmailBounceBackResponseBody]
      }
    }

    "write the object correctly" in new Setup {
      Json.toJson(emailBounceBackReseBodyOb) mustBe Json.parse(emailBounceBackResBodyWithoutTypeJsonString)
    }
  }

  trait Setup {
    val emailBounceBackFailureResJsonString: String =
      """{
        |"failureType":"header.correlationid",
        |"reason":"correlationId failed in validation"
        |}""".stripMargin

    val emailBounceBackFailureResInvalidJsonString: String = """{"failureType":"header.correlationid"}""".stripMargin

    val emailBounceBackFailureResOb =
      EmailBounceBackFailureResponse(
        failureType = Some("header.correlationid"),
        reason = "correlationId failed in validation"
      )

    val emailBounceBackFailuresResJsonString: String =
      """{"failures":[{"type":"header.correlationid","reason":"correlationId failed in validation"}]}""".stripMargin

    val emailBounceBackFailuresResWithOnlyReasonJsonString: String =
      """{"failures":[{"reason":"correlationId failed in validation"}]}""".stripMargin

    val emailBounceBackFailuresResInvalidJsonString: String =
      """{"failures":[{"type":"header.correlationid"}]}""".stripMargin

    val emailBounceBackFailuresResOb =
      EmailBounceBackFailuresResponse(failures = List(emailBounceBackFailureResOb.copy(failureType = None)))

    val emailBounceBackResBodyJsonString: String =
      """{
        |"origin":"HIP",
        |"response":{
        |"failures":[
        |{
        |"type":"header.correlationid",
        |"reason":"The request parameter header.correlationid failed validation due to pattern mismatch."
        |},
        |{
        |"type":"body.schema.pattern",
        |"reason":"Path '/emailAddress' validation failed."
        |}
        |]
        |}
        |}""".stripMargin

    val emailBounceBackResBodyWithoutTypeJsonString: String =
      """{
        |"origin":"hip",
        |"response":{
        |"failures":[{"reason":"The request parameter header.correlationid failed validation due to pattern mismatch."},
        |{"reason":"Path '/emailAddress' validation failed."}
        |]
        |}
        |}""".stripMargin

    val emailBounceBackResBodyInvalidJsonString: String =
      """{
        |"origin":"hip2",
        |"response":{
        |"failures":[{"reason":"The request parameter header.correlationid failed validation due to pattern mismatch."},
        |{"reason":"Path '/emailAddress' validation failed."}
        |]
        |}
        |}""".stripMargin

    val emailBounceBackFailuresResObForResBody = EmailBounceBackFailuresResponse(failures =
      List(
        EmailBounceBackFailureResponse(reason =
          "The request parameter header.correlationid failed validation due to pattern mismatch."
        ),
        EmailBounceBackFailureResponse(reason = "Path '/emailAddress' validation failed.")
      )
    )
    val emailBounceBackReseBodyOb =
      EmailBounceBackResponseBody(origin = HIP, response = Some(emailBounceBackFailuresResObForResBody))
  }
}
