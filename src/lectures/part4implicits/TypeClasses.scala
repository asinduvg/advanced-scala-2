package lectures.part4implicits

import lectures.part4implicits.OrganizingImplicits.{Person, persons}

object TypeClasses extends App {

  trait HTMLWritable {
    def toHTML: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    override def toHTML: String = s"<div>$name ($age yo) <a href=$email/> </div>"
  }

  User("John", 32, "john@rockthejvm.com").toHTML

  /*
    1 - For the types we write
    2 - ONE implementation out of quite a number
   */

  // option 2 - pattern matching
  object HTMLSerializerPM {
    def serializeToHTML(value: Any): Unit = value match {
      case User(n, a, e) =>
      //      case java.util.Date =>
      case _ =>
    }

    /*
      - Lost type safety
      - need to modify the code everytime
      - still ONE implementation
     */
  }

  trait HTMLSerializer[T] {
    def serialize(value: T): String
  }

  implicit object UserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = s"<div>${user.name} (${user.age} yo) <a href=${user.email}/> </div>"
  }

  val john = User("John", 32, "john@rockthejvm.com")
  println(UserSerializer.serialize(john))

  // 1 - we can serialize for other types

  import java.util.Date

  object DateSerializer extends HTMLSerializer[Date] {
    override def serialize(date: Date): String = s"<div>${date.toString}</div>"
  }

  // 2 - we can define multiple serializers
  object PartialUserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = s"<div>${user.name}</div>"
  }

  // TYPE CLASSES
  trait MyTypeClassTemplate[T] {
    def action(value: T): String
  }

  object MyTypeClassTemplate {
    def apply[T](implicit instance: MyTypeClassTemplate[T]): MyTypeClassTemplate[T] = instance
  }

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

  // part2

  object HTMLSerializer {
    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String =
      serializer.serialize(value)

    def apply[T](implicit serializer: HTMLSerializer[T]): HTMLSerializer[T] = serializer
  }

  implicit object IntSerializer extends HTMLSerializer[Int] {
    override def serialize(value: Int): String = s"<div style: color=blue>$value</div>"
  }

  println(HTMLSerializer.serialize(42))
  println(HTMLSerializer.serialize(john))

  // access to the entire type class interface
  println(HTMLSerializer[User].serialize(john))

  /*
    EXERCISE
      - implement the type class pattern for the Equality tc
   */

  object Equal {
    def apply[T](val1: T, val2: T)(implicit comparator: Equal[T]): Boolean = comparator.apply(val1, val2)
  }

  println(Equal[Person](Person("Amy", 12), Person("Amy", 44)))

  Equal(Person("Amy", 12), Person("Amy", 44)) // AD-HOC polymorphism

}
