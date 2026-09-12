/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.digitalcontactstub.utils

import uk.gov.hmrc.digitalcontactstub.utils.Utils.*

class UtilsSpec extends SpecBase {

  "encodeStringToBase64" should {

    val originalString = "This is for test"
    val encodedString = "VGhpcyBpcyBmb3IgdGVzdA=="

    "encode the input string correctly" in {
      encodeStringToBase64(originalString) mustBe encodedString
    }
  }

  "decodeStringFromBase64" should {
    val originalString = "This is for test"
    val encodedString = "VGhpcyBpcyBmb3IgdGVzdA=="

    "decode the input string correctly" in {
      Utils.decodeStringFromBase64(encodedString) mustBe originalString
    }
  }

  "uuidOfLength32AndWithoutHyphen" should {
    "return correct uuid value of length 32 and without hyphen" in {
      val uuid = uuidOfLength32AndWithoutHyphen

      uuid.length mustBe 32
      uuid.contains(HYPHEN) mustBe false
    }
  }

}
