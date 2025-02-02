package part3async

import java.util.concurrent.{ExecutorService, Executors}

object JVMConcurrencyIntro {

 def basicThreads(): Unit = {
   val runnable = new Runnable {
     println("waiting")
     Thread.sleep(2000)

     override def run(): Unit = println("running on some thread")
   }

   // threads on the JVM
   val aThread = new Thread(runnable)
   aThread.start() // will run the runnable on some JVM Thread
   // block until thread finishes
   aThread.join()

   println("main thread")
 }

  // order of execution is NOT guaranteed
 def orderOfExecution(): Unit = {
   val threadHello = new Thread(() => (1 to 100).foreach(_ => println("hello")))
   val threadGoodbye = new Thread(() => (1 to 100).foreach(_ => println("goodbye")))
   threadHello.start()
   threadGoodbye.start()
 }

  // executors
  def demoExecutors(): Unit = {
    val threadPool: ExecutorService = Executors.newFixedThreadPool(4)

    // submit a computation
    threadPool.execute(() => println("something in the thread pool"))

    threadPool.execute { () =>
      Thread.sleep(1000)
      println("done after one second")
    }

    threadPool.execute { () =>
      Thread.sleep(1000)
      println("almost done")
      Thread.sleep(1000)
      println("done after 2 seconds")

    }

    threadPool.shutdown()
  }

  def main(args: Array[String]): Unit = {
    demoExecutors()
  }

}
