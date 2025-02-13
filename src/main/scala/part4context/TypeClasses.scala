package part4context

object TypeClasses {

  /*
    Small library to serialize some data to a standard format (HTML)
   */

  // V1: the OO way
  trait HTMLWritable {
    def toHTML: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    override def toHTML: String = s"<div>$name ($age yo) <a href=$email/></div>"
  }

  val bob: User = User("Bob", 23, "bob@example.com")

  val bob2HTML = bob.toHTML
  // same for other data structures that we want to serialize

  /*
    Drawbacks:
      - only available for the types WE write
      - can only provide ONE implementation
   */

  // V2: pattern matching
  object HTMLSerializerPM {
    def serializeToHTML(value: Any) = value match {
      case User(name, age, email) => s"<div>$name ($age yo) <a href=$email/></div>"
      case _ => throw new IllegalArgumentException("data structure not supported")
    }
  }

  /*
    Drawbacks:
      - lost type safety
      - need to modify a SINGLE piece of code every time
      - still ONE implementation
   */

  // V3 - type class

  // part 1. type class definition
  trait HTMLSerializer[T] {
    def serialize(value: T): String
  }

  // part 2. type class instances for the supported types
  given userSerializer: HTMLSerializer[User] = (value: User) => {
    val User(name, age, email) = value
    s"<div>$name ($age yo) <a href=$email/></div>"
  }

  val bob2HTML_v2 = userSerializer.serialize(bob)


  /*
    Benefits:
      - can define serializers for other types OUTSIDE the "library"
      - multiple serializers for the same type
   */

  import java.util.Date
  given dateSerializer: HTMLSerializer[Date] = (date: Date) => s"<div>${date.toString}</div>"

  object SomeOtherSerializerFunctionality { // organize givens properly
    given partialUserSerializer: HTMLSerializer[User] = (user: User) => s"<div>${user.name}</div>"
  }

  // part3 - using the type class (user-facing API)
  object HTMLSerializer {
    def serialize[T](value: T)(using serializer: HTMLSerializer[T]): String = serializer.serialize(value)

    def apply[T](using serializer: HTMLSerializer[T]) = serializer
  }

  val bob2HTML_v3 = HTMLSerializer.serialize(bob)
  val bob2HTML_v4 = HTMLSerializer[User].serialize(bob)

  // part 4 - extension method
  object HTMLSyntax {
    extension [T](value: T) {
      def toHTML(using serializer: HTMLSerializer[T]): String = serializer.serialize(value)
    }
  }

  import HTMLSyntax.*

  val bob2HTML_v5 = bob.toHTML

  /*
    Benefits
      - extend functionality to new types
      - flexibility to add TC instances in a different place than the definition of the TC
      - choose implementation (by importing the right givens)
      - expressive (via extension methods)
   */

  def main(args: Array[String]): Unit = {
    println(bob2HTML)
    println(bob2HTML_v2)

    println(bob2HTML_v3)
    println(bob2HTML_v4)

    println(bob2HTML_v5)
  }
}
