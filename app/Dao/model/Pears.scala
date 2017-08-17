package Dao.model
import slick.jdbc.MySQLProfile.api._

/**
  * Created by ad on 2017/6/5.
  */
case class Pear(id:Int = -1, name: String, size: Int, weight: Double, typ: Int)
class Pears(tag: Tag) extends Table[Pear](tag, "pears"){
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME", O.Unique, O.SqlType("VARCHAR(20)"))
  def size = column[Int]("SIZE", O.Default(1))
  def weight = column[Double]("WEIGHT", O.Default(1024))
  def typ = column[Int]("type")
  def * = (id, name, size, weight, typ) <> (Pear.tupled, Pear.unapply)
}
object Pears {
  val pears = TableQuery[Pears]
}