package database

import scala.concurrent.Future

import javax.inject.Inject
import models.Job
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

class JobDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  private val Jobs = TableQuery[JobsTable]

  def all(): Future[Seq[Job]] = db.run(Jobs.result)
  def insert(job: Job): Future[Unit] = db.run(Jobs += job).map { _ => () }


  private class JobsTable(tag: Tag) extends Table[Job](tag, "JOB") {

    def name = column[String]("NAME", O.PrimaryKey)
    def color = column[String]("COLOR")

    def * = (name, color) <> (Job.tupled, Job.unapply _)
  }
}