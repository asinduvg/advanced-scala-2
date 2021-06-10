package lectures.part4implicits

import scala.annotation.tailrec
import scala.language.implicitConversions

object PimpMyLibrary extends App {

  // 2.isPrime

  implicit class RichInt(val value: Int) extends AnyVal {
    def isEven: Boolean = value % 2 == 0

    def sqrt: Double = Math.sqrt(value)

    def times(function: () => Unit): Unit = {
      @tailrec
      def timeAux(n: Int): Unit = {
        if (n <= 0) ()
        else {
          function()
          timeAux(n - 1)
        }
      }

      timeAux(value)
    }

    def *[T](list: List[T]): List[T] = List.fill(value)(list).flatten
  }

  implicit class RicherInt(value: Int) {
    def isOdd: Boolean = value % 2 == 1
  }

  new RichInt(42).sqrt

  println(42.isEven) // new RichInt(42).isEven
  // type enrichment = pimping

  1 to 10

  import scala.concurrent.duration._

  3.seconds

  // compiler does do multiple implicit searches
  println(42.isOdd)

  /*
    EXERCISE
      Enrich the String class
        - asInt
        - encrypt (with ceaser's cypher with the key 3)

      Keep enriching Int class
        - times(function)
           3.times(() => ...)
        - *
           3 * List(1, 2) => List(1, 2, 1, 2, 1, 2)
   */

  implicit class RichString(val value: String) {
    def asInt: Any = {
      val intOrNot = try {
        Some(Integer.parseInt(value.trim))
      }
      catch {
        case _: NumberFormatException => None
      }
      intOrNot match {
        case Some(i) => i
        case None => "Cannot convert to Int"
      }
    }

    def encrypt: String = value.map(c => (c + 3).toChar)
  }

  println("123".asInt)
  println("abc".encrypt)

  println(3 * List(1, 2, 3))
  3.times(() => println("Scala Rocks!"))

  // "3" / 4
  implicit def stringToInt(string: String): Int = Integer.valueOf(string)

  println("3" / 3) // stringToInt("3") / 3

  // equivalent: implicit class RichAltInt(value: Int)
  class RichAltInt(value: Int)

  implicit def enrich(value: Int): RichAltInt = new RichAltInt(value)

  // danger zone
  implicit def intToBoolean(i: Int): Boolean = i == 1 // this is a wrapper
  // not a best practice to use as function(avoid implicit defs). Errors are difficult to trace back
  // keep type enrichment to type classes and implicit classes

  val aConditionedValue = if (1) "OK" else "Something wrong"
  println(aConditionedValue)

  // compiler looks for implicit classes and wrappers

}
