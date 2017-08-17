package utils

/**
  * Created by frey on 2017/7/31.
  */
class ReflectDemo {

}

trait T[A] {
  val vT: A
  def mt = vT
}

class C(foo: Int) extends T[String] {
  val vT = "T"
  val vC = "C"
  def mc = vC
  class C2
}

object D {
  import scala.reflect.ClassTag
  def mkArray[T: ClassTag](elems: T*) = Array[T](elems: _*)

  import scala.reflect.runtime.universe._
  def toType2[T](t: T)(implicit tag: TypeTag[T]): Type = tag.tpe
  def toType[T: TypeTag](t: T): Type = typeOf[T]

}

class C1 {
  def m = println("c1")
}

trait T1 extends C1 {
  override def m: Unit = {
    println("t1")
    super.m
  }
}

trait T2 extends C1 {
  override def m: Unit = {
    println("t2")
    super.m
  }
}

trait T3 extends C1 {
  override def m: Unit = {
    println("t3")
    super.m
  }
}

class C2 extends T1 with T2 with T3 {
  override def m: Unit = {
    println("c2")
    super.m
  }
}

object Test{
  val c2 = new C2()
  c2.m
}



