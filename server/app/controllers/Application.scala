package controllers

import play.api.Environment
import play.api.mvc._
import shared.SharedMessages
import scala.concurrent.ExecutionContext.Implicits.global

import javax.inject._

import scala.concurrent.Future

import database._
import models.Job
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.text
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile
import play.api.mvc.Action
import play.api.mvc.Controller

class Application @Inject()(implicit environment: Environment) extends Controller {
  // val jobDao = new JobDAO
  def index = Action { implicit request =>
     /*val resultingJobs: Future[Seq[Job]] = JobDAO.all()
   resultingJobs.map(jobs => Ok(views.html.index(jobs.toString())))*/
   Ok(views.html.index("ok"))
}

  def details(id: Long) = Action {
    Ok(views.html.details(SharedMessages.itWorks))
  }

  def apply = Action {
    Ok(views.html.details(SharedMessages.itWorks))
  }

  def add = Action {
    Ok(views.html.add(SharedMessages.itWorks))
  }

  def createJob = Action {
    Ok(views.html.add(SharedMessages.itWorks))
  }


}
