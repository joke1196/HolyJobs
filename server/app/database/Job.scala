package database

import scala.concurrent._
import scala.concurrent.duration._
import javax.inject.Inject
import play.api.Play
import javax.inject._
import play.api.libs.concurrent.Execution.Implicits._
import slick.lifted.Tag
import slick.driver.MySQLDriver.api._


import java.sql.Date

case class Job(name: String, startDate: Date, endDate:Date,jobType:Int, id: Option[Int] = None)

class Jobs(tag: Tag) extends Table[Job](tag, "jobs") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def startDate = column[Date]("startDate")
  def endDate = column[Date]("endDate")
  def jobType = column[Int]("jobType")
  def typeId = foreignKey("typeId", jobType, Types.typeTable)(_.typeId)

  def * = (name, startDate, endDate, jobType, id.?)<>((Job.apply _).tupled, Job.unapply)
}

object Jobs {
  val db = Database.forConfig("h2mem1")
  val jobTable = TableQuery[Jobs]


  def insert(name: String, startDate: Date, endDate: Date, jobType:Int) =
    Await.result(db.run(DBIO.seq(jobTable += Job(name, startDate, endDate,jobType ))), Duration.Inf)

  def all(): List[Job] = (for (j <- Await.result(db.run(jobTable.result), Duration.Inf)) yield j).toList
  // def insert(job: Job): Future[Unit] = dbConfig.db.run(jobs += job).map { _ => () }

  def byID(id: Int): Option[Job] = {
    val jobs = for (j <- Await.result(db.run(jobTable.filter(_.id === id).result), Duration.Inf)) yield j
    if (jobs.isEmpty) None
    else Some(jobs.head)
  }

  def byType(typeId: Int): Option[List[Job]] = {
      val job = for (j <- Await.result(db.run(jobTable.filter(_.jobType === typeId).result), Duration.Inf)) yield j
      if(job.isEmpty) None
      else Some(job.toList)
  }

}
