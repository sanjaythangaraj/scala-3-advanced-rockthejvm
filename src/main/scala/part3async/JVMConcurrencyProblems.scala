package part3async

object JVMConcurrencyProblems {

  def runInParallel(): Unit = {
    var x = 0

    val thread1 = new Thread(() => x = 1)
    val thread2 = new Thread(() => x = 2)

    thread1.start()
    thread2.start()
    println(x) // race condition
  }

  case class BankAccount(var amount: Int)

  def buy(account: BankAccount, thing: String, price: Int): Unit = account.amount -= price

  def buySafe(account: BankAccount, thing: String, price: Int): Unit = account.synchronized { account.amount -= price }

  def demoBankingProblem(): Unit = {
    (1 to 20_000).foreach { _ =>
      val account = BankAccount(50_000)
      val thread1 = new Thread(() => buySafe(account, "shoes",  3000))
      val thread2 = new Thread(() => buySafe(account, "smartphone", 4000))

      thread1.start()
      thread2.start()

      thread1.join()
      thread2.join()

      if (account.amount != 43000) println(s"broke the bank: ${account.amount}")

    }
  }

  /**
    Exercises
    1 - create "inception threads"
      thread 1
        -> thread 2
            -> thread 3
                .....

      each thread prints "hello from thread $i"
      Print all messages IN REVERSE ORDER

    2. what's the max/min value of x

    3. "sleep fallacy": what is the value of message
   */

  // 1 - inception threads
  def inceptionThreads(maxThreads: Int, i: Int = 1): Thread =
    new Thread(() => {
      if (i < maxThreads) {
        val newThread = inceptionThreads(maxThreads, i +1)
        newThread.start()
        newThread.join()
      }
      println(s"hello from thread $i")
    })

  // 2
  /*
    max value = 100
    min value = 1
   */
  def minMaxX(): Unit = {
    var x = 0
    val threads = (1 to 100).map(_ => new Thread(() => x += 1))
    threads.foreach(_.start())
  }

  // 3
  /*
    almost always, message = "Scala is awesome"
    is it guaranteed? NO
    Obnoxious situation (possible):

      main thread:
        message = "Scala sucks"
        awesomeThread.start()
        sleep(1001) - yields execution
      awesome thread:
        sleep(1000) - yields execution
      OS gives the CPU to some important threads
      OS gives the CPU back the main thread (1.001 sec sleep finished)
      main thread:
        println(message) // "Scala sucks"
      awesome thread:
        message = "Scala is awesome"
   */
  def demoSleepFallacy(): Unit = {
    var message = ""
    val awesomeThread = new Thread (() => {
      Thread.sleep(1000)
      message = "Scala is awesome"
    })

    message = "Scala sucks"
    awesomeThread.start()
    Thread.sleep(1001)
    // solution: join the worker thread
    awesomeThread.join()
    println(message)
  }

  def main(args: Array[String]): Unit = {
    inceptionThreads(50).start()
    demoSleepFallacy()
  }
}
