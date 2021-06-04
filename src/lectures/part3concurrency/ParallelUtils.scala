package lectures.part3concurrency

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicReference
import scala.collection.parallel.CollectionConverters._
import scala.collection.parallel.ForkJoinTaskSupport
import scala.collection.parallel.immutable.ParVector

object ParallelUtils extends App {

  // 1. parallel collections

  val parList = List(1, 2, 3).par

  val aParVector = ParVector[Int](1, 2, 3)

  /*
    Seq
    Vector
    Map - Hash, Trie
    Set - Hash, Trie
   */

  def measure[T](operation: => T): Long = {
    val time = System.currentTimeMillis()
    operation
    System.currentTimeMillis() - time
  }

  val list = (1 to 10000).toList
  val serialTime = measure {
    list.map(_ + 1)
  }
  println("Serial Time: " + serialTime)

  val parallelTime = measure {
    list.par.map(_ + 1)
  }
  println("Parallel Time: " + parallelTime)

  /*
    map-reduce model
      - split the elements into chunks - splitter
      - operation
      - recombine - combiner
   */

  // map, flatMap, filter, reduce, fold

  // fold, reduce are not safe
  println(List(1, 2, 3).reduce(_ - _))
  println(List(1, 2, 3).par.reduce(_ - _))

  // synchronization
  var sum = 0
  List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).par.foreach(sum += _)
  println(sum) // race condition!

  // configuring
  aParVector.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(2))

  // 2 - atomic operations and references

  val atomic = new AtomicReference[Int](2)

  val currentValue = atomic.get() // thread safe read
  atomic.set(4) // thread safe write

  atomic.getAndSet(5) // thread safe combo
  atomic.compareAndSet(38, 56)
  // if the value is 38, then set it to 56
  // reference equality

  atomic.updateAndGet(_ + 1) // thread-safe function run
  atomic.getAndUpdate(_ + 1)

  atomic.accumulateAndGet(12, _ + _) // thread safe accumulation
  atomic.getAndAccumulate(12, _ + _)

}
