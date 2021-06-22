package lectures.part5ts

object PathDependantType extends App {

  class Outer {
    class Inner

    object InnerObject

    type InnerType

    def print(i: Inner): Unit = println(i)

    def printGeneral(i: Outer#Inner): Unit = println(i)
  }

  def aMethod: Int = {
    class HelperClass
    type HelperType = String
    2
  }

  // per-instance
  val o = new Outer
  //  val innerType = new o.InnerType // o.Inner is a type
  val inner = new o.Inner

  val oo = new Outer
  //  val otherInner: oo.Inner = new o.Inner // different type

  o.print(inner)
  //  oo.print(inner) // wrong

  // path depended type
  o.printGeneral(inner)
  oo.printGeneral(inner)

  /*
    Exercise
    DB keyed by Int or String, but may be others
   */

  /*
    use path dependent types
    abstract type members and/or type aliases
   */

  trait ItemLike {
    type Key
  }

  trait Item[K] extends ItemLike {
    type Key = K
  }

  trait IntItem extends Item[Int]

  trait StringItem extends Item[String]

  def get[ItemType <: ItemLike](key: ItemType#Key): ItemType#Key = key

  get[IntItem](42) // ok
  get[StringItem]("home") // ok
  //  get[IntItem]("scala") // wrong

}
