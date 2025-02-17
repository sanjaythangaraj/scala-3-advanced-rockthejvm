package part5ts

object OpaqueTypes {

  object SocialNetwork {
    opaque type Name = String

    object Name {
      def apply(str: String): Name = str
    }

    extension (name: Name)
      def length: Int = name.length

    // inside, Name <-> String
    def addFriend(person1: Name, person2: Name): Boolean = person1.length == person2.length
  }

  // outside SocialNetwork, Name and String are NOT related
  import SocialNetwork.*
  // val name: Name = "Daniel" // doesn not compile

  // why: you don't need (or want) to have access to the entire String API for the Name type

  object Graphics {
    opaque type Color = Int
    opaque type ColorFilter <: Color = Int

    val Red: Color = 0xFF000000
    val Green: Color =0x00FF0000
    val Blue: Color = 0x0000FF00
    val halfTransparency: ColorFilter = 0x80
  }

  import Graphics.*
  case class OverlayFilter(c: Color)
  val fadeLayer: OverlayFilter = OverlayFilter(halfTransparency) // ColorFilter <: Color

  // how can we create instances of opaque types + how to access their APIs

  // 1. companion objects
  val aName: Name = Name("Daniel")
  val nameLength = aName.length // ok, because of extension method

  def main(args: Array[Name]): Unit = {

  }

}
