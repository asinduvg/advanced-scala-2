package lectures.part5ts

object Reflection extends App {

  // reflection + macros + quasiquotes => METAPROGRAMMING

  case class Person(name: String) {
    def sayMyName(): Unit = println(s"Hi, my name is $name")
  }

  // 0 - import

  import scala.reflect.runtime.{universe => ru}

  // 1 - Mirror
  val m = ru.runtimeMirror(getClass.getClassLoader)
  // 2 - Create a class object (description)
  val clazz = m.staticClass("lectures.part5ts.Reflection.Person") // Creating a class object by name
  // 3 - Create a reflected mirror = "can do things"
  val cm = m.reflectClass(clazz)
  // 4 - Get the constructor
  val constructor = clazz.primaryConstructor.asMethod
  // 5 - Reflect the constructor
  val constructorMirror = cm.reflectConstructor(constructor)
  // 6 - Invoke the constructor
  val instance = constructorMirror.apply("John")

  println(instance)

  // I have an instance
  val p = Person("Mary") // from the wire as a serialized object
  // method name computed from somewhere else
  val methodName = "sayMyName"
  // 1 - mirror
  // 2 - reflect the instance
  val reflected = m.reflect(p)
  // 3 - method symbol
  val methodSymbol = ru.typeOf[Person].decl(ru.TermName(methodName)).asMethod
  // 4 - reflect the method - CAN DO THINGS
  val method = reflected.reflectMethod(methodSymbol)
  // 5 - invoke the method

  method.apply()

  // type erasure -> generic types are erased at the compile time

  // pain point #1: cannot differentiate types at runtime
  val numbers = List(1, 2, 3)

  numbers match {
    case listOfStrings: List[String] => println("list of strings")
    // compile time it rewrites as,
    // listOfStrings: List => println("list of strings") which cause for a match
    case listOfNumbers: List[Int] => println("list of numbers")
  }

  // pp #2: limitations on overloads
  //  def processList(list: List[Int]): Int = 44
  //  def processList(list: List[String]): Int = 45

  // Type tags
  // 0 - import

  import ru._

  // 1 - creating a type tag "manually"
  val tTag = typeTag[Person]
  println(tTag.tpe)

  class MyMap[K, V]

  // 2 - pass type tags as implicit parameters (preferred way)
  def getTypeArguments[T](value: T)(implicit typeTag: TypeTag[T]) = typeTag.tpe match {
    case TypeRef(_, _, typeArguments) => typeArguments
    case _ => List()
  }

  val myMap = new MyMap[Int, String]
  val typeArgs = getTypeArguments(myMap) // (typeTag: TypeTag[MyMap[Int, String]])
  println(typeArgs)

  def isSubType[A, B](implicit tTagA: TypeTag[A], tTagB: TypeTag[B]): Boolean = {
    tTagA.tpe <:< tTagB.tpe
  }

  class Animal

  class Dog extends Animal

  println(isSubType[Dog, Animal])


  // I have an instance
  // 1 - mirror
  // 2 - reflect the instance
  // 3 - method symbol
  val anotherMethodSymbol = typeTag[Person].tpe.decl(ru.TermName(methodName)).asMethod
  // 4 - reflect the method - CAN DO THINGS
  val sameMethod = reflected.reflectMethod(anotherMethodSymbol)
  // 5 - invoke the method
  sameMethod.apply()

}
