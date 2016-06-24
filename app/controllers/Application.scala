package controllers

import play.api.Environment
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits._

import javax.inject._
import play.api.Play



import scala.concurrent._
import scala.concurrent.duration._
import slick.jdbc.meta._
import slick.driver.MySQLDriver.api._
import database._
import models._
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.text
import play.api.libs.concurrent.Execution.Implicits.defaultContext


import java.sql.Date
import play.api.mvc.Action
import play.api.mvc.Controller

import play.api.data.Forms._
import play.api.data._
import play.api.data.format.Formats._

import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi

import play.api.libs.json._
import java.util.Date
import play.api.libs.mailer._
import java.io.File



class Application @Inject() (mailerClient: MailerClient, val messagesApi: MessagesApi) extends Controller with I18nSupport {
  private val db = Database.forConfig("h2mem1")

  def index = Action { implicit rs =>
    // Tries to get the URL parameters in order to restore a previous research
    // or to perform a research from the search-header of another page, which
    // posts these parameters on this page.
    val region = rs.queryString.get("region").flatMap(_.headOption)
    val startDate = rs.queryString.get("startDate").flatMap(_.headOption)
    val jobType = rs.queryString.get("jobType").flatMap(_.headOption)

    val resultingJobs: List[Job] = Jobs.all
    val jobTypes : List[Type] = Types.all
    val regions : List[Region] = Regions.all

    Ok(views.html.index(resultingJobs, jobTypes, regions, region, startDate, jobType))
  }

  private def createDB() = {
    val tables = Await.result(db.run(MTable.getTables), Duration.Inf).toList
    if (!tables.isEmpty) {
      val reset = DBIO.seq(Jobs.jobTable.schema.drop /*, Tables.data.schema.drop */)
      Await.result(db.run(reset), Duration.Inf)
    }
    val setup = DBIO.seq(Jobs.jobTable.schema.create /*, Tables.data.schema.create */)
    Await.result(db.run(setup), Duration.Inf)
  }

  def filterJobAjax = Action { request =>
      var jobType, region = -1;
      var startDate = new java.util.Date();
      var valid = true
      val dateFormat = new java.text.SimpleDateFormat("dd-MM-yyyy")

      // Tries to get the parameters values and to convert them.
      // If an error occurs, it means one parameter has not been properly set.
      try {
          jobType = request.queryString.get("jobType").flatMap(_.headOption).get.toInt
          region = request.queryString.get("region").flatMap(_.headOption).get.toInt
          startDate = dateFormat.parse(request.queryString.get("startDate").flatMap(_.headOption).get)
      } catch {
          case en: java.lang.NumberFormatException => valid = false
      }

      println(startDate);

      // Gets the jobs and returns them if the parameters were valid.
      if (valid) {
          val jobsList = Jobs.filteredJobs(jobType, region, new java.sql.Date(startDate.getTime()))

          // Returns the jobs list as a JSON array.
          Ok(JsObject(Seq("jobs" -> JsArray(jobsList.map {
              job => Json.obj(
                  "id" -> JsNumber(job.id.get),
                  "name" -> JsString(job.name),
                  "description" -> JsString(job.description),
                  "startDate" -> JsString(dateFormat.format(job.startDate)),
                  "endDate" -> JsString(dateFormat.format(job.endDate)),
                  "hourlyPay" -> JsNumber(job.hourlyPay),
                  "workingTime" -> JsNumber(job.workingTime),
                  "image" -> JsString(job.img.get)
              )
          }))))
      // Otherwise generate a bad request error.
      } else {
          BadRequest("errorField");
      }
  }

  def getRegionAndTypeName(regionId:Int, jobTypeId:Int):(String,String) = {
    val jobTypeName: String = Types.typeName(jobTypeId).get.typeName
    val regionName : String = Regions.regionName(regionId).get.regionName
    (regionName, jobTypeName)
  }

