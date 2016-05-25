package database

import scala.concurrent._
import scala.concurrent.duration._
import javax.inject.Inject
import play.api.Play
import javax.inject._
import play.api.libs.concurrent.Execution.Implicits._
import slick.lifted.Tag
import slick.driver.H2Driver.api._


import java.sql.Timestamp

case class Job(name: String, startDate: Timestamp, endDate:Timestamp, id: Option[Int] = None)

class Jobs(tag: Tag) extends Table[Job](tag, "jobs") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME")
  def startDate = column[Timestamp]("START_DATE")
  def endDate = column[Timestamp]("END_DATE")

  def * = (name, startDate, endDate, id.?)<>((Job.apply _).tupled, Job.unapply)
}

object Jobs {
  val db = Database.forConfig("h2mem1")
  lazy val data = TableQuery[Jobs]


  def insert(name: String, startDate: Timestamp, endDate: Timestamp) =
    Await.result(db.run(DBIO.seq(data += Job(name, startDate, endDate))), Duration.Inf)

  def all(): Seq[Job] = (for (j <- Await.result(db.run(data.result), Duration.Inf)) yield j).toSeq
  // def insert(job: Job): Future[Unit] = dbConfig.db.run(jobs += job).map { _ => () }

  def byID(id: Int): Option[Job] = {
    val jobs = for (j <- Await.result(db.run(data.filter(_.id === id).result), Duration.Inf)) yield j
    if (jobs.isEmpty) None
    else Some(jobs.head)
  }
}
