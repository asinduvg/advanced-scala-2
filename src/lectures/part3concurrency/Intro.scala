package lectures.part3concurrency

import java.util.concurrent.Executors

object Intro extends App {

  // JVM threads
  val runnable = new Runnable {
    override def run(): Unit = println("Running in parallel!")
  }
  val aThread = new Thread(runnable)

  //  aThread.start() // gives the signal to JVM to start a JVM thread
  // create a JVM thread => OS thread
  //  runnable.run() // doesn't do anything in parallel
  //  aThread.join() // blocks until aThread finishes running

  //  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("hello")))
  //  val threadGoodBye = new Thread(() => (1 to 5).foreach(_ => println("goodbye")))
  //  threadHello.run()
  //  threadGoodBye.run()
  // different runs produce different results

  // threads are expensive to start and kill
  // executors
  val pool = Executors.newFixedThreadPool(10)
  //  pool.execute(() => println("something in the thread pool"))

  pool.execute(() => {
    Thread.sleep(1000)
    //    println("done after 1 second")
  })

  pool.execute(() => {
    Thread.sleep(1000)
    //    println("almost done")
    Thread.sleep(1000)
    //    println("done after 2 seconds")
  })

  pool.shutdown()

  def runInParallel(): Unit = {
    var x = 0

    val thread1 = new Thread(() => {
      x = 1
    })

    val thread2 = new Thread(() => {
      x = 2
    })

    thread1.start()
    thread2.start()
    //    println(x)
  }

  //  for (_ <- 1 to 1000) runInParallel()

  class BankAccount(@volatile var amount: Int) {
    override def toString: String = " " + amount
  }

  def buy(account: BankAccount, thing: String, price: Int): Unit = {
    account.amount -= price
    //    println("I've bought " + thing)
    //    println("My account is now " + account)
  }

  for (_ <- 1 to 1000) {
    val account = new BankAccount(50000)
    val thread1 = new Thread(() => buy(account, "shoes", 3000)) // race condition
    val thread2 = new Thread(() => buy(account, "iPhone12", 4000)) // race condition (two threads trying to mutate onw value)

    thread1.start()
    thread2.start()
    Thread.sleep(10)
    //    if (account.amount != 43000) println("AHA " + account.amount)
  }

  // option #1: use synchronized
  def buySafe(account: BankAccount, thing: String, price: Int): Unit = {
    account.synchronized {
      // we can opt out () since it is one argument method
      account.amount -= price
      //      println("I've bought " + thing)
      //      println("My account is now " + account)
    }
  }

  // option #2: volatile

  /*
    Exercises
      1) Construct 50 "inception" threads(threads creating another threads)
          Thread1 -> Thread2 -> Thread3 -> ...
          println("Hello from thread #3)

          in REVERSE ORDER
   */

  /* 2) */
  //  var x = 0
  //  val threads = (1 to 100).map(_ => new Thread(() => x += 1))
  //  threads.foreach(_.start())
  /*
  i) What is the biggest value possible for x? 100
  ii) what is the smallest value possible for x? 1
   */

  /*
  3) sleep fallacy
   */
  //  var message = ""
  //  val awesomeThread = new Thread(() => {
  //    Thread.sleep(1000)
  //    message = "Scala is awesome"
  //  })
  //
  //  message = "Scala sucks"
  //  awesomeThread.start()
  //  Thread.sleep(2000)
  //  println(message)

  /*
  i) What's the value of the message? "Scala is awesome"
  ii) Is it guaranteed? NO!
  iii) Why? Why not?
   */


  // E 1
  def inceptionThreads(maxThreads: Int, index: Int = 1): Thread = new Thread(() => {
    if (index < maxThreads) {
      val aNewThread = inceptionThreads(maxThreads, index + 1)
      aNewThread.start()
      aNewThread.join()
    }
    println("Hello from thread #" + index)
  })

  inceptionThreads(50).start()

}
