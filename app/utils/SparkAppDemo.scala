package utils

import org.apache.spark.sql.SparkSession
/**
  * Created by frey on 2017/8/17.
  */
class SparkAppDemo {

}

object SparkAppDemo {
  def main(args: Array[String]) {
//    val logFile = "E:\\software\\apache\\spark\\spark-2.2.0-bin-hadoop2.7/README.md" // Should be some file on your system
  val logFile = "/home/frey/spark-2.2.0-bin-hadoop2.7/README.md"
    val spark = SparkSession.builder.appName("Simple Application").getOrCreate()
    val logData = spark.read.textFile(logFile).cache()
    val numAs = logData.filter(line => line.contains("a")).count()
    val numBs = logData.filter(line => line.contains("b")).count()
    println(s"Lines with a: $numAs, Lines with b: $numBs")
    spark.stop()
  }
}
