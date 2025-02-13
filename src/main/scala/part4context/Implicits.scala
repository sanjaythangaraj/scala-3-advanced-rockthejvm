package part4context

object Implicits {

  // the ability to pass arguments automatically (implicitly) by the compiler
  trait SemiGroup[A] {
    def combine(x: A, y: A): A
  }

  // implicit arg -> using clause
  // implicit val -> given declaration

  def combineAll[A](list: List[A])(implicit semigroup: SemiGroup[A]): A = list.reduce(semigroup.combine)

  implicit val intSemiGroup: SemiGroup[Int] = (x, y) => x + y

  val sumOf10 = combineAll((1 to 10).toList)

  // extension methods = implicit class
  implicit class MyRichInteger(number: Int) {
    def isEven = number % 2 == 0
  }

  val is10Even: Boolean = 10.isEven // new MyRichInteger(23).isEven

  case class Person(name: String) {
    def greet: String = s"Hi, my name is $name"
  }

  // implicit conversions - SUPER DANGEROUS
  implicit def string2Person(x: String): Person = Person(x)
  val danielSaysHi = "Daniel".greet // string2Person("Daniel").greet

  // implicit def => synthesize NEW implicit values
  implicit def semiGroupOfOption[A](implicit semiGroup: SemiGroup[A]): SemiGroup[Option[A]] = {
    (x: Option[A], y: Option[A]) => for {
      valueX <- x
      valueY <- y
    } yield semiGroup.combine(valueX, valueY)
  }

  // given semiGroupOfOption[A](using semiGroup[A]): SemiGroup[Option[A]] = ...

  // organizing implicits == organizing contextual abstractions
  // import package.* // also imports implicits

  /*
    Why implicits will be passed out:
      - the implicit keyword has many different meanings
      - conversions are easy to abuse
      - implicits are very hard to track while debugging (givens also not trivial, but they are explicitly)
   */

    /*
      Contextual abstractions:
        - given/using clauses
        - extension methods
        - explicitly declared implicit conversions
     */

  def main(args: Array[String]): Unit = {
    println(sumOf10)
    print(is10Even)
  }

}
