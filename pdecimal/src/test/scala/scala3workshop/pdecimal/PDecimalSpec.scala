
package scala3workshop.pdecimal

//Hide this deprecated implicit conversion because it disrupts the `+` operator
//Its going away in Dotty eventually by itself
import scala.Predef.{any2stringadd => _}

import cats.implicits._

//Required import to enable <, <=, > etc operators on a type `T: Ordering`
import scala.math.Ordering.Implicits._

import org.specs2.mutable._

import scala.language.implicitConversions

//requires Eql[Nat, Nat] instance to use == operator
import scala.language.strictEquality


class PDecimalSpec extends Specification {

  "ValidCompileTime" ! {pdecimal("6.6666666666666666666666667").toDecimal == BigDecimal("6.6666666666666666666666667")}
  
  //Why `compareTo` not `==`? 
  //because == on BigDecimal checks internal details that can differ for same quantity 
  "Zero" ! {pdecimal("0").toDecimal.compareTo(java.math.BigDecimal.ZERO) == 0}
  
  "ValidRunTime" ! {"6.6".toPDecimal == pdecimal("6.6").asRight[String]}
  //def testInvalidCompileTime: Unit = {pdecimal("foo"}
  "InvalidRunTime" ! {"-6".toPDecimal == "-6".asLeft[PDecimal]}

  "Equality" ! {pdecimal("7") === pdecimal("7")}
  "Ordering" ! {pdecimal("1") < pdecimal("1.00000000000000000001")}
  "Add" ! { pdecimal("1.1") + pdecimal("1") == pdecimal("2.1")}
  "Multiply" ! {pdecimal("1.1") * pdecimal("2") == pdecimal("2.2")}
  "Divide" ! {(pdecimal("5") / pdecimal("2")) == pdecimal("2.5")}

}

