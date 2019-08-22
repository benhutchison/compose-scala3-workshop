package scala3workshop.nat

import scala.Predef.{any2stringadd => _}

//Required import to enable <, <=, > etc operators on a type `T: Ordering`
import scala.math.Ordering.Implicits._

import org.specs2.mutable._

import scala.language.implicitConversions

//requires Eql[Nat, Nat] instance to use == operator
import scala.language.strictEquality


class BoundSpec extends Specification {

  "ValidCompileTime" ! {nat(6).toInt == 6}


}

