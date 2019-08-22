package scala3workshop.nat

import scala.Predef.{any2stringadd => _}

//Required import to enable <, <=, > etc operators on a type `T: Ordering`
import scala.math.Ordering.Implicits._

import org.specs2.mutable._

import scala.language.implicitConversions

//requires Eql[Nat, Nat] instance to use == operator
import scala.language.strictEquality


class NatSpec extends Specification {

  "ValidCompileTime" ! {nat(6).toInt == 6}
  "Zero" ! {nat(0).toInt == 0 }
  "ValidRunTime" ! {6.toNat == Right(nat(6))}
  //"InvalidCompileTime" ! {nat(-6))}
  "InvalidRunTime" ! {-6.toNat == Left(-6)}

  "Equality" ! {nat(7) == nat(7)}
  "Ordering" ! {nat(0) < nat(1)}
  "Add" ! {nat(1) + nat(1) == nat(2)}
  "Multiply" ! {nat(1) * nat(2) == nat(2)}

}

