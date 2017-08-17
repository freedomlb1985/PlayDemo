package Dao


import java.util.concurrent.TimeUnit
import javax.sql.rowset.serial.SerialBlob

import slick.jdbc.MySQLProfile.api._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import Dao.model.Suppliers._
import Dao.model.Coffees.coffees
import play.api.{Logger, Play}
import slick.basic.DatabasePublisher
import slick.driver.MySQLDriver

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by ad on 2017/5/27.
  */
class SlickDemo {

}

object SlickDemo {
  //  val databaseConfig = DatabaseConfigProvider.get("slick_demo")(Play.current)
  val databaseConfig = Database.forConfig("mysql")
  val db = databaseConfig
  var log = Logger(this.getClass)


  def main(args: Array[String]) = {
//    setup()


//    var a = commonQuery()
//    waitingUntilCompleted(a)
//    a.value.get.get.foreach{
//      case (name, supID, price, sales, total) =>
//        println("  " + name + "\t" + supID + "\t" + price + "\t" + sales + "\t" + total)
//    }
//    println(a)

//    val a = queryDemo1()

//    var a = queryMultiTables()
//    println(a)
//    log.trace("trace")
//    log.debug("debug")
//    log.info("info")
//    log.warn("warn")
//    log.error("error")


//    var a = streamQuery()
//    println(a)

//    var a = transactionQuery()
//    println(a)

    var a = rollbackTest()
    println(a)
  }



  def waitingUntilCompleted(future: Future[Unit]) = {
    while (!future.isCompleted) {
      println("waiting for db operation")
      TimeUnit.SECONDS.sleep(10)
    }
  }

  def commonQuery(): Future[Seq[(String, Int, Double, Int, Int)]] = {
    println("Coffees:")
    db.run(coffees.result)
  }

  def queryDemo1() = {
    val q1 = for(c <- coffees)
      yield LiteralColumn("  ") ++ c.name ++ "\t" ++ c.supID.asColumnOf[String] ++
        "\t" ++ c.price.asColumnOf[String] ++ "\t" ++ c.sales.asColumnOf[String] ++
        "\t" ++ c.total.asColumnOf[String]
    // The first string constant needs to be lifted manually to a LiteralColumn
    // so that the proper ++ operator is found

    // Equivalent SQL code:
    // select '  ' || COF_NAME || '\t' || SUP_ID || '\t' || PRICE || '\t' SALES || '\t' TOTAL from COFFEES

    db.stream(q1.result).foreach(println)
    TimeUnit.SECONDS.sleep(10)
  }

  def queryMultiTables() = {
    val q3 = for {
      c <- coffees if c.price < 9.0
      s <- c.supplier
    } yield (c.name, s.name)
    println(q3)
    var a = db.run(q3.result)
    // Equivalent SQL code:
    // select c.COF_NAME, s.SUP_NAME from COFFEES c, SUPPLIERS s where c.PRICE < 9.0 and s.SUP_ID = c.SUP_ID
    TimeUnit.SECONDS.sleep(10)
    a.value.get.get.foreach{
      case (cid, sid) => println(cid + ", " + sid)
    }
  }

  def streamQuery() = {
    var q = for (c <- coffees) yield c.name
    val p: DatabasePublisher[String] = db.stream(q.result)
    p.foreach(a => println(a))

    p.foreach{
      s => println("second: " + s)
    }
    TimeUnit.SECONDS.sleep(15)
  }

  def transactionQuery() = {
    val a = (for {
      ns <- coffees.filter(_.name.startsWith("ESPRESSO")).map(_.name).result
      _ <- DBIO.seq(ns.map(n => coffees.filter(_.name === n).delete): _*)
    } yield ()).transactionally

    val f: Future[Unit] = db.run(a)
    waitingUntilCompleted(f)
    println(f.value)
  }

