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

package uk.gov.hmrc.digitalcontactstub.controllers.externalmessageadapter

import uk.gov.hmrc.digitalcontactstub.utils.SpecBase

import scala.util.matching.Regex

class EmailBounceBackHandlerControllerSpec extends SpecBase {

  "processEmailBounceBack" must {

    "return OK" in new Setup {}

    "return InternalServerError" in new Setup {}

    "return Unauthorized" in new Setup {}

    "return BAD_REQUEST" in new Setup {}
  }

  trait Setup {
    val correlationidRegex: Regex = """[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}""".r
  }
}
