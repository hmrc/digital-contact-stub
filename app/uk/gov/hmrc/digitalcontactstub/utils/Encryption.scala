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

package uk.gov.hmrc.digitalcontactstub.utils

import com.typesafe.config.Config
import uk.gov.hmrc.crypto.{ Crypted, Decrypter, Encrypter, SymmetricCryptoFactory }

import javax.inject.Inject

class Encryption @Inject() (config: Config) {
  val configKey = "crypto"
  def crypto: Encrypter with Decrypter = SymmetricCryptoFactory.aesCryptoFromConfig(configKey, config)

  def decrypt(text: String) =
    crypto.decrypt(Crypted(text))
}

object Encryption {
  val KEY = "enrolment"
}
