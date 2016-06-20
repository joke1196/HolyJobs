package database

import scala.concurrent._
import scala.concurrent.duration._
import javax.inject.Inject
import play.api.Play
import javax.inject._
import play.api.libs.concurrent.Execution.Implicits._
import slick.lifted.Tag
import slick.driver.MySQLDriver.api._


case class Type(typeName:String, typeId:Option[Int] = None)

class Types(tag:Tag) extends Table[Type](tag, "types") {
  def typeId = column[Int]("typeId", O.PrimaryKey, O.AutoInc)
  def typeName = column[String]("typeName")
  def * = (typeName, typeId.?)<>((Type.apply _).tupled, Type.unapply)
}
object Types {

  val db = Database.forConfig("h2mem1")
  lazy val typeTable = TableQuery[Types]
  /* Returns all Type from the database
  */
  def all():List[Type]= {
    (for (t <- Await.result(db.run(typeTable.result), Duration.Inf)) yield t).toList
  }

  /* Returns a Type by its ID
  */
  def typeName(typeId:Int):Option[Type] ={
    val tName = Await.result(db.run(typeTable.filter(_.typeId === typeId).result), Duration.Inf)
    if(tName.isEmpty) None else Some(tName.head)
  }
}
