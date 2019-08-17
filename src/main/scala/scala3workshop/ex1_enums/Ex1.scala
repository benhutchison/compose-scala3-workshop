package scala3workshop.ex1_enums

import scala.language.implicitConversions

import cats.data.{Validated}
import cats.implicits._

//One welcome change in Scala 3 is the ability to write top level definitions
//this make Scala 2 "package objects" unncessary and obselete

def topLevel = "This is a top level definition"
type AlsoTopLevel = (String, Int)
val AsAreVals = "..as are values"

//In Scala 2, most people used sealed traits to model enumerations

object Scala2 {
  sealed trait Amount
  case class Exact private (n: Int) extends Amount
  case class Range private (min: Int, max: Int) extends Amount
  case object Unspecified extends Amount
}


enum Amount {
  case Exact private[ex1_enums] (n: Int) extends Amount
  case Range private[ex1_enums] (min: Int, max: Int) extends Amount
  case Unspecified extends Amount
}
object Amount {
  def exact(n: Int): Either[String, Amount] = 
    Either.cond(n >= 0, Amount.Exact(n), s"Amount $n must be nonnegative. ")
      

  def range(min: Int, max: Int): Either[String, Amount] = (
    Validated.cond(min >= 0, min, s"Require min >= 0: min=$min. "),
    Validated.cond(max >= 0, max, s"Require max >= 0: max=$max. "),
    Validated.cond(min <= max, max, s"Require min <= max: min=$min, max=$max. "),
  ).mapN((min, max, _) => Amount.Range(min, max)).toEither
    
}

//follow the widespread convention of lower inclusive, upper exclusive bounds
//means that maxExclusive aligns with collection size
case class IndexOutOfBounds(index: Int, maxExclusive: Int, min: Int = 0)

object Excercises {
  //TODO: Define the VehicleType enum for 
  //Cars, Trucks, Bicycles, Motocycles, and Other
  //Each VehicleType should have a `wheels: Amount` parameter

  enum VehicleType(wheels: Amount) {
    case Car extends VehicleType(Amount.Exact(4))
    //TODO Your code here
  }

  //TODO use the `values` method to resolve the item corresponding to the index, or an error
  def vehicleType(index: Int): Either[IndexOutOfBounds, VehicleType] = ???
}