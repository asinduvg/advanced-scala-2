package lectures.part5ts

object StructuralType extends App {

  // structural types

  type JavaClosable = java.io.Closeable

  class HipsterClosable {
    def close(): Unit = println("yeah yeah i'm closing")

    def closeSilently(): Unit = println("i am silent")
  }

  //  def closeQuietly(closable: JavaClosable OR HipsterClosable) // ?!

  type UnifiedClosable = {
    def close(): Unit
  } // Structural type

  def closeQuietly(unifiedClosable: UnifiedClosable): Unit = unifiedClosable.close()

  closeQuietly(new JavaClosable {
    override def close(): Unit = ???
  })

  closeQuietly(new HipsterClosable) // ok

  // TYPE REFINEMENTS

  type AdvancedClosable = JavaClosable {
    def closeSilently(): Unit
  }

  class AdvancedJavaClosable extends JavaClosable {
    override def close(): Unit = println("Java closes")

    def closeSilently(): Unit = println("Java closes silently")
  }

  def closeShh(advancedClosable: AdvancedClosable): Unit = advancedClosable.close()

  closeShh(new AdvancedJavaClosable) // ok

  //  closeShh(new HipsterClosable) // not ok

  // using structural types as standalone types
  def altClose(closable: {def close(): Unit}): Unit = closable.close()

  // type checking

  type SoundMaker = {
    def makeSound(): Unit
  }

  class Dog {
    def makeSound(): Unit = println("bark!")
  }

  class Car {
    def makeSound(): Unit = println("vroom!")
  }

  val dog: SoundMaker = new Dog
  val Car: SoundMaker = new Car

  // static duck typing

  // CAVEAT: Based on reflection (heavy impact on performance)

  /*
    EXERCISES
   */

  // 1.
  trait CBL[+T] {
    def head: T

    def tail: CBL[T]
  }

  class Human {
    def head: Brain = new Brain
  }

  class Brain {
    override def toString: String = "BRAINZ!"
  }

  def f[T](somethingWithAHead: {def head: T}): Unit = println(somethingWithAHead.head)

  /*
    f is compatible with a CBL and with a human?
   */

  case object CBNil extends CBL[Nothing] {
    override def head: Nothing = ???

    override def tail: CBL[Nothing] = ???
  }

  case class CBCons[T](override val head: T, override val tail: CBL[T]) extends CBL[T]

  f(CBCons(2, CBNil))
  f(new Human) // ?! T = Brain

  //2.
  object HeadEqualizer {
    type Headable[T] = {def head(): T}

    def ===[T](a: Headable[T], b: Headable[T]): Boolean = a.head == b.head
  }

  /*
   is compatible with a CBL and with a human? Yes
  */

  val brainzList = CBCons(new Brain, CBNil)
  val stringsList = CBCons("Brainz", CBNil)

  //  HeadEqualizer.===(brainzList, new Human)
  // problem
  //  HeadEqualizer.===(new Human, stringsList) // not type safe

}
