package utils

/**
  * Created by frey on 2017/7/26.
  */
class ForDemo {

}

object ForDemo {
  val states = List("Alabama", "Alaska", "Virginia", "Wyoming")
  val vec = Vector("Alabama", "Alaska", "Virginia", "Wyoming")
  def main(args: Array[String]): Unit = {
    for{
      state <- states
    } println(state)

    for {
      s <- states
      c <- s
      if (c.isUpper)
    } yield s"$c - ${c.toUpper}"

    for {
      i1 <- positive(5).right
      i2 <- positive(10 * i1).right
      i3 <- positive(25 * i2).right
      i4 <- positive(-5).right
    } yield ( i1 + i2 + i3, i4)
  }

  def positive(i: Int) = {
    if(i > 0) {
      Right(i)
    } else {
      Left(s"nonpositive number $i")
    }
  }
}
