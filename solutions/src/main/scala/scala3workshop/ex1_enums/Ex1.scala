package scala3workshop.ex1_enums

import scala.language.implicitConversions

import cats.data.{Validated}
import cats.implicits._

object Solutions {
  //TODO: Define the VehicleType enum for 
  //Car, Truck, Bicycle, Motocycle, and Other
  //Each VehicleType should have a `wheels: Amount` parameter

  enum VehicleType(wheels: Amount) {
    case Car extends VehicleType(Amount.Exact(4))
    case Truck extends VehicleType(Amount.Range(6, 10))
    case Bicycle extends VehicleType(Amount.Exact(2))
    case Other extends VehicleType(Amount.Unspecified)
  }


  //TODO use the `values` method to resolve the item corresponding to the index, or an error
  def vehicleType(index: Int): Either[IndexOutOfBounds, VehicleType] = 
    //Note: lift allows Array[T] to be safely accessed returning an Option[T]
    VehicleType.values.lift(index).toRight(IndexOutOfBounds(index, VehicleType.values.size))

}