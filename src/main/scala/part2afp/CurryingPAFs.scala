package part2afp

object CurryingPAFs {

  val superAdder: Int => Int => Int =
    x => y => x + y

  val add3: Int => Int = superAdder(3) // y => 3 + y
  val eight: Int = add3(5) // 8
  val eight_v2: Int = superAdder(3)(5) // 8

  // curried methods
  def curriedAdder(x: Int)(y: Int): Int =
    x + y

  // methods != function values

  // converting methods to functions
  val add4: Int => Int = curriedAdder(4) // eta-expansion
  val nine: Int = add4(5) // 9

  def increment(x: Int): Int = x + 1
  val aList: List[Int] = List(1, 2, 3)
  val anIncrementedList: List[Int] = aList.map(increment) // eta-expansion

  // underscores are powerful
  def concatenator(a: String, b: String, c: String): String = a + b + c

  // x => concatenator(a, x, c)
  val insertName: String => String = concatenator(
    "Hello, my name is ",
    _: String,
    ", I'm going to show you a nice Scala trick"
  )

  val danielsGreeting: String = insertName("Daniel")

  // (x, y) => concatenator(x, b, y)
  val fillInTheBlanks: (String, String) => String = concatenator(_: String, "Daniel", _: String)

  val danielsGreeting_v2: String = fillInTheBlanks("Hi ", ".How are you?")

  /**
   * Exercises
   * 1. create as many add7 definitions
   * 2.
   */

  val simpleAddFunction: (Int, Int) => Int = (x, y) => x + y
  def simpleAddMethod(x: Int, y: Int) = x + y
  def curriedMethod(x: Int)(y: Int) = x + y

  // 1 - obtain an add7 function: x => x + 7 out of these 3 definitions

  val add7: Int => Int = x => simpleAddFunction(x, 7)
  val add7_v2: Int => Int = x => simpleAddMethod(x, 7)
  val add7_v3: Int => Int = x => curriedMethod(7)(x)
  val add7_v4: Int => Int = curriedMethod(7)
  val add7_v5: Int => Int = simpleAddMethod(_: Int, 7)
  val add7_v6: Int => Int = simpleAddMethod.curried(7)

  // 2 - process a list of numbers and return their string representations under different formats
  val piWith2Dec = "%4.2f".format(Math.PI) // 3.14

  // step 1: create a curried formatting method with a formatting string and a value
  def curriedFormatter(fmt: String)(value: Double): String = fmt.format(value)

  // step 2: process a list of number with various formats
  val someDecimals: List[Double] = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)
  val formatedDecimals_v1: List[String] = someDecimals.map(curriedFormatter("%4.2f"))
  val formatedDecimals_v2: List[String] = someDecimals.map(curriedFormatter("%8.6f"))
  val formatedDecimals_v3: List[String] = someDecimals.map(curriedFormatter("%16.14f"))

  // methods vs functions + by-name vs 0-lambdas

  def byName(n: => Int) = n+1
  def byLambda(f: () => Int) = f() + 1

  def method: Int = 42
  def parenMethod(): Int = 42

  byName(23) // ok
  byName(method) // 43. eta-expanded? NO - method is INVOKED here
  byName(parenMethod()) // 43

  // byName(parenMethod) not ok

  // byLambda(23) // not ok
  // byLambda(method) // eta-expansion is NOT possible
  byLambda(parenMethod) // eta expansion is done

  def main(args: Array[String]): Unit = {
    println(danielsGreeting)
    println(danielsGreeting_v2)

    println(formatedDecimals_v1)
    println(formatedDecimals_v2)
    println(formatedDecimals_v3)

  }
}
