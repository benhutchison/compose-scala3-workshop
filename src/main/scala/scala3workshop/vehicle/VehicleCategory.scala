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

enum Constraint[T] {
  case Any[T]() extends Constraint[T]
  case Is(value: T) extends Constraint[T]
  case Not(constraint: Constraint[T]) extends Constraint[T]
  case Or(c1: Constraint[T], c2: Constraint[T]) extends Constraint[T]
  case And(c1: Constraint[T], c2: Constraint[T]) extends Constraint[T]
}

def Unlimited[T] = Bound.Unbounded[T]()

enum PowerSource {

  case Human extends PowerSource
  
  case Electric(powerWatts: Bound[Nat] = Unlimited) extends PowerSource
  
  case Piston(powerWatts: Bound[Nat] = Unlimited, cylinderSizeMl: Bound[Nat] = Unlimited) 
    extends PowerSource

}

import Bound._, BoundOps._, Boundary._, Constraint._, PowerSource._, Purpose._, RoadUse._
enum VehicleCategory(name: String, categoryCode: (Char, Char), options: Criteria*) {
  

  case PedalCycle extends VehicleCategory(
    name = "PEDAL CYCLE",
    categoryCode = ('A', 'A'),
    Criteria(
      wheels = inclusiveInterval(nat(2), nat(2)),
      powerSource = Constraint.Is(PowerSource.Human))
  )

  case PowerAssistedPedalCycle extends VehicleCategory(
    name = "POWER-ASSISTED PEDAL CYCLE",
    categoryCode = ('A', 'B'),
    Criteria(
      wheels = Exact(nat(2)),
      powerSource = And(Is(Human), Or(Is(Electric(powerWatts = lte(nat(200)))), Is(Piston(powerWatts = lte(nat(200))))))),
    Criteria(
      wheels = Exact(nat(2)),
      powerSource = And(Is(Human), Is(Electric(powerWatts = lte(nat(250))))),
      poweredSpeedKmh = lte(nat(25)))
  )

  case Moped2Wheel extends VehicleCategory(
    name = "MOPED 2 wheels",
    categoryCode = ('L', 'A'),
    Criteria(
      wheels = Exact(nat(2)),
      powerSource = Is(Piston(cylinderSizeMl = lte(nat(50)))),
      poweredSpeedKmh = lte(nat(50))
    ),
    Criteria(
      wheels = Exact(nat(2)),
      powerSource = Is(Electric()),
      poweredSpeedKmh = lte(nat(50)))
  )

  case Motorcycle extends VehicleCategory(
    name = "MOTOR CYCLE",
    categoryCode = ('L', 'C'),
    Criteria(
      wheels = Exact(nat(2)),
      powerSource = Is(Piston(cylinderSizeMl = gt(nat(50)))),
    ),
    Criteria(
      wheels = Exact(nat(2)),
      poweredSpeedKmh = gt(nat(50))),
  )

  case MotorTricycle extends VehicleCategory(
    name = "MOTOR TRICYCLE",
    categoryCode = ('L', 'E'),
    Criteria(
      wheels = Exact(nat(3)),
      powerSource = Is(Piston(cylinderSizeMl = gt(nat(50)))),
      grossVehicleMassTonnes = lte(pdecimal("1.0")),
    ),
    Criteria(
      wheels = Exact(nat(2)),
      poweredSpeedKmh = gt(nat(50)),
      grossVehicleMassTonnes = lte(pdecimal("1.0"))),
  )

  
  case PassengerCar extends VehicleCategory(
    name = "PASSENGER CAR",
    categoryCode = ('M', 'A'),
    Criteria(
      wheels = gte(nat(4)),
      seatingPositions = lte(nat(9)),
      notBeing = Seq(ForwardControlPassengerVehicle, OffRoadPassengerVehicle),
    )
  )

  case ForwardControlPassengerVehicle extends VehicleCategory(
    name = "FORWARD-CONTROL PASSENGER VEHICLE",
    categoryCode = ('M', 'B'),
    Criteria(
      wheels = gte(nat(4)),
      seatingPositions = lte(nat(9)),
      steeringWheelPositionInVehicleLength = lte(pdecimal("0.25")),
      notBeing = Seq(OffRoadPassengerVehicle),
    )
  )

  case OffRoadPassengerVehicle extends VehicleCategory(
    name = "FORWARD-CONTROL PASSENGER VEHICLE",
    categoryCode = ('M', 'C'),
    Criteria(
      wheels = gte(nat(4)),
      seatingPositions = lte(nat(9)),
      roadUse = OffRoadUse,
    )
  )

  case LightGoodsVehicle extends VehicleCategory(
    name = "LIGHT GOODS VEHICLE",
    categoryCode = ('N', 'A'),
    Criteria(
      wheels = gte(nat(4)),
      grossVehicleMassTonnes = lte(pdecimal("3.5")),
      primaryPurpose = Goods,
    )
  )

  case MediumGoodsVehicle extends VehicleCategory(
    name = "MEDIUM GOODS VEHICLE",
    categoryCode = ('N', 'B'),
    Criteria(
      wheels = gte(nat(4)),
      grossVehicleMassTonnes = interval(exclusive(pdecimal("3.5")), inclusive(pdecimal("12.0"))),
      primaryPurpose = Goods,
    )
  )

  case HeavyGoodsVehicle extends VehicleCategory(
    name = "HEAVY GOODS VEHICLE",
    categoryCode = ('N', 'C'),
    Criteria(
      wheels = gte(nat(4)),
      grossVehicleMassTonnes = gt(pdecimal("12.0")),
      primaryPurpose = Goods,
    )
  )

}

enum Purpose {
  case Passenger, Goods
}
enum RoadUse {
  case OnRoadUse, OffRoadUse
}


case class Criteria(
  wheels: Bound[Nat] = Unlimited,
  powerSource: Constraint[PowerSource] = Any[PowerSource](),
  poweredSpeedKmh: Bound[Nat] = Unlimited,
  grossVehicleMassTonnes: Bound[PDecimal] = Unlimited,
  notBeing: Seq[VehicleCategory] = Seq.empty,
  seatingPositions: Bound[Nat] = Unlimited,
  primaryPurpose: Purpose = Passenger,
  steeringWheelPositionInVehicleLength: Bound[PDecimal] = Unlimited,
  roadUse: RoadUse = OnRoadUse,
)



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