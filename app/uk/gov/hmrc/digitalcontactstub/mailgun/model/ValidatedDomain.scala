/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.digitalcontactstub.mailgun.model

import play.api.mvc.PathBindable
import uk.gov.hmrc.digitalcontactstub.mailgun.config.Keys

case class ValidatedDomain(domain: String)

object ValidatedDomain {

  implicit val binder: PathBindable[ValidatedDomain] = new PathBindable[ValidatedDomain] {
    def bind(key: String, value: String): Either[String, ValidatedDomain] =
      if (value.nonEmpty) {
        Right(ValidatedDomain(value))
      } else {
        Left(
          s"The provided domain ($value) did not match the domain expected by the stub (${Keys.allDomains.mkString(", ")})"
        )
      }

    def unbind(key: String, value: ValidatedDomain): String = value.domain
  }

}
