package part4context

object Givens {

  // list sorting
  val aList = List(4, 2, 3, 1)
  val anOrderedList = aList.sorted

  given descendingOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
  val anInverseOrderedList = aList.sorted(descendingOrdering)

  // custom sorting
  case class Person(name: String, age: Int)
  val people = List(Person("Alice", 29), Person("Sarah",34), Person("Jim", 23))

  given personOrdering: Ordering[Person] = (x: Person, y: Person) => x.name.compareTo(y.name)

  val sortedPeople = people.sorted(personOrdering)

  object PersonAltSyntax {
    given personOrdering: Ordering[Person] with {
      override def compare(x: Person, y: Person): Int = x.name.compareTo(y.name)
    }
  }

  // using clauses
  trait Combinator[A] {
    def combine(x: A, y: A): A
  }

  def combineAll[A](list: List[A])(using combinator: Combinator[A]) =
    list.reduce(combinator.combine)

  /*
    combineAll(List(1, 2, 3, 4))
    combineAll(people)
   */

  given intCombinator: Combinator[Int] = (x, y) => x + y

  val firstSum: Int = combineAll(List(1, 2, 3, 4, 5))
  // val combineAllPeople: People = combineAll(people) // does not compile - no Combinator[Person] in scope

  // context bound

  def combineInGroupsOf3[A](list: List[A])(using combinator: Combinator[A]): List[A] =
    list.grouped(3).map(group => combineAll(group)).toList

  def combineInGroupsOf3_v2[A](list: List[A])(using Combinator[A]): List[A] =
    list.grouped(3).map(group => combineAll(group)).toList

  def combineInGroupsOf3_v3[A: Combinator](list: List[A]): List[A] =
    list.grouped(3).map(group => combineAll(group)).toList

  // synthesize new given instances based on existing ones
  given listOrdering(using intOrdering: Ordering[Int]): Ordering[List[Int]] =
    (x, y) => intOrdering.compare(x.sum, y.sum)

  val listOfLists: List[List[Int]] = List(List(1, 2), List(1, 1), List(3, 4, 5))
  val nestedListsOrdered: List[List[Int]]  = listOfLists.sorted

  // with generics
  given listOrderingBasedOnCombinator[A: Combinator](using ordering: Ordering[A]): Ordering[List[A]] =
    (x, y) => ordering.compare(combineAll(y), combineAll(x))

  // pass a regular value instead of a given
  val myCombinator: Combinator[Int] = (x, y) => x * y

  val listProduct: Int = combineAll(List(1, 2, 3, 4))(using myCombinator)

  /**
   * Exercises
   * 1 - create a given for ordering Option[A] if you can order A
   * 2 - create a summoning method that fetches the given value of your particular
   */

  // 1
  given optionOrdering[A](using ordering: Ordering[A]): Ordering[Option[A]] =
    (x, y) => (x, y) match {
      case (None, None) => 0
      case (None, _) => -1
      case (_, None) => 1
      case (Some(a), Some(b)) => ordering.compare(a, b)
    }

  val optionsList = List(Option(1), Option.empty[Int], Option(3), Option(-1000))
  val aSortedOptionsList = optionsList.sorted

  // 2
  object Summon {

    def fetchGivenValue[A](using theValue: A): A = theValue

    given optionOrdering_v2[A](using ordering: Ordering[A]): Ordering[Option[A]] =
      (x, y) => (x, y) match {
        case (None, None) => 0
        case (None, _) => -1
        case (_, None) => 1
        case (Some(a), Some(b)) => fetchGivenValue[Ordering[A]].compare(a, b)
      }

    given optionOrdering_v3[A](using ordering: Ordering[A]): Ordering[Option[A]] =
      (x, y) => (x, y) match {
        case (None, None) => 0
        case (None, _) => -1
        case (_, None) => 1
        case (Some(a), Some(b)) => summon[Ordering[A]].compare(a, b)
      }
  }


  def main(args: Array[String]): Unit = {
    println(anOrderedList) // [4, 3, 2, 1]
    println(anInverseOrderedList) // [4, 3, 2, 1]
    println(sortedPeople) // [Person(Alice,29), Person(Jim,23), Person(Sarah,34)]
    println(nestedListsOrdered) // [[3, 4, 5], [1, 2], [1, 1]]
    println(listProduct) // 24

    println(aSortedOptionsList) // [None, Some(3), Some(1), Some(-1000)]
  }
}
