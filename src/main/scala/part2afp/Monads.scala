package part2afp

import scala.annotation.targetName

object Monads {

  def listStory(): Unit = {
    val aList = List(1, 2, 3)
    val listMultiply = for {
      x <- List(1, 2, 3)
      y <- List(4, 5, 6)
    } yield x * y

    val listMultiply_v2 = List(1, 2, 3).flatMap(x => List(4, 5, 6).map(y => x * y))

    val f = (x: Int) => List(x, x + 1)
    val g = (x: Int) => List(x, x * 2)
    val pure = (x: Int) => List(x)

    // property 1: left identity
    val leftIdentity = pure(42).flatMap(f) == f(42) // for every x, for every f

    // property 2: right identity
    val rightIdentity = aList.flatMap(pure) == aList // for every list

    // property 3: associativity
    /*
      List(1, 2, 3).flatMap(x => [x, x + 1]) = [1, 2, 2, 3, 3, 4]
      [1, 2, 2, 3, 3, 4].flatMap(x => [x, x * 2]) = [1, 2, 2, 4,  2, 4, 3, 6,  3, 6, 4, 8]

      [1, 2, 3].flatMap(f).flatMap(g) = [1, 2, 2, 4,  2, 4, 3 , 6,  3, 6, 4, 8]

      [1, 2, 2, 4] = f(1).flatMap(g)
      [2, 4, 3, 6] = f(2).flatMap(g)
      [3, 6, 4, 8] = f(3).flatMap(g)

      [1, 2, 2, 4,  2, 4, 3 , 6,  3, 6, 4, 8] = f(1).flatMap(g) ++ f(2).flatMap(g) ++ f(3).flatMap(g)
      [1,2, 3].flatMap(x => f(x).flatMap(g))

     */
    val associativity = aList.flatMap(f).flatMap(g) == aList.flatMap(x => f(x).flatMap(g)) // true
  }

  def optionStory(): Unit = {
    val anOption = Option(42)
    val optionString = for {
      lang <- Option("Scala")
      ver <- Option(3)
    } yield s"$lang-$ver"

    val optionString_v2 = Option("Scala").flatMap(lang => Option(3).map(ver => s"$lang-$ver"))

    val f = (x: Int) => Option(x + 1)
    val g = (x: Int) => Option(x * 2)
    val pure = (x: Int) => Option(x)

    // property 1: left identity
    val leftIdentity = pure(42).flatMap(f) == f(42) // for any x, for any f

    // property 2: right identity
    val rightIdentity = anOption.flatMap(pure) == anOption // for any Option

    // property 3: associativity
    /*
      anOption.flatMap(f).flatMap(g) = Option(42).flatMap(x => Option(x + 1)).flatMap(x => Option(x * 2))
      = Option(43).flatMap(x => Option(x * 2))
      = Option(86)

      anOption.flatMap(x => f(x).flatMap(g)) = Option(42).flatMap(x => Option(x + 1).flatMap(y => 2 * y))
      = Option(42).flatMap(x => Option(2 * x + 2))
      = Option(86)
     */
    val associativity = anOption.flatMap(f).flatMap(g) == anOption.flatMap(x => f(x).flatMap(g)) // for any Option, f and g
  }

  // MONADS - chain dependant computations

  // exercise: Is PossiblyMonad a MONAD? Yes!

  // interpretation: Any computation that might perform side effects

  case class PossiblyMonad[A](unsafeRun: () => A) {
    def map[B](f: A => B): PossiblyMonad[B] =
      PossiblyMonad[B](() => f(unsafeRun()))

    def flatMap[B](f: A => PossiblyMonad[B]): PossiblyMonad[B] =
      PossiblyMonad[B](() => f(unsafeRun()).unsafeRun())
  }

  object PossiblyMonad {
    @targetName("pure")
    def apply[A](value: => A): PossiblyMonad[A] =
      new PossiblyMonad(() => value)
  }

