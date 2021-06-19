package lectures.part4implicits

import scala.language.implicitConversions

object ImplicitsIntro extends App {

  case class Person(name: String) {
    def greet = s"Hi my name is $name"
  }

  implicit def fromStringToPerson(str: String): Person = Person(str)

  println("Peter".greet) // println(fromStringToPerson("Person").greet)

  // implicit parameters
  def increment(x: Int)(implicit amount: Int) = x + amount

  implicit val defaultAmount: Int = 10

  println(increment(15)) // NOT default args

  println(increment(15)(30))


}
