package part4context

object ExtensionMethods {
  
  case class Person(name: String) {
    def greet: String = s"Hi, my name is $name, nice to meet you"
  }

  extension (string: String) {
    def greetAsPerson: String = Person(string).greet
  }

  val danielsGreeting = "daniel".greetAsPerson

  // generic extension methods
  extension [A](list: List[A]) {
    def ends: (A, A) = (list.head, list.last)
  }

  val aList = List(1, 2, 3, 4)
  val ends = aList.ends

  // reason 1: make APIs very expensive
  // reason 2: enhance CERTAIN types with new capabilities

  trait Combinator[A] {
    def combine(x: A, y: A): A
  }

  extension [A](list: List[A]) {
    def combineAll(using combinator: Combinator[A]): A =
      list.reduce(combinator.combine)
  }

  given intCombinator: Combinator[Int] = (x, y) => x + y

  val sum = aList.combineAll // 20

  val someStrings =List("I", "love", "Scala")
  // val stringsSum = someStrings.combineAll // does not compile - no given Combinator[String] in scope

  // grouping extensions

  object GroupedExtensions {
    extension [A](list: List[A]) {

      def combineAll(using combinator: Combinator[A]): A =
        list.reduce(combinator.combine)

      def ends: (A, A) = (list.head, list.last)
    }
  }

  // call extension methods directly

  val ends_v2 = ends(aList)

  /**
   * Exercises
   *
   * 1. Add an isPrime method to the Int type.
   *    You should be able to write 7.isPrime
   * 2. Add extensions to Tree:
   *    - map[B](f: A -> B): Tree[B]
   *    - forAll(predicate: Boolean): Boolean
   *    - sum - sum of all element of tree
   */

  // "library code" = cannot change
  sealed abstract class Tree[A]
  case class Leaf[A](value: A) extends Tree[A]
  case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]

  // 1
  extension (number: Int) {
    def isPrime: Boolean = {
      def isPrimeAux(potentialDivisor: Int): Boolean =
        if (potentialDivisor > number / 2) true
        else if (number % potentialDivisor == 0) false
        else isPrimeAux(potentialDivisor + 1)

      assert(number >= 0)
      if (number == 0 || number == 1) false
      else isPrimeAux(2)
    }
  }

  // 2

  extension [A](tree: Tree[A]) {
    def map[B](f: A => B): Tree[B] = tree match
      case Leaf(value) => Leaf(f(value))
      case Branch(left, right) => Branch(left.map(f), right.map(f))

    def forall(predicate: A => Boolean): Boolean = tree match
      case Leaf(value) => predicate(value)
      case Branch(left, right) => left.forall(predicate) && right.forall(predicate)

    def combineAll(using combinator: Combinator[A]): A = tree match
      case Leaf(value) => value
      case Branch(left, right) => combinator.combine(left.combineAll, right.combineAll)
  }

  extension (tree: Tree[Int]) {
    def sum: Int = tree match
      case Leaf(value) => value
      case Branch(left, right) => left.sum + right.sum
  }

  def main(args: Array[String]): Unit = {
    println(danielsGreeting)
    println(ends)

    println(2003.isPrime)

    val aTree: Tree[Int] = Branch(Branch(Leaf(2), Leaf(1)), Leaf(10))
    println(aTree.map(_ + 1)) // Branch(Branch(Leaf(3), Leaf(2)), Leaf(11))
    println(aTree.forall(_ % 2 == 0)) // false
    println(aTree.sum) // 13
    println(aTree.combineAll) // 13
  }
}
