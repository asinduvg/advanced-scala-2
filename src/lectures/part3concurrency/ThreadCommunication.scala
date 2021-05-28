package lectures.part3concurrency

import scala.collection.mutable
import scala.util.Random

object ThreadCommunication extends App {

  /*
    the producer-consumer problem
    producer -> [?] -> consumer
   */

  class SimpleContainer {
    private var value: Int = 0

    def isEmpty: Boolean = value == 0

    def set(newValue: Int): Unit = value = newValue

    def get: Int = {
      val result = value
      value = 0
      result
    }
  }

  def naiveProdCons(): Unit = {
    val container = new SimpleContainer
    val consumer = new Thread(() => {
      println("[consumer] waiting...")
      while (container.isEmpty) {
        println("[consumer] actively waiting...")
      }
      println("[consumer] I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[producer] computing...")
      Thread.sleep(500)
      val value = 42
      println("[producer] I have produced, after long work, the value " + value)
      container.set(value)
    })

    consumer.start()
    producer.start()
  }

  //  naiveProdCons()

  // wait and notify
  def smartProdCons(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[consumer] waiting...")
      container.synchronized {
        container.wait()
      }
      println("[consumer] got the new value " + container.get)
    })

    val producer = new Thread(() => {
      println("[producer] computing...")
      Thread.sleep(2000)
      val value = 42
      container.synchronized {
        println("[producer] I am producing the value " + value)
        container.set(value)
        container.notify()
      }
    })

    consumer.start()
    producer.start()

  }

  //  smartProdCons()

  /*
    producer -> [? ? ? ?] -> consumer
 */

  def prodConsLargeBuffer(): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    val consumer = new Thread(() => {
      val random = new Random

      while (true) {
        buffer.synchronized {

          if (buffer.isEmpty) {
            println("[consumer] buffer is empty, waiting...")
            buffer.wait()
          }

          // there must be at least one value in the buffer
          val x = buffer.dequeue()
          println("[consumer] consumed " + x)
          buffer.notify()
        }

        Thread.sleep(random.nextInt(500))
      }

    })

    val producer = new Thread(() => {
      val random = new Random
      var i = 0

      while (true) {
        buffer.synchronized {

          if (buffer.size == capacity) {
            println("[producer] buffer is full, waiting...")
            buffer.wait()
          }

          // there must be at least one empty space in the buffer
          println("[producer] producing " + i)
          buffer.enqueue(i)

          buffer.notify()
          i += 1
        }

        Thread.sleep(random.nextInt(500))

      }
    })

    producer.start()
    consumer.start()

  }

  //  prodConsLargeBuffer()

  /*
      Prod-Cons level 3
      producer1 -> [? ? ? ?] <- consumer1
      producer2 ---^      ^---- consumer2
   */

  class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread {
    override def run(): Unit = {
      val random = new Random

      while (true) {
        buffer.synchronized {

          while (buffer.isEmpty) {
            println(s"[consumer $id] buffer is empty, waiting...")
            buffer.wait()
          }

          // there must be at least one value in the buffer
          val x = buffer.dequeue()
          println(s"[consumer $id] consumed " + x)
          buffer.notify() // wake up somebody (producer or consumer)
        }

        Thread.sleep(random.nextInt(500))
      }
    }
  }

  class Producer(id: Int, buffer: mutable.Queue[Int], capacity: Int) extends Thread {
    override def run(): Unit = {
      val random = new Random
      var i = 0

      while (true) {

        buffer.synchronized {

          while (buffer.size == capacity) {
            println(s"[producer $id] buffer is full, waiting...")
            buffer.wait()
          }

          // there must be at least one empty space in the buffer
          println(s"[producer $id] producing " + i)
          buffer.enqueue(i)

          buffer.notify() // wake up somebody (producer or consumer)
          i += 1
        }

        Thread.sleep(random.nextInt(500))

      }
    }
  }

  def multiProdCons(nConsumers: Int, nProducers: Int): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    (1 to nConsumers).foreach(i => new Consumer(i, buffer).start())
    (1 to nProducers).foreach(i => new Producer(i, buffer, capacity).start())

  }

  //  multiProdCons(3, 3)

  /*
    Exercises
      1) think of an example where notifyAll acts in a different way than notify?
      2) create a deadlock
      3) create a livelock (threads are active, but they can't continue)
   */

  // notifyAll
  def testNotifyAll(): Unit = {
    val bell = new Object

    (1 to 10).foreach(i => new Thread(() => {
      bell.synchronized {
        println(s"[thread $i] waiting...")
        bell.wait()
        println(s"[thread $i] hooray...")
      }
    }).start())

    new Thread(() => {
      Thread.sleep(2000)
      println("[announcer] rock'n roll")
      bell.synchronized {
        bell.notifyAll()
      }
    }).start()

  }

  //  testNotifyAll()

  // deadlock
  case class Friend(name: String) {
    def bow(other: Friend): Unit = {
      this.synchronized {
        println(s"$this: I am bowing to my friend $other")
        other.rise(this)
        println(s"$this: My friend $other has risen")
      }
    }

    def rise(other: Friend): Unit = {
      this.synchronized {
        println(s"$this: I am rising to my friend $other")
      }
    }

    // ex 3
    var side = "right"

    def switchSide(): Unit = {
      if (side == "right") side = "left"
      else side = "right"
    }

    def pass(other: Friend): Unit = {
      while (this.side == other.side) {
        println(s"$this: Oh please, $other, feel free to pass..")
        switchSide()
        Thread.sleep(1000)
      }
    }

  }

  val sam = Friend("Sam")
  val pierre = Friend("Pierre")

  //  new Thread(() => sam.bow(pierre)).start() // Sam's lock, then Pierre's lock
  //  new Thread(() => pierre.bow(sam)).start() // Pierre's lock, then Sam's lock

  // 3 - livelock
  new Thread(() => sam.pass(pierre)).start()
  new Thread(() => pierre.pass(sam)).start()

}

