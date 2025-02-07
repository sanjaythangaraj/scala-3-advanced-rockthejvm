package part3async

import scala.collection.parallel.ParSeq
import scala.collection.parallel.CollectionConverters.*
import scala.collection.parallel.immutable.ParVector

object ParallelCollections {
  val aList = (1 to 1_000_000).toList
  val anIncrementedList = aList.map(_  + 1)
  val aParList: ParSeq[Int] = aList.par
  val aParIncrementedList = aParList.map(_ + 1)

  /*
    Applicable for
      - Seq
      - Vector
      - Arrays
      - Maps
      - Sets

    Use-case: faster-processing
   */

  // parallel collection built explicitly
  val  aParVector = ParVector[Int](1, 2, 3, 4, 5, 6)

  def measure[A](expression: => A): Long = {
    val time = System.currentTimeMillis()
    expression // forcing evaluation
    System.currentTimeMillis() - time
  }

  def compareListTransformation(): Unit = {
    val list = (1 to 30_000_000).toList
    println("list creation done")

    val serialTime = measure(list.map(_ + 1))
    println(s"serial time $serialTime")

    val parallelTime = measure(list.par.map(_ + 1))
    println(s"parallel time $parallelTime")

  }

  def demoUndefinedOrder(): Unit = {
    val aList = (1 to 1000).toList
    val reduction = aList.reduce(_ - _) // usually bad idea to use non-associative operators
    // [1, 2, 3].reduce(_ - _) = 1 - 2 - 3 = -4
    // [1, 2, 3].reduce(_ - _) = 1 - (2 - 3) = 2

    val parallelReduction = aList.par.reduce(_ - _) // order of operation is undefined
    println(s"Sequential Reduction: $reduction")
    println(s"Parallel Reduction: $parallelReduction")
  }

  def demoDefinedOrder(): Unit = {
    val strings = "I love parallel collections but i must be careful".split(" ").toList
    val concatenation = strings.reduce(_ + " " + _)
    val parallelConcatenation = strings.par.reduce(_ + " " + _)

    println(s"Sequential concatenation: $concatenation")
    println(s"Parallel concatenation: $parallelConcatenation")
  }

  def demoRaceConditions(): Unit = {
    var sum = 0
    (1 to 1000).toList.par.foreach(elem => sum += elem)
    println(s"sum: $sum")
  }

  def main(args: Array[String]): Unit = {
    // compareListTransformation()
    // demoUndefinedOrder()
    // demoDefinedOrder()
    demoRaceConditions()
  }
}
