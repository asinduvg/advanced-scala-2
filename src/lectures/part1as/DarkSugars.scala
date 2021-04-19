package lectures.part1as

import scala.util.Try

object DarkSugars extends App {

  // syntactic sugar1: methods with single param
  def singleArgMethod(arg: Int): String = s"$arg little ducks.."

  val description = singleArgMethod {
    // write some code
    42
  }

  val aTryInstance = Try {
    throw new RuntimeException
  }

  List(1, 2, 3).map { x =>
    x + 1
  }

  // syntactic sugar 2: single abstract method
  trait Action {
    def act(x: Int): Int
  }

  val anInstance: Action = new Action {
    override def act(x: Int): Int = x + 1
  }

  val aFunkyInstance: Action = (x: Int) => x + 1

  // example: Runnables
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("Hello, Scala")
  })

  val aSweetThread = new Thread(() => println("Sweet, Scala"))

  abstract class AnAbstractType {
    def implemented: Int = 23

    def f(a: Int): Unit
  }

  val anAbstractInstance: AnAbstractType = (a: Int) => println("sweet")

  // syntactic sugar 3: the :: and #:: methods are special
  val prependedList = 2 :: List(3, 4, 5)

  // List(3, 4, 5).::(3) // right associativity
  // ends in colon, it's right associative

  class MyStream[T] {
    def -->:(value: T): MyStream[T] = this
  }

  val myStream = 1 -->: 2 -->: 3 -->: new MyStream[Int]

  // syntactic sugar 4: multi word method naming
  class TeenGirl(name: String) {
    def `and then said`(gossip: String): Unit = println(s"$name said $gossip")
  }

  val lilly = new TeenGirl("Lilly")
  lilly `and then said` "scala is so sweet"

  // syntactic sugar 5: infix types
  class Composite[A, B]

  //  val composite: Composite[Int, String] = ??? // is eqivalent to,
  val composite: Int Composite String = ???

  class -->[A, B]

  val towards: Int --> String = ???

  // syntactic sugar 6: update() is very special, much like apply()
  val anArray = Array(1, 2, 3)
  anArray(2) = 7 // rewritten to anArray.update(2, 7)
  // used in mutable collections

  // syntactic sugar 7: setters for mutable collections
  class Mutable {
    private var internalMember: Int = 0 // private for OO encapsulation
    def member: Int = internalMember // getter
    def member_=(value: Int): Unit = {
      internalMember = value
    }
  }

  val aMutableContainer = new Mutable
  aMutableContainer.member = 43 // this calls to member_= method

}
