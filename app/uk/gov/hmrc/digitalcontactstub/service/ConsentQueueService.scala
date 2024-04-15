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

package uk.gov.hmrc.digitalcontactstub.service

import uk.gov.hmrc.digitalcontactstub.models.email.ImiConsent
import scala.collection.mutable

class ConsentQueueService() {

  var queue = mutable.LinkedHashSet.empty[ImiConsent]

  def save(imiConsent: ImiConsent): Seq[ImiConsent] = {
    val contains = queue.find(_.address == imiConsent.address)
    if (contains.isEmpty)
      queue.addOne(imiConsent).toList
    else {
      queue.remove(contains.get): Unit
      queue.addOne(imiConsent).toList
    }
  }

  def remove(email: String): Option[Boolean] =
    queue.find(_.address == email).map(queue.remove)

  def resetQueue() =
    queue = mutable.LinkedHashSet.empty[ImiConsent]

}
