package part5ts

import scala.language.reflectiveCalls

object StructuralTypes {
  type SoundMaker = { // structural type
    def makeSound(): Unit
  }

  class Dog {
    def makeSound(): Unit = println("bark")
  }

  class Car {
    def makeSound(): Unit = println("vroom")
  }

  val dog: SoundMaker = new Dog
  val car: SoundMaker = new Car
  // compile time duck typing

  // type refinements
  abstract class Animal {
    def eat(): String
  }

  type WalkingAnimal = Animal { // refined type
    def walk(): Int
  }

  val cat: WalkingAnimal = new Animal {
    override def eat(): String = "eating"
    def walk(): Int= 1
  }

  // why: creating type-safe APIs for existing types following the same structure, but no connection to each other
  type JavaCloseable = java.io.Closeable
  class CustomCloseable {
    def close(): Unit = println("closing")
    def closeSilently() = println("closing without making a sound")
  }

  /*
    In Scala, when using a union type A | B, you can only call a method on the union if
    all members of the union declare that method via a common supertype.
    In your code, JavaCloseable (a Java interface) and CustomCloseable (a Scala class) don't share a common supertype
    that defines close(), even though both have a close() method.
    This leads to a compilation error because Scala doesn't perform structural typing by default
    (i.e., it doesn't automatically recognize methods with the same name/signature across unrelated types).
   */

//  def closeResource(closeable: JavaCloseable | CustomCloseable): Unit = {
//    closeable.close() // value close is not a member of JavaCloseable | CustomCloseable
//  }

  // solution: structural type
  type UnifiedCloseable = {
    def close(): Unit
  }

  def closeResource(closeable: UnifiedCloseable): Unit = closeable.close()

  val javaCloseable = new JavaCloseable {
    override def close(): Unit = println("closing java resource")
  }
  val customCloseable = new CustomCloseable

  def closeResource_v2(closeable: {def close(): Unit}): Unit = closeable.close()


  def main(args: Array[String]): Unit = {
    dog.makeSound() // through reflection (slow)
    car.makeSound()

    closeResource(javaCloseable)
    closeResource(customCloseable)
  }
}
