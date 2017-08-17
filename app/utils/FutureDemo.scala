package utils

import scala.concurrent.Future

/**
  * Created by ad on 2017/5/26.
  */
class FutureDemo {


}
object FutureDemo {
  import java.util.concurrent.Executors

  import scala.concurrent.ExecutionContext

  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(5))
  var f = Future{
    "dsafdsaf"
  }
}
