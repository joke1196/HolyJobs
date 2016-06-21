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

case class Job(name: String, description:String, startDate: Date, endDate:Date,jobType:Int, region:Int, hourlyPay:Double, workingTime:Int, email:String, img:Option[String] = None, id: Option[Int] = None)
// The representation of the Table jobs in the database
class Jobs(tag: Tag) extends Table[Job](tag, "jobs") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def description = column[String]("description")
  def startDate = column[Date]("startDate")
  def endDate = column[Date]("endDate")
  def jobType = column[Int]("jobType")
  def region = column[Int]("region")
  def hourlyPay = column[Double]("hourlyPay")
  def workingTime = column[Int]("workingTime")
  def email = column[String]("email")
  def img = column[String]("img")
  def typeId = foreignKey("typeId", jobType, Types.typeTable)(_.typeId)
  def regionId = foreignKey("regionId", region, Regions.regionTable)(_.regionId)

  def * = (name, description, startDate, endDate, jobType, region, hourlyPay, workingTime, email,img.?, id.?)<>((Job.apply _).tupled, Job.unapply)
}

object Jobs {
  val db = Database.forConfig("h2mem1")
  val jobTable = TableQuery[Jobs]

  /* Insert a job in the database
  */
  def insert(name: String,description:String, startDate: Date, endDate: Date, jobType:Int, region:Int, hourlyPay:Double, workingTime:Int, email:String, img:Option[String]):Int= {
    //Two different possibilities with or without an image
      if(img != None){
        val jobId = Await.result(db.run{
          (jobTable returning jobTable.map(_.id)) += new Job(name, description, startDate, endDate,jobType, region, hourlyPay, workingTime, email, None )
        }, Duration.Inf)
        updateImgPath(jobId, img.get)
        jobId
      }else {
        Await.result(db.run(  (jobTable returning jobTable.map(_.id)) += new Job(name, description, startDate, endDate,jobType, region, hourlyPay, workingTime, email, Some("/tmp/noImg.jpeg") ) ), Duration.Inf)
      }
  }
  /* Update the image path of a job
  */
  def updateImgPath(id:Int, img:String)={
    val imgPath = for{j <- jobTable if j.id === id} yield j.img
    var updateAction = ""

    if (img == "default.jpg") {
        Await.result(db.run(imgPath.update(img)), Duration.Inf)
    } else {
        Await.result(db.run(imgPath.update(id + "-" + img)), Duration.Inf)
    }


  }

  // Returns all the job in the database
  def all(): List[Job] = (for (j <- Await.result(db.run(jobTable.result), Duration.Inf)) yield j).toList

  /* Returns a job by providing an ID
  */
  def byID(id: Int): Option[Job] = {
    val jobs = for (j <- Await.result(db.run(jobTable.filter(_.id === id).result), Duration.Inf)) yield j
    if (jobs.isEmpty) None
    else Some(jobs.head)
  }
  /*
  * given the type and the id of a job returns five other jobs with the same type
  */
  def relatedJob(typeId: Int, id:Int): Option[List[Job]] = {
      val job = for (j <- Await.result(db.run(jobTable.filter(_.jobType === typeId).filter(_.id =!= id).result), Duration.Inf)) yield j
      if(job.isEmpty) None
      else Some(job.take(5).toList)
  }


  /* Returns all the jobs filtered by its type,region and date.
  *  For the date, a job is returnes if the date provided is inbetween its start and end date
  */
  def filteredJobs(jobType: Int, region:Int, startDate:Date):List[Job] = {
    val query = (jobType, region, startDate) match {
        case (j, r, d) if(j < 0 && r >= 0) => jobTable.filter(_.region === r).filter(_.startDate <= d).filter(_.endDate >= startDate)
        case (j, r, d) if( r < 0 && j >= 0) => jobTable.filter(_.jobType === j).filter(_.startDate <= d).filter(_.endDate >= startDate)
        case (j, r, d) if(r < 0 && j < 0) => jobTable.filter(_.startDate <= d).filter(_.endDate >= startDate)
        case (j, r, d) => jobTable.filter(_.jobType === j).filter(_.region === r).filter(_.startDate <= d).filter(_.endDate >= startDate)
    }
    val job = for(j <- Await.result(db.run(query.result), Duration.Inf)) yield j
    job.toList
  }

}
