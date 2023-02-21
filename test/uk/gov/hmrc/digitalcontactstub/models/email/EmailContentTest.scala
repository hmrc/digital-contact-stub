package uk.gov.hmrc.digitalcontactstub.models.email

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{Json, OFormat}
import EmailContent.format

class EmailContentTest extends PlaySpec {

  "EmailContent" must {
    "be deserialized" in {

      val emailContent = EmailContent(
        Channel.EMAIL,
        "test@hmrc.com",
        List(To(List("senderEmail@gmail.com"), "correlationId")),
        "",
        Options(true, true, "HMRC"),
        Content("type", "subject", Some(EmailAddress("replayTo@gmail.com")), "text", "html")
      )

      val json = Json.toJson(emailContent)

      val content: EmailContent = json.validate[EmailContent].get

      println(json)
      println(content)


    }
  }



}
