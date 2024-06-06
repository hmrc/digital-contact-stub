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

package uk.gov.hmrc.digitalcontactstub.repositories

import org.mongodb.scala.model
import org.mongodb.scala.model.Sorts.{ ascending, descending }
import org.mongodb.scala.model.{ Filters, IndexOptions }
import uk.gov.hmrc.digitalcontactstub.models.email.EmailContent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.{ MongoComponent, MongoUtils }
import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class EmailQueueRepository @Inject() (mongo: MongoComponent)(implicit ec: ExecutionContext)
    extends PlayMongoRepository[EmailContent](
      mongo,
      "email_queue",
      EmailContent.format,
      Seq.empty
    ) {

  override def ensureIndexes(): Future[Seq[String]] =
    MongoUtils.ensureIndexes(
      collection,
      Seq(model.IndexModel(ascending("timeStamp"), IndexOptions().expireAfter(2, TimeUnit.DAYS))),
      replaceIndexes = true
    )

  def save(emailContent: EmailContent): Future[Boolean] =
    collection.insertOne(emailContent.copy(timeStamp = Some(Instant.now))).toFuture().map(_.wasAcknowledged())

  def deleteAll: Future[Boolean] =
    collection.deleteMany(Filters.empty()).toFuture().map(_.wasAcknowledged())

  def findAll: Future[Seq[EmailContent]] =
    collection.find(Filters.empty()).limit(30).sort(descending("timeStamp")).toFuture()

  def findItem(id: String): Future[Seq[EmailContent]] =
    collection.find(Filters.eq("to.correlationId", id)).toFuture()

  def findItemByEmail(email: String): Future[Seq[EmailContent]] =
    collection.find(Filters.eq("to.email", email)).toFuture()

  def deleteItem(id: String): Future[Boolean] =
    collection
      .deleteOne(Filters.eq("to.correlationId", id))
      .toFuture()
      .map(_.wasAcknowledged())

}
