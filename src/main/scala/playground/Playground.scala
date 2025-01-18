package playground

object Playground {
  def main(args: Array[String]): Unit = {
    println("Hello World")

    case class Person(name: String, age: Int, hobbies: List[String])
    case class Address(city: String, country: String, residents: List[String])

    val people = List(
      Person("Alice", 30, List("reading", "hiking")),
      Person("Bob", 25, List("painting", "cycling")),
      Person("Charlie", 35, List("gaming", "cooking"))
    )

    val addresses = List(
      Address("New York", "USA", List("Alice", "Charlie")),
      Address("Paris", "France", List("Bob")),
      Address("Tokyo", "Japan", List("Alice", "Bob"))
    )

    val complexPairs = for {
      person <- people
      address <- addresses
      if address.residents.contains(person.name) // Filter by residents
      hobby <- person.hobbies
      if hobby.startsWith("c") // Filter hobbies that start with 'c'
    } yield (person.name, hobby, address.city, address.country)

    complexPairs.foreach(println)

  }
}
