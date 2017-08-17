package utils

import com.sun.glass.ui.Pixels.Format
import play.api.libs.json.{JsPath, JsValue, Json}

/**
  * Created by ad on 2017/5/24.
  */
class JsonUtil {

}

case class Resident(name: String, age: Int, role: Option[String])

object Resident {
  implicit val residentReads = Json.reads[Resident]
  implicit val residentWrites = Json.writes[Resident]
  implicit val residentFormat = Json.format[Resident]
}

object Demo {
  def main(args: Array[String]) = {
    val resident = Resident(name = "Fiver", age = 4, role = None)

    val residentJson: JsValue = Json.toJson(resident)
    println(residentJson.toString())

    //
    val resident2 = Resident(name = "Fiver", age = 4, role = Option("admin"))

    val residentJson2: JsValue = Json.toJson(resident2)
    println(residentJson2.toString())

    //
    val jsonString: JsValue = Json.parse(
      """{
    "name" : "Fiver",
    "age" : 4
     }""")

    var res: Resident = jsonString.as[Resident]
    println(res.toString)

    //
    val jsonString2: JsValue = Json.parse(
      """{
    "name" : "Fiver",
    "age" : 4,
    "role": "user"
     }""")

    var res2: Resident = jsonString2.as[Resident]
    println(res2.toString)

  }
}

import play.api.libs.json._
import play.api.libs.functional.syntax._
case class User(id: Long, name: String, friends: Seq[User] = Seq.empty)
object User {

  // In this format, an undefined friends property is mapped to an empty list
  //  implicit val format: Format[User] = (
  //    (JsPath \ "id").format[Long] and
  //      (JsPath \ "name").format[String] and
  //      (JsPath \ "friends").lazyFormatNullable(implicitly[Format[Seq[User]]])
  //        .inmap[Seq[User]](_ getOrElse Seq.empty, Some(_))
  //    )(User.apply, unlift(User.unapply))


  case class Location(lat: Double, long: Double)

  case class Place(name: String, location: Location, residents: Seq[Resident])

  implicit val locationWrites: Writes[Location] = (
    (JsPath \ "lat").write[Double] and
      (JsPath \ "long").write[Double]
    ) (unlift(Location.unapply))
  implicit val placeWrites: Writes[Place] = Json.writes[Place]
//  implicit val residentWrites2: Writes[Resident] = (
//    (JsPath \ "name").write[String] and
//      (JsPath \ "age").write[Int] and
//      (JsPath \ "role").writeNullable[String]
//    ) (unlift(Resident.unapply))
//
//  implicit val placeWrites: Writes[Place] = (
//    (JsPath \ "name").write[String] and
//      (JsPath \ "location").write[Location] and
//      (JsPath \ "residents").write[Seq[Resident]]
//    ) (unlift(Place.unapply))


  val place = Place(
    "Watership Down",
    Location(51.235685, -1.309197),
    Seq(
      Resident("Fiver", 4, None),
      Resident("Bigwig", 6, Some("Owsla"))
    )
  )

  val json = Json.toJson(place)

  case class Page (count: Int, results: Seq[String])

  implicit val pageWrites: Writes[Page] = Json.writes[Page]
  var p = new Page(10, Seq("a", "b"))
  var s = Json.toJson(p)
  println(s.toString())
}