  def details(id: Int) = Action { request =>
    val result = request.queryString.get("result").flatMap(_.headOption)
    val jobTypes : List[Type] = Types.all
    val regions : List[Region] = Regions.all
    val jobDetails: Option[Job] = Jobs.byID(id)
    val relatedJob:  Option[List[Job]] = Jobs.relatedJob(jobDetails.get.jobType, id)
    val regionAndType = getRegionAndTypeName(jobDetails.get.region, jobDetails.get.jobType)

    Ok(views.html.details(result, jobTypes, regions, jobDetails.get, relatedJob.getOrElse(List[Job]()), regionAndType._1, regionAndType._2))
  }

  def add = Action {
    val jobTypes : List[Type] = Types.all
    val regions : List[Region] = Regions.all
    Ok(views.html.add(jobForm, jobTypes, regions))
  }

  def apply(id: Int) = Action(parse.multipartFormData) { request =>
      val emailPattern = """(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$)""".r
      val jobDetails = Jobs.byID(id).get
      // Gets the form's parameters.
      val firstName = request.body.asFormUrlEncoded.get("firstName").map(_.head);
      val lastName = request.body.asFormUrlEncoded.get("lastName").map(_.head);
      val emailAddress = request.body.asFormUrlEncoded.get("email").map(_.head);
      val age = request.body.asFormUrlEncoded.get("age").map(_.head);
      val comments = request.body.asFormUrlEncoded.get("comments").map(_.head);
      val file = request.body.file("file");
      val validResult = "ok"
      // Indicates the result of the fields validation.
      var result = validResult;

      // Validates mandatory fields.
      if (firstName.get.isEmpty || lastName.get.isEmpty || emailAddress.get.isEmpty || age.get.isEmpty) {
          result = "fieldEmpty"
      // Validates email field.
      } else if (!emailAddress.get.matches(emailPattern.toString)) {
         result = "badEmailFormat"
      // Validates age as a number field.
      } else if (!age.get.forall(_.isDigit)) {
         result = "ageNaN"
      // Validates the uploaded file, if there is one.
      } else if (!file.get.filename.isEmpty) {
          // The file must be a PDF.
          if (file.get.contentType.isEmpty || file.get.contentType.get != "application/pdf") {
              result = "invalidFile"
          // The file cannot be larger than 5MB.
          } else if (file.get.ref.file.length / 1024 >= 5000) {
              result = "fileTooLarge"
          }
      }

      // Sends the email if every field is valid.
      if (result == validResult) {
          if (file.get.filename.isEmpty) {
              println("Sending to " + jobDetails.email + "...");

              val email = Email(
                "HolyJobs - New application for a job!",
                "HolyJobs <from@email.com>",
                Seq("<" + jobDetails.email + ">"),
                bodyHtml = Some(s"""
                    <html>
                        <body>
                            <p>
                                Hello,
                            </p>
                            <p>
                                You received a new application for your job '<strong>""" + jobDetails.name + """</strong>':
                                <table cellpadding=10>
                                    <tr>
                                        <td></td>
                                        <td>First Name: </td>
                                        <td>""" + firstName.get + """</td>
                                    </tr>
                                    <tr>
                                        <td></td>
                                        <td>Last Name: </td>
                                        <td>""" + lastName.get + """</td>
                                    </tr>
                                    <tr>
                                        <td></td>
                                        <td>Email Address: </td>
                                        <td><a href="mailto:""" + emailAddress.get + """">""" + emailAddress.get + """</a></td>
                                    </tr>
                                    <tr>
                                        <td></td>
                                        <td>Age: </td>
                                        <td>""" + age.get + """</td>
                                    </tr>
                                    <tr>
                                        <td></td>
                                        <td>Comments: </td>
                                        <td>""" + (if (comments.get.isEmpty) "-" else comments.get) + """</td>
                                    </tr>
                                </table><br/>
                            </p>
                            <p>
                                We're looking forward to seeing you again on HolyJobs,<br/>
                                The <a href="http://localhost:9000/">HolyJobs</a> Team
                            </p>
                        </body>
                    </html>""")
              )

              mailerClient.send(email)
          } else {
             val email = Email(
                "HolyJobs - New application for a job!",
                "HolyJobs <from@email.com>",
                Seq("<" + jobDetails.email + ">"),
                // adds attachment
                attachments = Seq(AttachmentFile("Attachment.pdf", new File(file.get.ref.file.getAbsolutePath()))),
                bodyHtml = Some(s"""
                    <html>
                        <body>
                            <p>
                                Hello,
                            </p>
                            <p>
                                You received a new application for your job '<strong>""" + jobDetails.name + """</strong>':
                                <table cellpadding=10>
                                    <tr>
                                        <td></td>
                                        <td>First Name: </td>
                                        <td>""" + firstName.get + """</td>
                                    </tr>
                                    <tr>
                                        <td></td>
                                        <td>Last Name: </td>
                                        <td>""" + lastName.get + """</td>
                                    </tr>
                                    <tr>
                                        <td></td>
                                        <td>Email Address: </td>
                                        <td><a href="mailto:""" + emailAddress.get + """">""" + emailAddress.get + """</a></td>
                                    </tr>
                                    <tr>
                                        <td></td>
                                        <td>Age: </td>
                                        <td>""" + age.get + """</td>
                                    </tr>
                                    <tr>
                                        <td></td>
                                        <td>Comments: </td>
                                        <td>""" + (if (comments.get.isEmpty) "-" else comments.get) + """</td>
                                    </tr>
                                </table><br/>
                                Don't forget to have a look at the attached PDF file!
                            </p>
                            <br/>
                            <p>
                                We're looking forward to seeing you again on HolyJobs,<br/>
                                The <a href="http://localhost:9000/">HolyJobs</a> Team
                            </p>
                        </body>
                    </html>""")
              )

              mailerClient.send(email)
          }
      }

      // Redirects the user on the product page when the process ends.
      Redirect("/details/" + id + "?result=" + result)
  }

