package controllers

import play.api.Environment
import play.api.mvc._
import shared.SharedMessages
import scala.concurrent.ExecutionContext.Implicits.global

import javax.inject.Inject
import database.JobDAO
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.text
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Action
import play.api.mvc.Controller

class Application @Inject()(implicit environment: Environment) extends Controller {


  def index = Action {
    Ok(views.html.index(SharedMessages.itWorks))
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