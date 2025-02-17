package part5ts

class VariancePositions {
  class Animal
  class Dog extends Animal
  class Cat extends Animal
  class Crocodile extends Animal

  // 1. type bounds
  class Cage[A <: Animal] // A must be a subtype of animal
   val cage = new Cage[Dog] // ok, Dog <: Animal
  // val aCage = new Cage[String] // not ok, String is not a subtype of Animal

  class WeirdContainer[A >: Animal] // A must be a supertype of animal

  // 2. variance positions

  // a.

  // class Vet[-T](val favouriteAnimal:T) // contravariant type T occurs in covariant position in type T of value favouriteAnimal

  /*
    let us assume,

      class Vet[-T](val favouriteAnimal:T)

    compiles.

    val garfield = new Cat
    val theVet: Vet[Animal] = new Vet[Animal](garfield)
    val aDogVet: Vet[Dog] = theVet
    val aDog: Dog = aDogVet.favouriteAnimal // must be a Dog - type conflict!

   */

  // b.

  // class MutableOption[+T](var contents: T) // covariant type T occurs in contravariant position in type T of value contents

  /*
    let us assume,

    class MutableOption[+T](var contents: T)

    compiles.

    val maybeAnimal: MutableOption[Animal] = new MutableOption[Dog](new Dog)
    maybeAnimal.contents = new Cat // type conflict!
   */

  // c.

  //  class MyList[+T] {
  //    def add(element: T): MyList[T] = ??? // covariant type T occurs in contravariant position in type T of parameter element
  //  }

  /*
    let us assume,

    def add(element: T): MyList[T] = ???

    compiles.

    val animals: MyList[Animal] = new MyList[Cat]
    val biggerListOfAnimals = animals.add(new Dog) // type conflict!
   */

    class Vet[-T] {
      def heal(animal: T): Boolean = true
    }

  // d.

  //  class Vet2[-T] {
  //    def rescueAnimal(): T = ??? // contravariant type T occurs in covariant position in type(): T of method rescueAnimal
  //  }

  /*
    val vet: Vet2[Animal] = () => new Cat
    val lassiesVet = Vet2[Dog] = vet // Vet2[Animal]
    val rescueDog: Dog = lassiesVet.rescueAnimal() // must return a Dog, returns a Cat - type conflict!
   */

  /**
   * 3 - solving variance positions problems
   */
  abstract class LList[+A] {
    def head: A
    def tail: LList[A]
    def add[B >: A](element: B): LList[A] // widen the type
  }

  // val animals: List[Cat] = list of cats
  // val newAnimals: List[Animal] = animals.add(new Dog)


  class Vehicle
  class Car extends Vehicle
  class SuperCar extends Car
  class RepairShop[-A <: Vehicle] {
    def repair[B <: A](vehicle: B): B = vehicle
  }

  val myRepairShop: RepairShop[Car] = new RepairShop[Vehicle]
  val myBeatupVW = new Car
  val freshCar: Car = myRepairShop.repair(myBeatupVW) // works, returns a car

  val damagedFerrari = new SuperCar
  val freshFerrari: SuperCar = myRepairShop.repair(damagedFerrari) // works, returns a Supercar


  def main(args: Array[String]): Unit = {

  }
}
