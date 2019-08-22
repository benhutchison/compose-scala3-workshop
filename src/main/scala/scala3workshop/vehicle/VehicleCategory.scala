package scala3workshop.enums

import scala.math.Ordering.Implicits._

import scala.language.implicitConversions

import cats.data.{Validated}
import cats.implicits._

import scala3workshop.nat._
import scala3workshop.pdecimal._
import scala3workshop.bound._


/** VehicleCategory encodes the categories defined under the Australian legislation 
https://www.legislation.gov.au/Details/F2012C00326
*/

//One welcome change in Scala 3 is the ability to write top level definitions
//this make Scala 2 "package objects" unncessary and obselete

enum VehicleCategory(wheels: Bound[Nat]) {
  case PedalCycle extends VehicleCategory(wheels = Bound.inclusive(nat(2), nat(2)))

}

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
  case Exact (n: Nat) extends Amount
  case Range private[enums] (min: Nat, max: Nat) extends Amount
  case Unspecified extends Amount
}
object Amount {    

  def range(min: Nat, max: Nat): Either[String, Amount] = 
    Either.cond(min <= max, Amount.Range(min, max), s"Require min <= max: min=$min, max=$max. ")   
}

//follow the widespread convention of lower inclusive, upper exclusive bounds
//means that maxExclusive aligns with collection size
case class IndexOutOfBounds(index: Int, maxExclusive: Int, min: Int = 0)

object Excercises {
  //TODO: Define the VehicleType enum for 
  //Cars, Trucks, Bicycles, Motocycles, and Other
  //Each VehicleType should have a `wheels: Amount` parameter

  enum VehicleType(wheels: Amount) {
    case Car extends VehicleType(Amount.Exact(nat(4)))
    //TODO Your code here
  }

  //TODO use the `values` method to resolve the item corresponding to the index, or an error
  def vehicleType(index: Int): Either[IndexOutOfBounds, VehicleType] = ???
}