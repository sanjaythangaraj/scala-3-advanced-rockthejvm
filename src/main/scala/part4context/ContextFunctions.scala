package part4context

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}

object ContextFunctions {

  val aList = List(2, 1, 3, 4)
  val sortedlist = aList.sorted

  // def can have using clauses
  def methodWithoutContextArguments(nonContextArg: Int)(nonContextArg2: String): String = s"$nonContextArg : $nonContextArg2"
  def methodWithContextArguments(nonContentArg: Int)(using contextArg2: String): String = s"$nonContentArg : $contextArg2"

  // eta-expansion
  val functionWithoutContextArguments = methodWithoutContextArguments
  // val func2 = methodWithContextArguments // doesn't compile

  // context function

  val functionWithContextArguments: Int => String ?=> String = methodWithContextArguments

  val someResult = functionWithContextArguments(2)(using "scala")

  /*
    Use Cases:
      - convert methods with using clauses to function values
      - HOF with function values taking given instances as arguments
      - requiring given instances at CALL SITES and not at DEFINITION SITES
   */

  // execution context here
  // val incrementAsync: Int => Future[Int] = x => Future(x + 1) // doesn't work without a given Execution Context in scope

  val incrementAsync: Int => ExecutionContext ?=> Future[Int] = x => Future(x + 1)


  def main(args: Array[String]): Unit = {
    println(someResult)
    given executionContext: ExecutionContext = ExecutionContext.global
    incrementAsync(3).foreach(println)
  }
}
