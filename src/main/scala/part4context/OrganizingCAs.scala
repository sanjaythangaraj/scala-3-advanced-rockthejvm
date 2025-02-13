package part4context

object OrganizingCAs {

  val aList = List(2, 3, 1, 4)
  val anOrderedList = aList.sorted

  // compiler fetches givens/extensions

  // 1. local scope
  given reverseOrdering: Ordering[Int] = (x, y) => y - x

  // 2. imported scope
  case class Person(name: String, age: Int)
  val persons = List(Person("Steve", 30), Person("Amy", 22), Person("John", 67))

  object PersonGivens {
    given ageOrdering: Ordering[Person] = (x, y) => Integer.compare(y.age, x.age)

    extension (p: Person) {
      def greet: String = s"Hi, I'm ${p.name}. Nice to meet you!"
    }
  }

  // a. import explicitly

  // import PersonGivens.ageOrdering

  // b. import a given for a particular type

  // import PersonGivens.given Ordering[Person]

  // c. import all givens

  // import PersonGivens.given

  // warning: import PersonGivens.* does NOT also import given instances!
  // import PersonGivens.*

  // 3. companions of all types involved in method signature
  /*
    - Ordering
    - List
    - Person
   */
  // def sorted[B >: A](using ord: Ordering[B]): List[B]

  object Person {
    given byNameOrdering: Ordering[Person] = (x, y) => x.name.compareTo(y.name)

    extension (p: Person) {
      def greet: String = s"Hello, I'm ${p.name}"
    }
  }

  val sortedPersons = persons.sorted

  /*
    Good practice tips:
      1. when you have a "given" (only ONE that makes sense) add it in the companion object of the type.
      2. when you have MANY possible givens, but ONE that is dominant (used most), add that in the companion and
         the rest in other objects.
      3. when you have MANY possible givens and NO ONE is dominant, add them in separate objects and import them
        explicitly
   */

    // same principles apply to extension methods as well.

  /**
   * Exercises - Create given instances for Ordering[Purchase]
   *  - ordering by total price, descending = 50% of code base
   *  - ordering by unit count, descending = 25% of code base
   *  - ordering by unit price, ascending = 25% of code base
   */
  case class Purchase(uUnits: Int, unitPrice: Double)

  object Purchase {
    given byTotalPriceOrdering: Ordering[Purchase] = (x, y) => java.lang.Double.compare(y.uUnits * y.unitPrice, x.uUnits * x.unitPrice)
  }

  object UnitCountPurchaseOrdering {
    given byUnitCountOrdering: Ordering[Purchase] = (x, y) => Integer.compare(y.uUnits, x.uUnits)
  }

  object UnitPricePurchaseOrdering {
    given byUnitPriceOrdering: Ordering[Purchase] = (x, y) => java.lang.Double.compare(x.unitPrice, y.unitPrice)
  }

  def main(args: Array[String]): Unit = {
    println(anOrderedList)
    println(sortedPersons)

    import PersonGivens.* // includes extension methods
    println(Person("daniel", 23).greet)
  }
}
