package part5ts

import scala.util.{Failure, Try}

object HigherKindedTypes {

  class HigherKindedType[F[_]]

  class HigherKindedType2[F[_], G[_], A]

  val higherKindedTypeExample = new HigherKindedType[List]
  val higherKindedType2Example = new HigherKindedType2[List, Option, Int]

  // why: abstract libraries, e.g. Cats
  // example: Functor
  val aList = List(1, 2, 3)
  val anOption = Option(2)
  val aTry = Try(42)

  val anIncrementedList: List[Int] = aList.map(_ + 1) // List(2, 3, 4)
  val anIncrementedOption: Option[Int] = anOption.map(_ + 1) // Some(3)
  val anIncrementedTry: Try[Int] = aTry.map(_ + 1) // Success(43)

  // "duplicated" APIs
  def do10xList(list: List[Int]): List[Int] = list.map(_ * 10)

  def do10xOption(option: Option[Int]) = option.map(_ * 10)

  def do10xTry(theTry: Try[Int]): Try[Int] = theTry.map(_ * 10)

  // DRY principle

  // step 1: TC definition
  trait Functor[F[_]] {
    def map[A, B](fa: F[A])(f: A => B): F[B]
  }

  // step 2: TC instances
  given listFunctor: Functor[List] = new Functor[List] {
    override def map[A, B](list: List[A])(f: A => B): List[B] = list.map(f)
  }

  // step 3: "user-facing" API
  def do10x[F[_]](container: F[Int])(using functor: Functor[F]): F[Int] =
    functor.map(container)(_ * 10)

  // step 4: extension method
  extension [F[_], A](container: F[A])(using functor: Functor[F]) {
    def map[B](f: A => B): F[B] = functor.map(container)(f)
  }

  def do10x_v2[F[_] : Functor](container: F[Int]): F[Int] = {
    container.map(_ * 10) // map is an extension method
  }

  /**
   * Exercise: implement a new type class on the same structure as Functor
   * In the general API, must use for-comprehensions
   *
   */

  def combineList[A, B](listA: List[A], listB: List[B]): List[(A, B)] =
    for {
      a <- listA
      b <- listB
    } yield (a, b)

  def combineOption[A, B](optionA: Option[A], optionB: Option[B]): Option[(A, B)] =
    for {
      a <- optionA
      b <- optionB
    } yield (a, b)

  def combineTry[A, B](tryA: Try[A], tryB: Try[B]): Try[(A, B)] =
    for {
      a <- tryA
      b <- tryB
    } yield (a, b)

//  trait Functor2[F[_]] {
//    def combine[A, B](fa: F[A], fb: F[B]): F[(A, B)]
//  }
//
//  given listFunctor2: Functor2[List] = new Functor2[List] {
//    override def combine[A, B](listA: List[A], listB: List[B]): List[(A, B)] = for {
//      a <- listA
//      b <- listB
//    } yield (a, b)
//  }
//
//  given optionFunctor2: Functor2[Option] = new Functor2[Option] {
//    override def combine[A, B](optionA: Option[A], optionB: Option[B]): Option[(A, B)] = for {
//      a <- optionA
//      b <- optionB
//    } yield (a, b)
//  }
//
//  given tryFunctor2: Functor2[Try] = new Functor2[Try] {
//    override def combine[A, B](tryA: Try[A], tryB: Try[B]): Try[(A, B)] = for {
//      a <- tryA
//      b <- tryB
//    } yield (a, b)
//  }
//
//  def combine[F[_], A, B](fa: F[A], fb: F[B])(using functor: Functor2[F]) =
//    functor.combine(fa, fb)

  // 1 - TC definition
  trait Monad[F[_]] extends Functor[F] {
    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
  }

  // 2 - TC instances
  given listMonad: Monad[List] with {
    override def flatMap[A, B](list: List[A])(f: A => List[B]): List[B] = list.flatMap(f)
    override def map[A, B](list: List[A])(f: A => B): List[B] = list.map(f)
  }

  given optionMonad: Monad[Option] with {
    override def flatMap[A, B](option: Option[A])(f: A => Option[B]): Option[B] = option.flatMap(f)
    override def map[A, B](option: Option[A])(f: A => B): Option[B] = option.map(f)
  }

  given tryMonad: Monad[Try] with {
    override def flatMap[A, B](theTry: Try[A])(f: A => Try[B]): Try[B] = theTry.flatMap(f)
    override def map[A, B](theTry: Try[A])(f: A => B): Try[B] = theTry.map(f)
  }

  // 3. "user-facing" API
  def combine_v0[F[_], A, B](fa: F[A], fb: F[B])(using monad: Monad[F]): F[(A, B)] =
    monad.flatMap(fa)(a => monad.map(fb)(b => (a,b)))

  extension [F[_], A](container: F[A])(using monad: Monad[F]) {
    def flatMap[B](f: A => F[B]): F[B] = monad.flatMap(container)(f)
  }

  def combine[F[_] : Monad, A, B](fa: F[A], fb: F[B]): F[(A, B)] =
    for {
      a <- fa
      b <- fb
    } yield(a, b)

  def main(args: Array[String]): Unit = {
    println(do10x(List(1, 2, 3)))
    println(do10x_v2(List(1, 2, 3)))
    println(combine(List(1, 2, 3), List('a', 'b', 'c')))
    println(combine(Option(2), Option("three")))
    println(combine(Try("one"), Try(false)))
  }
}
