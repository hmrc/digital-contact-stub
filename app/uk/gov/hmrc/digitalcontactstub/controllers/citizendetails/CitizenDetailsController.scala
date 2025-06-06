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

package uk.gov.hmrc.digitalcontactstub.controllers.citizendetails

import play.api.Logging
import play.api.mvc.{ Action, AnyContent, MessagesControllerComponents }
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{ Inject, Singleton }
import scala.annotation.unused
import scala.concurrent.Future

@Singleton
class CitizenDetailsController @Inject() (
  cc: MessagesControllerComponents
) extends BackendController(cc) with Logging {

  val ninos = Map(
    "AA000003" -> "AA000003B",
    "CS700100" -> "CS700100A"
  )

  def basic(nino: String): Action[AnyContent] =
    Action.async {
      Future.successful(
        Ok(
          s"""
             |{
             |  "firstName": "firstname",
             |  "lastName":  "lastname",
             |  "title":     "Ms",
             |  "nino":      "${ninos.getOrElse(nino.take(8), nino)}"
             |}
             |""".stripMargin
        )
      )
    }

  def etag(@unused nino: String): Action[AnyContent] =
    Action.async {
      Future.successful(
        Ok(
          """
            |{
            |  "etag": "1"
            |}
            |""".stripMargin
        )
      )
    }

}
