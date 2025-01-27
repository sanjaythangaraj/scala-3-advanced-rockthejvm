package practice

import scala.annotation.tailrec

abstract class FSet[A] extends (A => Boolean) {
  def contains(elem: A): Boolean
  override def apply(elem: A): Boolean = contains(elem)

  infix def +(elem: A): FSet[A]
  infix def ++(anotherSet: FSet[A]): FSet[A]

  def map[B](f: A => B): FSet[B]
  def flatMap[B](f: A => FSet[B]): FSet[B]
  def filter(predicate: A => Boolean): FSet[A]
  def foreach(f: A => Unit): Unit

  infix def -(elem: A): FSet[A]
  infix def --(anotherSet: FSet[A]): FSet[A]
  infix def &(anotherSet: FSet[A]): FSet[A]

  def unary_! : FSet[A] = new PBSet(x => !contains(x))
}

class PBSet[A](property: A => Boolean) extends FSet[A] {

  override def contains(elem: A): Boolean = property(elem)

  override infix def +(elem: A): FSet[A] = new PBSet(x => x == elem || property(x))

  override infix def ++(anotherSet: FSet[A]): FSet[A] = new PBSet(x => property(x) || anotherSet(x))

  override def map[B](f: A => B): FSet[B] = politelyFail()

  override def flatMap[B](f: A => FSet[B]): FSet[B] = politelyFail()

  override def filter(predicate: A => Boolean): FSet[A] = new PBSet(x => property(x) && predicate(x))

  override def foreach(f: A => Unit): Unit = politelyFail()

  override infix def -(elem: A): FSet[A] = filter(x => x != elem)

  override infix def --(anotherSet: FSet[A]): FSet[A] = filter(!anotherSet)

  override infix def &(anotherSet: FSet[A]): FSet[A] = filter(anotherSet)

  private def politelyFail() = throw new RuntimeException("I don't know if this set is iterable...")
}

case class Empty[A]() extends FSet[A] {

  override def contains(elem: A): Boolean = false

  override infix def +(elem: A): FSet[A] = Cons(elem, this)

  override infix def ++(anotherSet: FSet[A]): FSet[A] = anotherSet

  override def map[B](f: A => B): FSet[B] = Empty()

  override def flatMap[B](f: A => FSet[B]): FSet[B] = Empty()

  override def filter(predicate: A => Boolean): FSet[A] = this

  override def foreach(f: A => Unit): Unit = ()

  override infix def -(elem: A): FSet[A] = this

  override infix def --(anotherSet: FSet[A]): FSet[A] = this

  override infix def &(anotherSet: FSet[A]): FSet[A] = this
}

case class Cons[A](head: A, tail: FSet[A]) extends FSet[A] {

  override def contains(elem: A): Boolean = elem == head || tail.contains(elem)

  override infix def +(elem: A): FSet[A] =
    if (contains(elem)) this
    else Cons(elem, this)

  override infix def ++(anotherSet: FSet[A]): FSet[A] = tail ++ anotherSet + head

  override def map[B](f: A => B): FSet[B] = tail.map(f) + f(head)

  override def flatMap[B](f: A => FSet[B]): FSet[B] = tail.flatMap(f) ++ f(head)

  override def filter(predicate: A => Boolean): FSet[A] = {
    val filteredTail = tail.filter(predicate)
    if predicate(head) then filteredTail + head
    else filteredTail
  }

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  override infix def -(elem: A): FSet[A] =
    if (head == elem) tail
    else tail - elem + head

  override infix def --(anotherSet: FSet[A]): FSet[A] = filter(x => !anotherSet(x))

  override infix def &(anotherSet: FSet[A]): FSet[A] = filter(anotherSet)

}

object FSet {
  def apply[A](values: A*): FSet[A] = {
    @tailrec
    def buildSet(valuesSeq: Seq[A], acc: FSet[A]): FSet[A] =
      if (valuesSeq.isEmpty) acc
      else buildSet(valuesSeq.tail, acc + valuesSeq.head)

    buildSet(values,Empty())
  }
}

object FunctionalSetPlayground {
  def main(args: Array[String]): Unit = {
    val first5 = FSet(1, 2, 3, 4, 5)
    val someNumbers = FSet(4, 5, 6, 7, 8)
    println(first5.contains(5)) // true
    println(first5(6)) // false
    println((first5 + 10).contains(10)) // true
    println(first5.map(_ * 2).contains(10)) // true
    println(first5.map(_ % 2).contains(1)) // true
    println(first5.flatMap(x => FSet(x, x+1)).contains(7)) // false

    println((first5 - 3).contains(3)) // false
    println((first5 -- someNumbers).contains(4)) // false
    println((first5 & someNumbers).contains(4)) // true

    val integers = new PBSet[Int](- => true)
    println(integers.contains(5234)) // true
    println(!integers.contains(0)) // false
    println((!integers + 1 + 2 + 3).contains(2)) // true

  }
}
