package Dao.model

import slick.jdbc.MySQLProfile.api._

/**
  * Created by ad on 2017/5/31.
  */
case class Fruit(id: Option[Int], name: String, tp: String)
class Fruits(tag: Tag) extends Table[Fruit](tag, "FRUITS"){
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.Unique, O.SqlType("VARCHAR(20)"))
  def tp = column[String]("type")
  def * = (id.?, name, tp) <> ((Fruit.apply _).tupled, Fruit.unapply)
}
object Fruits {
  val fruits = TableQuery[Fruits]
}
