package database

import scala.concurrent._
import scala.concurrent.duration._
import javax.inject.Inject
import play.api.Play
import javax.inject._
import play.api.libs.concurrent.Execution.Implicits._
import slick.lifted.Tag
import slick.driver.MySQLDriver.api._


case class Region(regionName:String, regionId:Option[Int] = None)

class Regions(tag:Tag) extends Table[Region](tag, "regions") {
  def regionId = column[Int]("regionId", O.PrimaryKey, O.AutoInc)
  def regionName = column[String]("regionName")
  def * = (regionName, regionId.?)<>((Region.apply _).tupled, Region.unapply)
}
object Regions {

  val db = Database.forConfig("h2mem1")
  lazy val regionTable = TableQuery[Regions]

  def all():List[Region]= {
    (for (t <- Await.result(db.run(regionTable.result), Duration.Inf)) yield t).toList
  }
}
