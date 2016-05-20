package database

import scala.concurrent.Future

import play.api.Play
import javax.inject._
import models.Job
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.MySQLDriver.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import slick.lifted.Tag

trait JobComponent {
  class JobsTable(tag: Tag) extends Table[Job](tag, "JOB") {

    def name = column[String]("NAME", O.PrimaryKey)
    def color = column[String]("COLOR")

    def * = (name, color) <> (Job.tupled, Job.unapply _)
  }

}


object JobDAO extends JobComponent {

  // import dbConfig.driver.api._
  protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  private val Jobs = TableQuery[JobsTable]

  def all(): Future[Seq[Job]] = dbConfig.db.run(Jobs.result)
  def insert(job: Job): Future[Unit] = dbConfig.db.run(Jobs += job).map { _ => () }

}
