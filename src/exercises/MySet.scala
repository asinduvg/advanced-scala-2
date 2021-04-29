package exercises

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {
  // extended to get apply method from the Function object
  /*
      Exercise - implement a functional set
   */

  override def apply(elem: A): Boolean = contains(elem)

  def contains(elem: A): Boolean

  def +(elem: A): MySet[A]

  def ++(anotherSet: MySet[A]): MySet[A]

  def map[B](f: A => B): MySet[B]

  def flatMap[B](f: A => MySet[B]): MySet[B]

  def filter(predicate: A => Boolean): MySet[A]

  def foreach(f: A => Unit): Unit

  def -(elem: A): MySet[A]

  def &(anotherSet: MySet[A]): MySet[A] // intersection

  def --(anotherSet: MySet[A]): MySet[A]

  def unary_! : MySet[A]

}

/*
Exercise
  - removing an element
  - intersection with another set
  - difference with another set
 */

class EmptySet[A] extends MySet[A] {
  override def contains(elem: A): Boolean = false

  override def +(elem: A): MySet[A] = new NonEmptySet[A](elem, this)

  override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  override def map[B](f: A => B): MySet[B] = new EmptySet[B]

  override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]

  override def filter(predicate: A => Boolean): MySet[A] = this

  override def foreach(f: A => Unit): Unit = ()

  // part2

  override def -(elem: A): MySet[A] = this

  override def &(anotherSet: MySet[A]): MySet[A] = this

  override def --(anotherSet: MySet[A]): MySet[A] = this

  override def unary_! : MySet[A] = new PropertyBasedSet[A](_ => true)

}


//class AllInclusiveSet[A] extends MySet[A] {
//  override def contains(elem: A): Boolean = this
//
//  override def +(elem: A): MySet[A] = this
//
//  override def ++(anotherSet: MySet[A]): MySet[A] = this
//
//  override def map[B](f: A => B): MySet[B] = ???
//
//  override def flatMap[B](f: A => MySet[B]): MySet[B] = ???
//
//  override def filter(predicate: A => Boolean): MySet[A] = ??? // property-based set
//
//  override def foreach(f: A => Unit): Unit = ???
//
//  override def -(elem: A): MySet[A] = ???
//
//  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)
//
//  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)
//
//  override def unary_! : MySet[A] = new EmptySet[A]
//}

// all elements of type A which satisfy a property
// {x in A | property(x) }
class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {
  def contains(elem: A): Boolean = property(elem)

  // { x in A | property(x) } + element = { x in A | property(x) || x == element }
  def +(elem: A): MySet[A] =
    new PropertyBasedSet[A](x => property(x) || elem == x)

  // { x in A | property(x) } ++ set => { x in A | property(x) || set contains x}
  def ++(anotherSet: MySet[A]): MySet[A] =
    new PropertyBasedSet[A](x => property(x) || anotherSet(x))

  def map[B](f: A => B): MySet[B] = politelyFail

  def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail

  def foreach(f: A => Unit): Unit = politelyFail

  def filter(predicate: A => Boolean): MySet[A] =
    new PropertyBasedSet[A](x => property(x) && predicate(x))


  def -(elem: A): MySet[A] = filter(x => x != elem)

  def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet) // intersection

  def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)

  def unary_! : MySet[A] = new PropertyBasedSet[A](x => !property(x))

  def politelyFail = throw new IllegalArgumentException("Really deep rabbit hole!")

}

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {
  override def contains(elem: A): Boolean = elem == head || tail.contains(elem)

  override def +(elem: A): MySet[A] =
    if (this contains elem) this
    else new NonEmptySet[A](elem, this)

  /*
  [1 2 3] ++ [4 5] =
  [2 3] ++ [4 5] + 1 =
  [3] ++ [4 5] + 1 + 2 =
  [] ++ [4 5] + 1 + 2 + 3
  [4 5] + 1 + 2 + 3 = [4 5 1 2 3]
   */

  override def ++(anotherSet: MySet[A]): MySet[A] = tail ++ anotherSet + head

  override def map[B](f: A => B): MySet[B] = (tail map f) + f(head)

  override def flatMap[B](f: A => MySet[B]): MySet[B] = (tail flatMap f) ++ f(head)

  override def filter(predicate: A => Boolean): MySet[A] = {
    val filteredTail = tail filter predicate
    if (predicate(head)) filteredTail + head
    else filteredTail
  }

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail foreach f
  }

  // part 2

  override def -(elem: A): MySet[A] =
    if (head == elem) tail
    else tail - elem + head

  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet) // intersection == filtering!

  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)

  override def unary_! : MySet[A] = new PropertyBasedSet[A](x => !this.contains(x))

}

object MySet {
  /*
  val s = MySet(1, 2, 3) = buildSet(seq(1, 2, 3), [])
  = buildSet(seq(2, 3), [] + 1)
  = buildSet(seq(3), [1] + 2)
  = buildSet(seq(), [1, 2] + 3)
  = [1, 2, 3]
   */
  def apply[A](values: A*): MySet[A] = {
    @tailrec
    def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] =
      if (valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)

    buildSet(values, new EmptySet[A])
  }
}

object MySetPlayground extends App {
  val s = MySet(1, 2, 3, 4)
  //  println(s(90))
  //  s + 5 ++ MySet(6, 7) + 3 flatMap (x => MySet(x, x * 10)) filter (_ % 2 == 0) foreach println

  val negative = !s // s.unary_! = all the naturals not equal to 1, 2, 3, 4
  println(negative(2))
  println(negative(5))

  val negativeEven = negative.filter(_ % 2 == 0)
  println(negativeEven(5))

  val negativeEven5 = negativeEven + 5
  println(negative.map(_ * 2))

}
