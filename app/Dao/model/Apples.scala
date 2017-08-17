package Dao.model
import slick.jdbc.MySQLProfile.api._
/**
  * Created by ad on 2017/5/31.
  */
case class Apple(id: Option[Int], name: String, size: Int, weight: Option[Double], typ: Int)
class Apples(tag: Tag) extends Table[Apple](tag, "APPLES"){
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME", O.Unique, O.SqlType("VARCHAR(20)"))
  def size = column[Int]("SIZE", O.Default(1))
  def weight = column[Double]("WEIGHT", O.Default(1024))
  def typ = column[Int]("type")
  def * = (id.?, name, size, weight.?, typ) <> (Apple.tupled, Apple.unapply)
}
object Apples {
  val apples = TableQuery[Apples]
}
