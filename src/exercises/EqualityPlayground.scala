package exercises

import lectures.part4implicits.OrganizingImplicits.Person
import lectures.part4implicits.TypeClasses.{HTMLSerializer, User}


object EqualityPlayground extends App {

  // Exercise
  // - Equality

  trait Equal[T] {
    def apply(val1: T, val2: T): Boolean
  }

  implicit object NameComparator extends Equal[Person] {
    override def apply(val1: Person, val2: Person): Boolean = val1.name.equals(val2.name)
  }

  object FullEquality extends Equal[Person] {
    override def apply(val1: Person, val2: Person): Boolean = val1.name == val2.name && val1.age == val2.age
  }

  implicit object IntComparator extends Equal[Int] {
    override def apply(val1: Int, val2: Int): Boolean = val1 == val2
  }


  /*
    EXERCISE
      - implement the type class pattern for the Equality tc
   */

  object Equal {
    def apply[T](val1: T, val2: T)(implicit comparator: Equal[T]): Boolean = comparator.apply(val1, val2)
  }

  println(Equal[Person](Person("Amy", 12), Person("Amy", 44)))

  Equal(Person("Amy", 12), Person("Amy", 44)) // AD-HOC polymorphism

  /*
    EXERCISE
      - improve the Equal type class with an implicit conversion class
      === (anotherValue: T)
      !== (anotherValue: T)
   */

  implicit class EqualEnrichment[T](value1: T) {
    def ===(value2: T)(implicit comparator: Equal[T]): Boolean = comparator(value1, value2)

    def !==(value2: T)(implicit comparator: Equal[T]): Boolean = !comparator(value1, value2)
  }

  println(2 !== 3)
  /*
    2.===(3)
    new EqualEnrichment(2).===(3)
    new EqualEnrichment(2).===(3)(IntComparator)
  */
  /*
    TYPE SAFE
   */
  val john = Person("John", 32)

  println(john == 34)
  //  println(34 === john) // this can't be performed

}
