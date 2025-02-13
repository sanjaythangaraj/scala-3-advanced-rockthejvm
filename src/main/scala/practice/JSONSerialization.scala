package practice

import java.util.Date

object JSONSerialization {
  /*
    Users, Posts, feeds
    Serialize to JSON
   */

  case class User(name: String, age: Int, email: String)
  case class Post(content: String, createdAt: Date)
  case class Feed(user: User, posts: List[Post])

  /*
    1 - intermediate data: numbers, strings, lists, dates, objects
    2 - type class to convert data to intermediate data
    3 - serialize to JSON
   */

  sealed trait JSONValue {
    def stringify: String
  }

  final case class JSONString(value: String) extends JSONValue {
    override def stringify: String = "\"" + value + "\""
  }

  final case class JSONNumber(value: Int) extends JSONValue {
    override def stringify: String = value.toString
  }

  final case class JSONArray(values: List[JSONValue]) extends JSONValue {
    override def stringify: String = values.map(_.stringify).mkString("[", ",", "]")
  }

  final case class JSONObject(values: Map[String, JSONValue]) extends JSONValue {
    override def stringify: String = values.map {
      case (key, value) => "\"" + key + "\":" + value.stringify
    }.mkString("{", ",", "}")
  }

  /*
  {
    "name": "John",
    "age": 22,
    "friends": [...],
    "latestPost": {...}
    }
   */

  val data: JSONValue = JSONObject(Map(
    "user" -> JSONString("Daniel"),
    "posts" -> JSONArray(List(
      JSONString("Scala is awesome"),
      JSONNumber(42)
    ))
  ))

  trait JSONConverter[T] {
    def convert(value: T): JSONValue
  }

  given stringConverter: JSONConverter[String] = (value: String) => JSONString(value)
  given intConverter: JSONConverter[Int] = (value: Int) => JSONNumber(value)
  given dateConverter: JSONConverter[Date] = (value: Date) => JSONString(value.toString)
  given userConverter: JSONConverter[User] = (user: User) => JSONObject(Map(
    "name" -> JSONConverter[String].convert(user.name),
    "age" -> JSONConverter[Int].convert(user.age),
    "email" -> JSONConverter[String].convert(user.email)
  ))
  given postConverter: JSONConverter[Post] = (post: Post) => JSONObject(Map(
    "content" -> JSONConverter[String].convert(post.content),
    "createdAt" -> JSONConverter[Date].convert(post.createdAt)
  ))
  given feedConverter: JSONConverter[Feed] = (feed: Feed) => JSONObject(Map(
    "user" -> JSONConverter[User].convert(feed.user),
    "posts" -> JSONArray(feed.posts.map(JSONConverter[Post].convert(_)))
  ))

  object JSONConverter {
    def convert[T](value: T)(using converter: JSONConverter[T]): JSONValue = converter.convert(value)
    def apply[T](using instance: JSONConverter[T]): JSONConverter[T] = instance
  }

  // example
  val now = new Date(System.currentTimeMillis())
  val john = User("John", 34, "john@example.com")
  val feed = Feed(john, List(
    Post("Hello, I'm learning type classes", now),
    Post("I bought a new puppy", now)
  ))

  object JSONConverterSyntax {

    extension [T](value: T)(using jsonConverter: JSONConverter[T]) {
      def toJSONValue: JSONValue = jsonConverter.convert(value)
    }

    extension [T](value: T)(using jsonConverter: JSONConverter[T]) {
      def toJSONString: String = jsonConverter.convert(value).stringify
    }
  }

  def main(args: Array[String]): Unit = {
    println(data.stringify)
    println(JSONConverter.convert(feed).stringify)

    import JSONConverterSyntax.*
    println(john.toJSONString)
  }
}