  def possiblyMonadStory(): Unit = {
    val aPossiblyMonad = PossiblyMonad(42)

    val f = (x: Int) => PossiblyMonad(x + 1)
    val g = (x: Int) => PossiblyMonad(x * 2)
    val pure = (x: Int) => PossiblyMonad(x)

    // prop 1: left-identity
    /*
      pure(42).flatMap(f) = PossiblyMonad( () => 42 ).flatMap( x => PossiblyMonad( () => x + 1 ) )
      = PossiblyMonad(() => PossiblyMonad( () => 42 + 1).unsafeRun())
      = PossiblyMonad(() => 43)

      f(42) = PossiblyMonad(() => 42 + 1)
      = PossiblyMonad(() => 43)
     */
    val leftIdentity = pure(42).flatMap(f) == f(42)

    // prop 2: right-identity
    /*
      aPossiblyMonad.flatMap(pure) = PossiblyMonad(() => 42).flatMap(x => PossiblyMonad(() => x))
      = PossiblyMonad(() => PossiblyMonad(() => 42).unsafeRun())
      = PossiblyMonad(() => 42)
      = aPossiblyMonad
     */
    val rightIdentity = aPossiblyMonad.flatMap(pure) == aPossiblyMonad


    // prop 3: associativity
    /*
      aPossiblyMonad.flatMap(f).flatMap(g)
      = PossiblyMonad(() => 42).flatMap(x => PossiblyMonad(() => x + 1)).flatMap(g)
      = PossiblyMonad(() => PossiblyMonad(() => 43).unsafeRun()).flatMap(g)
      = PossiblyMonad(() => 43).flatMap(g)
      = PossiblyMonad(() => 43).flatMap(x => PossiblyMonad(() => 2 * x))
      = PossiblyMonad(() => PossiblyMonad(() => 43 * 2).unsafeRun())
      = PossiblyMonad(() => 86)


      aPossiblyMonad.flatMap(x => f(x).flatMap(g))
      = PossiblyMonad(() => 42).flatMap(x => f(x).flatMap(g))
      = PossiblyMonad(() => (f(42).flatMap(g)).unsafeRun())
      = PossiblyMonad(() => (PossiblyMonad(() => 43).flatMap(g)).unsafeRun())
      = PossiblyMonad(() => (PossiblyMonad(() => 43).flatMap(x => PossiblyMonad(() => 2 * x))).unsafeRun())
      = PossiblyMonad(() => (PossiblyMonad(() => PossiblyMonad(() => 43 * 2).unsafeRun())).unsafeRun())
      = PossiblyMonad(() => PossiblyMonad(() => 86).unsafeRun())
      = PossiblyMonad(() => 86)
     */
    val associativity = aPossiblyMonad.flatMap(f).flatMap(g) == aPossiblyMonad.flatMap(x => f(x).flatMap(g))

    println(leftIdentity) // false
    println(rightIdentity) // false
    println(associativity) // false
    println(PossiblyMonad(42) == PossiblyMonad(42)) // false
    // ^^ false negative.

    // real tests: values produced + side effects ordering
    val leftIdentity_v2 = pure(42).flatMap(f).unsafeRun() == f(42).unsafeRun() // true
    val rightIdentity_v2 = aPossiblyMonad.flatMap(pure).unsafeRun() == aPossiblyMonad.unsafeRun() // true
    val associativity_v2 = aPossiblyMonad.flatMap(f).flatMap(g).unsafeRun() == aPossiblyMonad.flatMap(x => f(x).flatMap(g)).unsafeRun() // true

    println(leftIdentity_v2) // true
    println(rightIdentity_v2) // true
    println(associativity_v2) // true

    val fs = (x: Int) => PossiblyMonad {
      println("incrementing")
      x + 1
    }

    val gs = (x: Int) => PossiblyMonad {
      println("doubling")
      x * 2
    }

    val associativity_v3 = aPossiblyMonad.flatMap(fs).flatMap(gs).unsafeRun() == aPossiblyMonad.flatMap(x => fs(x).flatMap(gs)).unsafeRun() // true
    println(associativity_v3)
  }

  def possiblyMonadExample(): Unit = {
    val aPossiblyMonad = PossiblyMonad {
      println("printing my first possibly monad")
      // do some computation
      42
    }

    val anotherPossiblyMonad = PossiblyMonad {
      println("my second possibly monad")
      "Scala"
    }

    // val aResult = aPossiblyMonad.unsafeRun() // printing my first possibly monad
    // println(aResult) // 42

    val aForComprehension = for { // computations are DESCRIBED, not EXECUTED
      num <- aPossiblyMonad
      lang <- anotherPossiblyMonad
    } yield s"$num-$lang"
  }

  def main(args: Array[String]): Unit = {
    possiblyMonadStory()
    possiblyMonadExample()
  }

}
