package Dao.model

import slick.jdbc.MySQLProfile.api._
import slick.lifted.ProvenShape
/**
  * Created by ad on 2017/5/31.
  */
class UionPrimaryKey(tag: Tag) extends Table[(Int, Int)] (tag, "UionPrimaryKey"){
  def k1 = column[Int]( "k1", O.Unique)
  def k2 = column[Int]("k2")
  def * = (k1, k2)

  def pk = primaryKey("pk_a", (k1,k2))
  def idx = index("idx_a", (k1,k2), unique = true)
}
