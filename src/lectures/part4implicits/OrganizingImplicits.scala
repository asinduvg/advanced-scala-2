package lectures.part4implicits

import com.sun.deploy.perf.PerfRollup

object OrganizingImplicits extends App {

  // scala predef
  implicit def reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)

  println(List(1, 2, 3, 4, 5).sorted)
  /*
    Implicit (used as implicit parameters)
     - val/var
     - object
     - accessor methods = defs with no parenthesis
   */

  //  implicit def orderPerson: Ordering[Person] = Ordering.fromLessThan(_.name < _.name)
  //  implicit def alphabeticalOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)


  // Exercise
  case class Person(name: String, age: Int)

  val persons = List(
    Person("Steve", 30),
    Person("Amy", 22),
    Person("John", 66)
  )


  /*
    Implicit scope
      - normal scope - LOCAL scope
      - imported scope
      - companions of all types involved in the method signature
        - List
        - Ordering
        - all the types involved = A or any supertype
   */

  object AlphabeticNameOrdering {
    implicit def alphabeticalOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  }

  object AgeOrdering {
    implicit def alphabeticalOrdering: Ordering[Person] = Ordering.fromLessThan(_.age < _.age)
  }

  import AgeOrdering._

  println(persons.sorted)

  /*
    Exercise
      - total price => most used(50%)
      - by unit count => 25%
      - by unit price => 25%
   */

  case class Purchase(nUnits: Int, unitPrice: Double)

  object Purchase {
    implicit def mostUsedOrdering: Ordering[Purchase] = Ordering.fromLessThan((a, b) => (a.nUnits * a.unitPrice) > (b.nUnits * b.unitPrice))
  }

  object UnitCountOrdering {
    implicit def unitCountOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.nUnits > _.nUnits)
  }

  object UnitPriceOrdering {
    implicit def unitPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.unitPrice > _.unitPrice)
  }

  import UnitPriceOrdering._

  val purchaseList = List(
    Purchase(1, 30),
    Purchase(12, 33),
    Purchase(3, 2)
  )

  println(purchaseList.sorted)


}
