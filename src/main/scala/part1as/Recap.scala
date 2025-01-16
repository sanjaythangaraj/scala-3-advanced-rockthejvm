package part1as

import scala.annotation.{tailrec, targetName}

object Recap {

  // values, types and expressions
  val aCondition = false
  val anIfExpression = if (aCondition) 42 else 43 // expressions evaluate to a value

  val aCodeBlock = {
    if (aCondition) {
      78
    }
  }

  // types: Int, String, Double, Boolean, Char, ...
  // Unit = () == "void" in other languages
  val theUnit = println("Hello")

  //functions
  def aFunction(x: Int): Int = x + 1

  // recursion: stack & tail
  @tailrec
  private def factorial(n: Int, acc: Int): Int =
    if (n < 0) acc
    else factorial(n - 1, n * acc)

  val fact10 = factorial(10, 1)

  // object oriented programming
  class Animal

  class Dog extends Animal

  val aDog: Animal = new Dog

  trait Carnivore {
    def eat(a: Animal): Unit

    @targetName("add")
    def +(a: Animal): Unit
  }

  class Crocodile extends Animal with Carnivore:
    override def eat(a: Animal): Unit = println("I am a crocodile, I am eating")

    @targetName("add")
    override def +(a: Animal): Unit = println("I can add")

  val aCroc = new Crocodile
  aCroc.eat(aDog) // method notation
  aCroc eat aDog // "operator"/infix position
  aCroc + aDog

  // anonymous classes
  val aCarnivore = new Carnivore:
    override def eat(a: Animal): Unit = println("I'm a carnivore")

    @targetName("add")
    override def +(a: Animal): Unit = println("I'm a Carnivore that can also add")

  // generics
  abstract class LList[A] {
    // type A is know inside the implementation
  }

  // singletons and companions
  object LList // companion object, used for instance-independent ("static") fields/methods

  // case class
  case class Person(name: String, age: Int)

  // enums
  enum BasicColors {
    case RED, GREEN, BLUE
  }

  // exceptions and try/catch/finally
  def throwSomeException(): Int =
    throw new RuntimeException()

  val aPotentialFailure = try {
    throwSomeException()
  } catch {
    case e: Exception => "I caught an expression"
  } finally {
    // closing resources
    println("some important logs")
  }

  // functional programming
  val incrementer = new Function[Int, Int] {
    override def apply(x: Int): Int = x + 1
  }

  val two = incrementer(1)

  // lambdas
  val anonymousIncrementer: Int => Int = x => x + 1

  // hofs = higher-order functions
  val anIncrementedList: List[Int] = List(1, 2, 3).map(anonymousIncrementer) // [2, 3, 4]
  
  // map, flatMap, filter
  
  val pairs = for {
    number <- List(1, 2, 3)
    char <- List('a', 'b', 'c')
  } yield s"$number-$char"
  
  // Scala collections: Seqs, Arrays, Lists, Vectors, Maps, Tuples, Sets
  
  // options, try
  val anOption: Option[Int] = Option(42)
  
  // pattern matching
  val x = 2
  val order = x match {
    case 1 => "first"
    case 2 => "second"
    case _ => "not important"
  }
    
  val bob = Person("bob", 22)
  
  val greeting = bob match {
    case Person(name, _) => s"Hi, my name is $name"
  }
  
  // brace-less syntax
  val pairs_v2 = 
    for
      number <- List(1, 2, 3)
      char <- List('a', 'b', 'c')
    yield s"$number-$char"
    
  val order_v2 = x match
    case 1 => "first"
    case 2 => "second"
    case _ => "not important"
    
  // indentation token
  class BracesLessAnimal extends Animal:
    def eat(): Unit =
      println("I am brace-less")
      println("I am eating")
    end eat    
  end BracesLessAnimal

  def main(args: Array[String]): Unit = {

  }
}