  def actionsGenerator() = {
    val ins1: DBIO[Int] = coffees += ("Colombian", 101, 7.99, 0, 0)
    val ins2: DBIO[Int] = coffees += ("French_Roast", 101, 8.99, 0, 0)

    val a1: DBIO[Unit] = DBIO.seq(ins1, ins2)

    val a2: DBIO[Int] = ins1 andThen ins2

    val a3: DBIO[(Int, Int)] = ins1 zip ins2

    val a4: DBIO[Vector[Int]] = DBIO.sequence(Vector(ins1, ins2))
  }

  def rollbackTest() = {
    val countAction = coffees.length.result

    val rollbackAction = (coffees ++= Seq(
      ("Cold_Drip", 101, 7.99, 0, 0),
      ("Dutch_Coffee", 101, 7.99, 0, 0)
    )).flatMap { _ =>
      DBIO.failed(new Exception("Roll it back"))
    }.transactionally

    val errorHandleAction = rollbackAction.asTry.flatMap {
      case Failure(e: Throwable) => DBIO.successful(e.getMessage)
      case Success(_) => DBIO.successful("never reached")
    }

    // Here we show that that coffee count is the same before and after the attempted insert.
    // We also show that the result of the action is filled in with the exception's message.
    val f = db.run(countAction zip errorHandleAction zip countAction).map {
      case ((initialCount, result), finalCount) =>
        // init: 5, final: 5, result: Roll it back
        println(s"init: ${initialCount}, final: ${finalCount}, result: ${result}")
        result
    }
    TimeUnit.SECONDS.sleep(15)
  }


  def setup() = {
    val setup = DBIO.seq(
      // Create the tables, including primary and foreign keys
      //      (suppliers.schema ++ coffees.schema).create,
      //      coffees.schema.create,

      // Insert some suppliers
      suppliers += (101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199"),
      suppliers += (49, "Superior Coffee", "1 Party Place", "Mendocino", "CA", "95460"),
      suppliers += (150, "The High Ground", "100 Coffee Lane", "Meadows", "CA", "93966"),
      // Equivalent SQL code:
      // insert into SUPPLIERS(SUP_ID, SUP_NAME, STREET, CITY, STATE, ZIP) values (?,?,?,?,?,?)

      // Insert some coffees (using JDBC's batch insert feature, if supported by the DB)
      coffees ++= Seq(
        ("Colombian", 101, 7.99, 0, 0),
        ("French_Roast", 49, 8.99, 0, 0),
        ("Espresso", 150, 9.99, 0, 0),
        ("Colombian_Decaf", 101, 8.99, 0, 0),
        ("French_Roast_Decaf", 49, 9.99, 0, 0)
      )
      // Equivalent SQL code:
      // insert into COFFEES(COF_NAME, SUP_ID, PRICE, SALES, TOTAL) values (?,?,?,?,?)
    )

    //    var f2 = db.run(
    //      coffees.schema.create
    //    )
    //
    //    f2.map(e => {
    //      println(e.toString)
    //    })
    //    import java.util.concurrent.Executors
    //
    //    import scala.concurrent.ExecutionContext
    //
    //    implicit val executionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(5))

    var setupFuture = db.run(setup)

    setupFuture.onFailure {
      case t => println("An error has occured: " + t.getMessage)
    }
    setupFuture.onSuccess {
      case posts => println("success in onSuccess: " + posts)
    }
    setupFuture.onComplete {
      case Success(posts) => println("success: " + posts)
      case Failure(t) => println("An error has occured: " + t.getMessage)
    }

    setupFuture.foreach(e => {
      println(e.toString)
    })
    //    this.wait(1000 * 300)
    while (!setupFuture.isCompleted) {
      println("waiting for db operation")
      TimeUnit.SECONDS.sleep(10)
    }
    var v = setupFuture.value
    println(v)

  }

}

object o1 {
  def p = println("o1")
}

object o2 {
  val o = o1
  o.p

}
