package controllers

import play.api.Environment
import play.api.mvc._
import shared.SharedMessages
import scala.concurrent.ExecutionContext.Implicits._

import javax.inject._
import play.api.Play



import scala.concurrent._
import scala.concurrent.duration._
import slick.jdbc.meta._
import slick.driver.H2Driver.api._


import database._
import models._
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.text
import play.api.libs.concurrent.Execution.Implicits.defaultContext


import java.sql.Timestamp

import play.api.mvc.Action
import play.api.mvc.Controller



class Application @Inject()(implicit environment: Environment) extends Controller {

  private val db = Database.forConfig("h2mem1")
  // lazy val jobD = new JobDAO
  def index = Action {
    createDB
    insertData
     Ok(views.html.index("Hello World !"))
    //val resultingJobs: Future[Seq[Job]] = JobDAO.all()
    //resultingJobs.map(jobs => Ok(views.html.index(jobs.toString())))
  }

  private def createDB() = {
    val tables = Await.result(db.run(MTable.getTables), Duration.Inf).toList
    if (!tables.isEmpty) {
      val reset = DBIO.seq(Jobs.data.schema.drop /*, Tables.data.schema.drop */)
      Await.result(db.run(reset), Duration.Inf)
    }
    val setup = DBIO.seq(Jobs.data.schema.create /*, Tables.data.schema.create */)
    Await.result(db.run(setup), Duration.Inf)
  }

  private def insertData() = {
    Jobs.insert("Test", new Timestamp(System.currentTimeMillis), new Timestamp(System.currentTimeMillis))
  }
  // def index = Action {
  //   Ok(views.html.index("Test"))
  // }
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
