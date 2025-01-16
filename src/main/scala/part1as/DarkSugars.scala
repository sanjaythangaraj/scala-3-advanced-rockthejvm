package part1as

import scala.annotation.targetName
import scala.util.Try

object DarkSugars {

  // 1. sugar for methods with one argument

  def singleArgMethod(arg: Int): Int = arg + 1

  val aMethodCall = singleArgMethod({
    // long code
    42
  })

  val aMethodCall_v2 = singleArgMethod {
    // long code
    42
  }

  // example: Try, Future
  val aTryInstance = Try {
    throw new RuntimeException
  }

  // with hofs
  val anIncrementedList = List(1, 2, 3) map { x =>
    // long code
    x + 1
  }

  // 2. single abstract method pattern (since Scala 2.12)
  trait Action {
    def act(x: Int): Int
  }

  val anAction: Action = (x: Int) => x + 1

  // example: Runnable
  val aThread = new Thread(() => println("Hi, Scala, from another thread"))

  // 3. methods ending in a : are RIGHT-ASSOCIATIVE
  val aList = List(1, 2, 3)
  val aPrependedList = 0 :: aList // aList.::(0)
  val aBigList = 0 :: 1 :: 2 :: List(3,4) // List(3,4).::(2).::(1).::(0)

  class MyStream[T] {
    def -->:(value: T): MyStream[T] = this
  }

  val myStream = 1 -->: 2 -->: 3 -->: 4 -->: new MyStream[Int]

  // 4. multi-word identifiers
  class Talker(name: String) {
    def `and then said`(gossip: String) = println(s"$name said $gossip")
  }

  val daniel = new Talker("Daniel")
  val danielsStatement = daniel `and then said` "I love Scala"

  // example: HTTP libraries
  object `Content-Type` {
    val `application/json` = "application/JSON"
  }

  // 5. infix types
  @targetName("Arrow") // for more readable bytecode + java interop
  infix class -->[A, B]
  val compositeType: Int --> String = new -->[Int, String]

  // 6. update()
  val anArray = Array(1, 2, 3, 4)
  anArray.update(2, 45)
  anArray(2) = 45

  // 7. mutable fields
  class Mutable {
    private var internalMember: Int = 0
    def member = internalMember
    def member_=(value: Int): Unit = internalMember = value
  }

  val aMutableContainer = new Mutable
  aMutableContainer.member = 42 // aMutableContainer.member_=(42)

  // 8. variable arguments (varargs)
  def methodWithVarargs(args: Int*) = {
    // return the number of arguments supplied
    args.length
  }

  val callWithZeroArgs = methodWithVarargs()
  val callWithOneArgs = methodWithVarargs(78)
  val callwithTwoArgs = methodWithVarargs(12, 34)

  val aCollection = List(1, 2, 3, 4)
  val callWithDynamicArgs = methodWithVarargs(aCollection*)

  def main(args: Array[String]): Unit = {

  }
}
