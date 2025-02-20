package part5ts

object LiteralUnionIntersectionTypes {

  // 1 - literal types
  val aNum = 3
  val three: 3 = 3

  def passNumber(n: Int) = println(n)

  passNumber(45)
  passNumber(three) // ok, 3 <: Int

  def passStrict(n: 3) = println(n)
  passStrict(3)
  passStrict(three)
  // passStrict(45) // not ok

  // available for double, boolean, strings
  val pi: 3.14 = 3.14
  val truth: true = true
  val favLang: "Scala" = "Scala"

  // literal types can be used as type arguments (just like any other types)
  def doSomethingWithYourLife(meaning: Option[42]) = meaning.foreach(println)

  // 2 - union types

  val truthOr42: Boolean | Int = 43

  def ambivalentMethod(arg: String | Int): String = arg match {
    case _: String => "a string"
    case _: Int => "a number"
  } // PM complete

  val aNumber = ambivalentMethod(45)
  val aString = ambivalentMethod("Scala")

  // type inference chooses the LCA of the two types instead of the String | Int
  val stringOrInt = if (43 > 0) "a string" else 45
  val stringOrInt_v2: String | Int = if (43 > 0) "a string" else 45 // ok

  // union types + nulls
  type Maybe[T] = T | Null // not null

  def handleMaybe(someValue: Maybe[String]): Int =
    if (someValue != null) someValue.length // flow typing
    else 0

  type ErrorOrT[T] = T | String

//  def handleResource(arg: ErrorOrT[Int]): Unit =
//    if (arg != "error") println(arg + 1) // flow typing doesn't work here
//    else println("Error")

  // 3. intersection types
  class Animal
  trait Carnivore
  class Crocodile extends Animal with Carnivore
  val carnivoreAnimal: Animal & Carnivore = new Crocodile

  trait Camera {
    def takePhoto() = println("smile")
    def use() = println("snap")
  }

  trait Phone {
    def makePhoneCall() = println("calling...")
    def use() = println("ring")
  }

  def useSmartDevice(smartDevice: Camera & Phone): Unit = {
    smartDevice.takePhoto()
    smartDevice.makePhoneCall()
    smartDevice.use()
  }

  class SmartPhone extends Camera with Phone {
    override def use(): Unit = println("smart")
  }

  trait Vehicle {
    def use(): Unit
  }

  trait Helicopter extends Vehicle {
    override def use() = println("up we go!")
  }

  trait Car extends Vehicle {
    override def use() = println("on the land!")
  }

  class FlyingCar extends Car with Helicopter

  def useCarCopter(carCopter: Car & Helicopter) = {
    carCopter.use()
  }

  // intersection types + covariance
  trait HostConfig
  trait HostController {
    def get: Option[HostConfig]
  }

  trait PortConfig
  trait PortController {
    def get: Option[PortConfig]
  }

  def getConfigs(controller: HostController & PortController): Option[HostConfig & PortConfig] = controller.get

  def main(args: Array[String]): Unit = {
    useSmartDevice(new SmartPhone) // smart
    useCarCopter(new FlyingCar) // up we go!
  }
}
