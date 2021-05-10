package exercises

import scala.annotation.tailrec

abstract class MyStream[+A] {
  def isEmpty: Boolean

  def head: A

  def tail: MyStream[A]

  def #::[B >: A](element: B): MyStream[B] // prepend operator

  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] // concatenates two streams

  def foreach(f: A => Unit): Unit

  def map[B](f: A => B): MyStream[B]

  def flatMap[B](f: A => MyStream[B]): MyStream[B]

  def filter(predicate: A => Boolean): MyStream[A]

  def take(n: Int): MyStream[A] // takes the first n elements out of this stream

  def takeAsList(n: Int): List[A] = take(n).toList()

  /*
  [1 2 3].toList([]) =
  [2 3].toList([1]) =
  [3].toList([2 1]) =
  [].toList([3 2 1]) =
  [1 2 3]
   */

  @tailrec
  final def toList[B >: A](acc: List[B] = Nil): List[B] =
    if (isEmpty) acc.reverse
    else tail toList (head :: acc)
}

object EmptyStream extends MyStream[Nothing] {
  def isEmpty: Boolean = true

  def head: Nothing = throw new NoSuchElementException

  def tail: MyStream[Nothing] = throw new NoSuchElementException

  def #::[B >: Nothing](element: B): MyStream[B] = new Cons[B](element, this) // prepend operator

  def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] = anotherStream // concatenates two streams

  def foreach(f: Nothing => Unit): Unit = ()

  def map[B](f: Nothing => B): MyStream[B] = this

  def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this

  def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

  def take(n: Int): MyStream[Nothing] = this // takes the first n elements out of this stream

}

class Cons[+A](hd: A, tl: => MyStream[A]) extends MyStream[A] {
  def isEmpty: Boolean = false

  override val head: A = hd

  override lazy val tail: MyStream[A] = tl // call by need

  def #::[B >: A](element: B): MyStream[B] = new Cons[B](element, this) // prepend operator

  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] = new Cons[B](head, tail ++ anotherStream) // concatenates two streams

  def foreach(f: A => Unit): Unit = {
    f(head)
    tail foreach f
  }

  def map[B](f: A => B): MyStream[B] = new Cons[B](f(head), tail map f) // preserves lazy eval

  def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(head) ++ (tail flatMap f)

  def filter(predicate: A => Boolean): MyStream[A] =
    if (predicate(head)) new Cons[A](head, tail filter predicate)
    else tail filter predicate // preserves lazy eval

  def take(n: Int): MyStream[A] =
    if (n <= 0) EmptyStream
    else if (n == 1) new Cons[A](head, EmptyStream)
    else new Cons[A](head, tail take n - 1)
  // takes the first n elements out of this stream

}

object MyStream {
  def from[A](start: A)(generator: A => A): MyStream[A] =
    new Cons[A](start, MyStream.from(generator(start))(generator))
}

object StreamsPlayground extends App {
  val naturals = MyStream.from(1)(_ + 1)
  //  println(naturals.head)
  //  println(naturals.tail.head)
  //  println(naturals.tail.tail.head)

  val startFrom0 = 0 #:: naturals //naturals.#::(0)
  //  println(startFrom0.head)

  //  startFrom0.take(10000).foreach(println)

  // map, flatMap
  //  println(startFrom0.map(_ * 2).take(100).toList())
  //  println(startFrom0.flatMap(x => new Cons[Int](x, new Cons[Int](x + 1, EmptyStream))).take(10).toList())
  //  println(startFrom0.filter(_ < 2).take(10).toList())

  // Exercise on streams
  // 1 - Stream of fibonacci numbers
  // 2 - stream of prime numbers with Eratosthenes' sieve
  /*
    [2, 3, 4, ...]
    filter out all numbers divisible by 2
    [2, 3, 5, 7, 9, 11, ...]
    filter out all numbers divisible by 3
    [2, 3, 5, 7, 11, 13..]
    filter out all numbers divisible by 5
    ...
   */

  def fibonacci(first: Int, second: Int): MyStream[Int] = {
    new Cons[Int](first, fibonacci(second, first + second))
  }

  def eratosthenes(numbers: MyStream[Int]): MyStream[Int] = {
    if (numbers.isEmpty) numbers
    else new Cons[Int](numbers.head, eratosthenes(numbers.tail.filter(_ % numbers.head != 0)))
  }

  //  println(fibonacci(1, 1).take(100).toList())
  println(eratosthenes(MyStream.from(2)(_ + 1)).take(10).toList())
}