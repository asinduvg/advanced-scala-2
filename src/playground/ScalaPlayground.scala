package playground

object ScalaPlayground extends App {

  implicit class RichInt(n: Int) {
    def isOdd: Boolean = n % 2 != 0
  }

  println(42.isOdd)

}
