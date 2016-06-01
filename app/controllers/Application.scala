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



class Application @Inject() (val messagesApi :MessagesApi)extends Controller with I18nSupport {
  private val db = Database.forConfig("h2mem1")

  def index = Action { implicit rs =>
    val resultingJobs: List[Job] = Jobs.all
    val jobTypes : List[Type] = Types.all
    val regions : List[Region] = Regions.all
    Ok(views.html.index(resultingJobs, jobTypes, regions))
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

  def createJob = Action { implicit request =>
    jobForm.bindFromRequest().fold(
      formWithErrors => BadRequest(views.html.add(formWithErrors, Types.all,Regions.all)),
      job => {
        val inserting = Jobs.insert(job.name, job.description, job.startDate, job.endDate, job.jobType, job.region, job.hourlyPay, job.workingTime, job.email)
      }
    )
    Ok(views.html.index(Jobs.all, Types.all, Regions.all))
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
     "id" -> optional(number)
   )(Job.apply)(Job.unapply)
  )


}
