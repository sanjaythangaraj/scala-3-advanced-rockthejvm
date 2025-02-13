package part4context

object ImplicitConversions {

  import scala.language.implicitConversions

  case class Person(name: String) {
    def greet: String = s"Hi, I am $name, how are you?"
  }

  val daniel = Person("Daniel")
  val danielSaysHi = daniel.greet

  // special conversion instance
  given string2Person: Conversion[String, Person] = (x: String) => Person(x)

  val danielSaysHi_v2 = "Daniel".greet // Person("Daniel").greet(), automatically by the compiler

  def processPerson(person: Person): String = {
    if (person.name.startsWith("J")) "OK"
    else "NOT OK"
  }

  val isJaneOk = processPerson("Jane") // ok - compiler rewrites to processProcess(Person("Jane"))

  /*
    - auto box types
    - use multiple types for the same code interchangeably
   */

  def main(args: Array[String]): Unit = {

  }
}
