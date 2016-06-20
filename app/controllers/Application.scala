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



class Application @Inject() (val messagesApi :MessagesApi)extends Controller with I18nSupport {
  private val db = Database.forConfig("h2mem1")

  def index = Action { implicit rs =>
    val resultingJobs: List[Job] = Jobs.all
    val jobTypes : List[Type] = Types.all
    val regions : List[Region] = Regions.all
    Ok(views.html.index(resultingJobs, jobTypes, regions, filterForm))
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
              job => Json.obj("name" -> JsString(job.name), "description" -> JsString(job.description), "hourlyPay" -> JsNumber(job.hourlyPay), "image" -> JsString(job.img.get))
          }))))
      // Otherwise generate a bad request error.
      } else {
          BadRequest("errorField");
      }
  }

  def filterJob = Action { implicit rs =>
    val form = filterForm.bindFromRequest()
    println(form.toString)
    form.fold(
      formWithErrors => {
        BadRequest(views.html.index(Jobs.all, Types.all,Regions.all, filterForm))
      },
      job => {
        val jobList = Jobs.filteredJobs(job.jobType, job.region, job.startDate)
        Ok(views.html.index(jobList, Types.all, Regions.all, filterForm))
      }
    )

  }

  def details(id: Int) = Action {
    val jobDetails: Option[Job] = Jobs.byID(id)
    val relatedJob:  Option[List[Job]] = Jobs.relatedJob(jobDetails.get.jobType, id)
    Ok(views.html.details(jobDetails.get, relatedJob.getOrElse(List[Job]())))
  }

  // def apply = Action {
  //   Ok(views.html.details("Okay"))
  // }

  def add = Action {
    val jobTypes : List[Type] = Types.all
    val regions : List[Region] = Regions.all
    Ok(views.html.add(jobForm,jobTypes, regions))
  }

  def createJob = Action(parse.multipartFormData) { implicit request =>
    val form = jobForm.bindFromRequest()
    var jobId = 0
    println(form.toString)
    form.fold(
      formWithErrors => {
        BadRequest(views.html.add(formWithErrors, Types.all,Regions.all))
      },
      job => {
        request.body.file("img") match {
          case Some(file) => {
            import java.io.File
            val filename = file.filename
            jobId = Jobs.insert(job.name, job.description, job.startDate, job.endDate, job.jobType, job.region, job.hourlyPay, job.workingTime, job.email, Some(filename))
            println("ID---------------" + jobId)
            val contentType = file.contentType
            file.ref.moveTo(new File(s"tmp/"+ jobId + file.filename))
          }
          case _ => jobId = Jobs.insert(job.name, job.description, job.startDate, job.endDate, job.jobType, job.region, job.hourlyPay, job.workingTime, job.email, Some("/tmp/noImg.jpeg"))
        }
      }
    )
    val jobDetails: Option[Job] = Jobs.byID(jobId)
    val relatedJob:  Option[List[Job]] = Jobs.relatedJob(jobDetails.get.jobType, jobId)
    Ok(views.html.details(jobDetails.get, relatedJob.getOrElse(List[Job]())))
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

  def filterForm:Form[FilterJob] = Form(
    mapping(
      "jobType" -> number,
      "region" -> number,
      "startDate" -> sqlDate
    )(FilterJob.apply)(FilterJob.unapply)
  )

}
