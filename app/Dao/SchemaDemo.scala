package Dao

import java.util.concurrent.TimeUnit

import Dao.model.Fruits._
import Dao.model.Apples._
import Dao.model.Coffees.coffees
import Dao.model.Pears.pears
import Dao.model.{Apple, Fruit, Pear}
import play.api.Logger
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by ad on 2017/5/31.
  */
class SchemaDemo {

}

object SchemaDemo {
  val databaseConfig = Database.forConfig("mysql")
  val db = databaseConfig
  var log = Logger(this.getClass)

  def main(args: Array[String]) = {
    setup()
    println("end")
  }

  def setup() = {
    val schema = fruits.schema ++ apples.schema
    val q = schema.create
    schema.create.statements.foreach(println)
    schema.truncate.statements.foreach(println)
    schema.drop.statements.foreach(println)
    var f = db.run(DBIO.seq(
      schema.create,
      //      schema.drop
//      coffees ++= Seq(
//        ("Colombian", 101, 7.99, 0, 0),
//        ("French_Roast", 49, 8.99, 0, 0),
//        ("Espresso", 150, 9.99, 0, 0),
//        ("Colombian_Decaf", 101, 8.99, 0, 0),
//        ("French_Roast_Decaf", 49, 9.99, 0, 0)
//      ),
      fruits ++= Seq(
        Fruit(Option(1), "apple", "guoshi"),
        Fruit(Option(2), "orange", "huainan")
      ),
      apples ++= Seq(
        Apple(Option(1), "shandong apple", 1, Option(251), 1),
        Apple(None, "hebei apple", 1, Option(125), 1)
      )
    ).transactionally)
    Await.result(f, Duration.Inf)
    println(f.value.get.get)
  }

  def waitingUntilCompleted(future: Future[Unit]) = {
    while (!future.isCompleted) {
      println("waiting for db operation")
      TimeUnit.SECONDS.sleep(10)
    }
  }

  def joinDemo() = {
    val crossJoin = for {
      (c, s) <- apples join fruits
    } yield (c.id , c.name, c.typ, s.id, s.name)

    val crossJoin2 = for {
      (a, f) <- apples join fruits on (_.typ === _.id)
      (a, c) <- apples join coffees
      if a.id === 1
    } yield (a.id , a.name, a.typ, f.id, f.name, c.name, c.price)

    val leftOuterJoin = for {
      (c, s) <- apples joinLeft fruits on (_.typ === _.id)
    } yield (c.name, s.map(_.name))

    val fullOuterJoin = for {
      (c, s) <- apples joinFull fruits on (_.typ === _.id)
    } yield (c.map(_.name), s.map(_.name))

    val monadicInnerJoin = for {
      c <- apples
      s <- fruits if c.typ === s.id
    } yield (c.name, s.name)
    // compiles to SQL:
    //   select x2."COF_NAME", x3."SUP_NAME"
    //     from "COFFEES" x2, "SUPPLIERS" x3
    //     where x2."SUP_ID" = x3."SUP_ID"

    val sql = for {
      a <- apples
      f <- fruits
      c <- coffees
      if a.typ === f.id && a.id === 1
    } yield (a.id , a.name, a.typ, f.id, f.name, c.name, c.price)

    val sql2 = for {
      a <- apples
      f <- fruits
      c <- coffees
      if a.typ === f.id && a.id === 1
    } yield (a.id , a.name, a.typ, f.id, f.name, c.name, c.price)
//***************************************************************
    var q1 = pears += Pear(73, "guangdong33", 333, 444, 1)
    var q2 = pears.filter(_.id===103).sortBy(_.id desc).result
    var q3 = pears.sortBy(_.id desc).result
    val q = q1 zip q2 zip q3
    var ff = db.run(
      for{
        ((a,b),c) <- q
      } yield (a, b, c)
    )

    var fff = db.run(
      for{
        a <- q
      } yield (a)
    )
    //***************************************************************
//return AutoInc
var  qq= (pears returning pears.map(_.id)) += Pear(73, "guangdong35", 333, 444, 1)
    var ffff = db.run(qq)
//    *********************************************************
    var app = new Apple(None, "guangdong38", 333, Option(444), 1)
    qq= (pears returning pears.map(_.id)) += Pear(73, "guangdong36", 333, 444, 1)
    var sql22 = for{
      id <- qq
      r <- apples += app.copy(size = id, weight = Option(id))
    } yield (r)
    var fffff = db.run(sql22.transactionally)
//    ****************************************************
    var  q22= (pears returning pears.map(_.id)) += Pear(73, "guangdong38", 333, 444, 1)
    var q33 = apples += app.copy(size = 1024, weight = Option(1024), name = "guangdong30")
    var sql4 = for{
      id <- q22
      _ <- {app = app.copy(size = id, weight = Option(id))
        q33}
      r <- apples += app.copy(size = id, weight = Option(id), name = "guangdong38")
    } yield (r)
    //********************************
    val sql11111 = DBIO.sequence((4 to 5).map(i => {
      pears += Pear(73, "guangdong" + i, 333, 444, 1)
    }))
    val sql22222 = DBIO.sequence((6 to 8).map(i => {
      pears += Pear(73, "guangdong" + i, 333, 444, 1)
    }))
    var sql0 = DBIO.sequence(Seq(sql11111, sql22222))
    var ff12 = db.run(sql0.transactionally)
//************************************************
var sql5 = for{
  ps <- pears.filter(_.id > 137).result
  rs <- DBIO.sequence(ps.map(p => {
    pears += Pear(73, "guangdong" + p.id, 333, 444, 1)
  }))
} yield (ps, rs)
    var ff5 = db.run(sql5.transactionally)


//  ************************************************
var sql6 = for{
  ps <- pears.filter(_.id > 139).result
  rs <- DBIO.sequence((1 to 3).map(i => {
    DBIO.sequence(ps.map(p => {
      pears += Pear(73, "guangdong" + p.id + "-" + i, 333, 444, 1)
    }))
  }))
} yield (ps, rs)
    var _ff = db.run(sql6.transactionally)

//    *************************************************************
    val ff2 = db.run(sql2.result)
    Await.result(ff2, Duration.Inf)
    val rs2 = ff2.value.get.get
    rs2.foreach(println)
  }
}
