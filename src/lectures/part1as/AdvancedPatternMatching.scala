package lectures.part1as

import java.awt.print.Book

object AdvancedPatternMatching extends App {

  val numbers = List(1)
  val description: Unit = numbers match {
    case head :: nil => println(s"the only element is $head")
    case _ =>
  }

  /*
      - constants
      - wild cards
      - case classes
      - tuples
      - some special magic like above
   */

  class Person(val name: String, val age: Int)

  object Person {
    def unapply(person: Person): Option[(String, Int)] = Some((person.name, person.age))

    def unapply(age: Int): Option[String] = {
      Some(if (age < 21) "minor" else "major")
    }

  }

  val bob = new Person("Bob", 25)
  val greeting = bob match {
    case Person(n, a) => s"Hi, my name is $n and I'm $a y.o"
  }

  println(greeting)

  val legalStatus = bob.age match {
    case Person(status) => s"My legal status is $status"
  }

  println(legalStatus)

  /*
  Exercise
   */

  //  val n: Int = 45
  //  val mathProperty = n match {
  //    case x if x < 10 => "single digit"
  //    case x if x % 2 == 0 => "an even number"
  //    case _ => "no property"
  //  }

  object even {
    def unapply(arg: Int): Option[Boolean] =
      if (arg % 2 == 0) Some(true)
      else None
  }

  object singleDigit {
    def unapply(arg: Int): Boolean = arg > -10 && arg < 10
  }

  val n: Int = 1
  val mathProperty = n match {
    case singleDigit() => "single digit"
    case even(_) => "an even number"
    case _ => "no property"
  }

  println(mathProperty)

  // infix patterns
  case class Or[A, B](a: A, b: B)

  val either = Or(2, "two")
  val humanDescription = either match {
    case number Or string => s"$number is $string"
  }

  println(humanDescription)

  // decomposing sequences
  val vararg = numbers match {
    case List(1, _*) => "starting with 1"
  }

  abstract class MyList[+A] {
    def head: A = ???

    def tail: MyList[A] = ???
  }

  case object Empty extends MyList[Nothing]

  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if (list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
  }

  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))
  val decomposed = myList match {
    case MyList(1, 2, _*) => "starting with 1, 2"
    case _ => "something else"
  }

  println(decomposed)

  // custom return types for unapply
  // isEmpty: Boolean, get: Something

  abstract class Wrapper[T] {
    def isEmpty: Boolean

    def get: T
  }

  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      override def isEmpty: Boolean = false

      override def get: String = person.name
    }
  }

  println(bob match {
    case PersonWrapper(name) => s"This person's name is $name"
    case _ => "An alien"
  })

}