    def createJob = Action(parse.multipartFormData) { implicit request =>
      val form = jobForm.bindFromRequest()
      var jobId = 0
      //println(form.toString)
      form.fold(
        formWithErrors => {
          BadRequest(views.html.add(formWithErrors, Types.all,Regions.all))
        },
        job => {
          request.body.file("file") match {
            case Some(file) => {
              val filename = file.filename

              if (filename.isEmpty) {
                  jobId = Jobs.insert(job.name, job.description, job.startDate, job.endDate, job.jobType, job.region, job.hourlyPay, job.workingTime, job.email, Some("default.jpg"))
              } else {
                  jobId = Jobs.insert(job.name, job.description, job.startDate, job.endDate, job.jobType, job.region, job.hourlyPay, job.workingTime, job.email, Some(filename))
                  val contentType = file.contentType
                  file.ref.moveTo(new File(s"public/images/jobs/" + jobId + "-" + file.filename))
              }
            }
            case _ => jobId = Jobs.insert(job.name, job.description, job.startDate, job.endDate, job.jobType, job.region, job.hourlyPay, job.workingTime, job.email, Some("default.jpg"))
          }
        }
      )
      val jobTypes : List[Type] = Types.all
      val regions : List[Region] = Regions.all
      val jobDetails: Option[Job] = Jobs.byID(jobId)
      val relatedJob:  Option[List[Job]] = Jobs.relatedJob(jobDetails.get.jobType, jobId)
      val regionAndType = getRegionAndTypeName(jobDetails.get.region, jobDetails.get.jobType)

      Ok(views.html.details(None, jobTypes, regions, jobDetails.get, relatedJob.getOrElse(List[Job]()), regionAndType._1, regionAndType._2))
    }

  def jobForm:Form[Job] = Form(
    mapping(
    "name" -> nonEmptyText,
     "description" ->text,
     "startDate" -> sqlDate,
     "endDate" -> sqlDate,
     "jobType" -> number,
     "region" ->number,
     "hourlyPay" -> of(doubleFormat),
     "workingTime" -> number (min = 0, max = 100),
     "email" -> email,
     "img"-> optional(text),
     "id" -> optional(number)
   )(Job.apply)(Job.unapply)
  )
}
