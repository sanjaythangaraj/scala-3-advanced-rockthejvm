package part2afp

object FunctionalCollections {

  // Set[A] extends A => Boolean
  val aSet: Set[String] = Set("I", "love", "Scala")
  val aSetContainsScala: Boolean = aSet("Scala") // true


  // Seq[A] extends PartialFunction[Int, A]
  val aSeq: Seq[Int] = Seq(1, 2, 3, 4)
  val anElement: Int = aSeq(2) // 3

  // val aNonExistingElement: Int = aSeq(100) // throws java.lang.IndexOutOfBoundsException
  val aNonExistingElementButPFChained: Any = aSeq.orElse {
    case 100 => "Out of Bounds"
  } (100)

  // Map[K, V] extends PartialFunction[K, V]
  val aPhonebook: Map[String, Int] = Map(
    "Alice" -> 123456,
    "Bob" -> 987654
  )

  val alicesPhone: Int = aPhonebook("Alice") // 123456
  // val danielsPhone: Int = aPhonebook("Daniel") // java.util.NoSuchElementException

  def main(args: Array[String]): Unit = {
    println(aSetContainsScala)
    println(aNonExistingElementButPFChained)

  }
}
