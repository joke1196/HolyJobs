package controllers

import play.api.Environment
import play.api.mvc._
import shared.SharedMessages

class Application()(implicit environment: Environment) extends Controller {

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
