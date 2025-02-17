package part5ts

import java.util

object Variance {
  def main(args: Array[String]): Unit = {
    class Animal
    class Dog(name: String) extends Animal

    /*
      variance question for List:
        If Dog extends Animal, then should a List[Dog] "extends" List[Animal]?

      // for List, YES - List is COVARIANT
     */
    val lassie = new Dog("Lassie")
    val hachi = new Dog("Hachi")
    val laika = new Dog("Laika")

    val anAnimal: Animal = lassie // ok, Dog <: Animal
    val myDogs: List[Animal] = List(lassie, hachi, laika)// ok- List is COVARIANT: a list of dogs is a list of animals

    // define covariant types
    class MyList[+A] // MyList is COVARIANT in A
    val aListOfAnimals: MyList[Animal] = new MyList[Dog]

    /*
     If the answer for the variance question is NO, then the type is INVARIANT
     */
    trait Semigroup[A] { // no marker = INVARIANT
      def combine(x: A, y: A): A
    }

    // java generics
    // val aJavaList: java.util.ArrayList[Animal] = new util.ArrayList[Dog] // type mismatch: java generics are all INVARIANT

    /*
     If the answer for the variance question is HELL NO, then the type is CONTRAVARIANT

      If Dog extends Animal, then should a Vet[Animal] "extends" Vet[Dog]?

      For Vet, YES - Vet is CONTRAVARIANT
     */

    trait Vet[-A] { // contravariant in A
      def heal(animal: A): Boolean
    }

    // if Dog <: Animal, then Vet[Animal] <: Vet[Dog]
    val myVet: Vet[Dog] = (animal: Animal) => true
    // if the vet can treat any Animal, then they can treat dog too

    /*
      Rule of thumb:
        - If your type PRODUCES or RETRIEVES a value (e.g. a list), then it should be COVARIANT
        - If your type ACT ON or CONSUMES a value (e.g. a vet), then it should be CONTRAVARIANT
        - otherwise, INVARIANT
     */

    /**
     * Exercises
     */
    // 1. which type should be invariant, covariant, contravariant
    class RandomGenerator[+A] // produces values: covariant
    class MyOption[+A] // similar to Scala Option
    class JSONSerializer[A] // consumes values: contravariant
    trait MyFunction[-A, +B] // similar to Function1[-A, +B]
    // Function1 takes in (consumes) value of type A and
    // returns (produces) value of type B.

    // 2. - add variance modifiers to this "library"
    abstract class LList[+A] {
      def head: A
      def tail: LList[A]
    }

    case object EmptyList extends LList[Nothing] {
      override def head: Nothing = ???
      override def tail: LList[Nothing] = ???
    }

    case class Cons[+A](override val head: A, override val tail: LList[A]) extends LList[A]

    val aList: LList[Int] = EmptyList // fine
    val anotherList: LList[String] = EmptyList // also fine
    // Nothing <: A, then LList[Nothing] <: LList[A]

    

  }
}
