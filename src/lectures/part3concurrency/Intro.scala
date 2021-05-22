package lectures.part3concurrency

import java.util.concurrent.Executors

object Intro extends App {

  // JVM threads
  val runnable = new Runnable {
    override def run(): Unit = println("Running in parallel!")
  }
  val aThread = new Thread(runnable)

  aThread.start() // gives the signal to JVM to start a JVM thread
  // create a JVM thread => OS thread
  runnable.run() // doesn't do anything in parallel
  aThread.join() // blocks until aThread finishes running

  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("hello")))
  val threadGoodBye = new Thread(() => (1 to 5).foreach(_ => println("goodbye")))
//  threadHello.run()
//  threadGoodBye.run()
  // different runs produce different results

  // threads are expensive to start and kill
  // executors
  val pool = Executors.newFixedThreadPool(10)
  //  pool.execute(() => println("something in the thread pool"))

  pool.execute(() => {
    Thread.sleep(1000)
    println("done after 1 second")
  })

  pool.execute(() => {
    Thread.sleep(1000)
    println("almost done")
    Thread.sleep(1000)
    println("done after 2 seconds")
  })

  pool.shutdown()

}
