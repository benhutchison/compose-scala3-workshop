package scala3workshop.vehicle

import scala.math.Ordering.Implicits._

import scala.collection.immutable.ArraySeq

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

import Bound._, BoundOps._, Boundary._, Constraint._, PowerSource._, Purpose._, RoadUse._

def Unlimited[T] = Bound.Unbounded[T]()

enum PowerSource {

  case Human() extends PowerSource
  
  case Electric(powerWatts: Bound[Nat] = Unlimited) extends PowerSource
  
  case Piston(powerWatts: Bound[Nat] = Unlimited, cylinderSizeMl: Bound[Nat] = Unlimited) 
    extends PowerSource

}

enum Purpose {
  case Passenger()
  case Goods()
}
enum RoadUse {
  case OnRoadUse()
  case OffRoadUse()
}

case class Criteria(
  wheels: Bound[Nat] = Unlimited,
  powerSource: Constraint[PowerSource] = Any[PowerSource](),
  poweredSpeedKmh: Bound[Nat] = Unlimited,
  grossVehicleMassTonnes: Bound[PDecimal] = Unlimited,
  notBeing: Seq[VehicleCategory] = Seq.empty,
  seatingPositions: Bound[Nat] = Unlimited,
  primaryPurpose: Purpose = Passenger(),
  steeringWheelPositionInVehicleLength: Constraint[Option[PDecimal]] = Any(),
  roadUse: RoadUse = OnRoadUse(),
)

case class Vehicle(
  wheels: Nat,
  powerSource: PowerSource,
  maxPoweredSpeedKmh: Nat,
  grossVehicleMassTonnes: PDecimal,
  seatingPositions: Nat,
  primaryPurpose: Purpose,
  steeringWheelPositionInVehicleLength: Option[PDecimal],
  roadUse: RoadUse, 
) {

  def category: Either[CategorisationFail, VehicleCategory] =
    VehicleCategory.values.filter(_.accepts(this)) match {
      case Seq(cat) => cat.asRight
      case Seq() => CategorisationFail.NoCategory(this).asLeft
      case multipleCategories: Seq[VehicleCategory] => 
        CategorisationFail.MultipleCategories(this, multipleCategories).asLeft
    }

}

enum CategorisationFail {
  case NoCategory(v: Vehicle)
  case MultipleCategories(v: Vehicle, categories: Seq[VehicleCategory])
}


enum VehicleCategory(val name: String, val categoryCode: (Char, Char), val options: Criteria*) {

  def accepts(v: Vehicle): Boolean = this.options.exists(criteria => 
    criteria.wheels.accepts(v.wheels) &&
    criteria.powerSource.accepts(v.powerSource) &&
    criteria.poweredSpeedKmh.accepts(v.maxPoweredSpeedKmh) &&
    criteria.grossVehicleMassTonnes.accepts(v.grossVehicleMassTonnes) &&
    !criteria.notBeing.exists(otherCategory => otherCategory.accepts(v)) &&
    criteria.seatingPositions.accepts(v.seatingPositions) &&
    criteria.primaryPurpose == v.primaryPurpose &&
    criteria.steeringWheelPositionInVehicleLength.accepts(v.steeringWheelPositionInVehicleLength) &&
    criteria.roadUse == v.roadUse
  )

  case PedalCycle() extends VehicleCategory(
    name = "PEDAL CYCLE",
    categoryCode = ('A', 'A'),
    Criteria(
      wheels = inclusiveInterval(nat(2), nat(2)),
      powerSource = Constraint.Is(PowerSource.Human()))
  )

  case PowerAssistedPedalCycle() extends VehicleCategory(
    name = "POWER-ASSISTED PEDAL CYCLE",
    categoryCode = ('A', 'B'),
    Criteria(
      wheels = Exact(nat(2)),
      powerSource = And(Is(Human()), Or(Is(Electric(powerWatts = lte(nat(200)))), Is(Piston(powerWatts = lte(nat(200))))))),
    Criteria(
      wheels = Exact(nat(2)),
      powerSource = And(Is(Human()), Is(Electric(powerWatts = lte(nat(250))))),
      poweredSpeedKmh = lte(nat(25)))
  )

  case Moped2Wheel() extends VehicleCategory(
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

  case Motorcycle() extends VehicleCategory(
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

  case MotorTricycle() extends VehicleCategory(
    name = "MOTOR TRICYCLE",
    categoryCode = ('L', 'E'),
    Criteria(
      wheels = Exact(nat(3)),
      powerSource = Is(Piston(cylinderSizeMl = gt(nat(50)))),
      grossVehicleMassTonnes = lte(pdecimal("1.0")),
    ),
    Criteria(
      wheels = Exact(nat(3)),
      poweredSpeedKmh = gt(nat(50)),
      grossVehicleMassTonnes = lte(pdecimal("1.0"))),
  )

  
  case PassengerCar() extends VehicleCategory(
    name = "PASSENGER CAR",
    categoryCode = ('M', 'A'),
    Criteria(
      wheels = gte(nat(4)),
      seatingPositions = lte(nat(9)),
      notBeing = Seq(ForwardControlPassengerVehicle(), OffRoadPassengerVehicle()),
    )
  )

  case ForwardControlPassengerVehicle() extends VehicleCategory(
    name = "FORWARD-CONTROL PASSENGER VEHICLE",
    categoryCode = ('M', 'B'),
    Criteria(
      wheels = gte(nat(4)),
      seatingPositions = lte(nat(9)),
      steeringWheelPositionInVehicleLength = ExistsWithin(lte(pdecimal("0.25")), the[Ordering[PDecimal]]),
      notBeing = Seq(OffRoadPassengerVehicle()),
    )
  )

  case OffRoadPassengerVehicle() extends VehicleCategory(
    name = "FORWARD-CONTROL PASSENGER VEHICLE",
    categoryCode = ('M', 'C'),
    Criteria(
      wheels = gte(nat(4)),
      seatingPositions = lte(nat(9)),
      roadUse = OffRoadUse(),
    )
  )

  case LightGoodsVehicle() extends VehicleCategory(
    name = "LIGHT GOODS VEHICLE",
    categoryCode = ('N', 'A'),
    Criteria(
      wheels = gte(nat(4)),
      grossVehicleMassTonnes = lte(pdecimal("3.5")),
      primaryPurpose = Goods(),
    )
  )

  case MediumGoodsVehicle() extends VehicleCategory(
    name = "MEDIUM GOODS VEHICLE",
    categoryCode = ('N', 'B'),
    Criteria(
      wheels = gte(nat(4)),
      grossVehicleMassTonnes = interval(exclusive(pdecimal("3.5")), inclusive(pdecimal("12.0"))),
      primaryPurpose = Goods(),
    )
  )

  case HeavyGoodsVehicle() extends VehicleCategory(
    name = "HEAVY GOODS VEHICLE",
    categoryCode = ('N', 'C'),
    Criteria(
      wheels = gte(nat(4)),
      grossVehicleMassTonnes = gt(pdecimal("12.0")),
      primaryPurpose = Goods(),
    )
  )

}

object VehicleCategory {

  val values = IndexedSeq(
    PedalCycle(),
    PowerAssistedPedalCycle(),
    Moped2Wheel(),
    Motorcycle(),
    MotorTricycle(),
    PassengerCar(),
    ForwardControlPassengerVehicle(),
    OffRoadPassengerVehicle(),
    LightGoodsVehicle(),
    MediumGoodsVehicle(),
    HeavyGoodsVehicle()
  )
}

