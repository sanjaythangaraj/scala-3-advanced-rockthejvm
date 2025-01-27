package part2afp

object PartialFunctions {
  val aFunction: Int => Int = x => x + 1

  val aFuzzyFunction: Int => Int = (x: Int) => {
    if (x == 1) 42
    else if (x == 2) 56
    else if (x == 5) 999
    else throw new RuntimeException("no suitable case possible")
  }

  val aFuzzyFunction_v2: Int => Int = (x: Int) => x match {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }

  // partial function
  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }

  val canCallOn37: Boolean = aPartialFunction.isDefinedAt(37)

  val liftedPF: Int => Option[Int] = aPartialFunction.lift

  val pfChain = aPartialFunction.orElse {
    case 45 => 86
  }

  // HOFs accepts PFs as arguments
  val aList = List(1, 2, 3, 4)
  val aChangedList = aList.map(x => x match {
    case 1 => 4
    case 2 => 3
    case 3 => 45
    case 4 => 67
    case _ => 0
  })

  val aChangedList_v2 = aList.map ({
    case 1 => 4
    case 2 => 3
    case 3 => 45
    case 4 => 67
    case _ => 0
  })

  val aChangedList_v3 = aList map {
    case 1 => 4
    case 2 => 3
    case 3 => 45
    case 4 => 67
    case _ => 0
  }

  case class Person(person: String, age: Int)
  val someKids = List(
    Person("Alice", 3),
    Person("Bobbie", 5),
    Person("Jane", 4)
  )

  val kidsGrowingUp: List[Person] = someKids map {
    case Person(name, age) => Person(name, age + 1)
  }

  def main(args: Array[String]): Unit = {
    println(aPartialFunction(1)) // 42
    println(liftedPF(5)) // Some(999)
    println(liftedPF(37)) // None

    println(pfChain(2)) // 56
    println(pfChain(45)) // 86
  }
}
