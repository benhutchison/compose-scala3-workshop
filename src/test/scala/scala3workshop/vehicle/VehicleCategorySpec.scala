package scala3workshop.vehicle


import cats.implicits._

import org.specs2.mutable._

import scala.language.implicitConversions


import scala3workshop.nat._
import scala3workshop.pdecimal._
import scala3workshop.bound._


class VehicleCategorySpec extends Specification {

  "Bike" ! {
    Vehicle(
      wheels = nat(2),
      powerSource = PowerSource.Human(),
      maxPoweredSpeedKmh = nat(0),
      grossVehicleMassTonnes = pdecimal("0.012"),
      seatingPositions = nat(1),
      primaryPurpose = Purpose.Passenger(),
      steeringWheelPositionInVehicleLength = Option.empty,
      roadUse = RoadUse.OnRoadUse(),

    ).category must_== VehicleCategory.PedalCycle().asRight[CategorisationFail]}
  
  

}

