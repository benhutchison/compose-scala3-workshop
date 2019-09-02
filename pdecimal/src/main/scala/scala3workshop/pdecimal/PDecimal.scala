package scala3workshop.pdecimal

import algebra.instances.int._

import cats.implicits._

import scala.quoted._

import scala.language.implicitConversions

import scala.compiletime._
import scala.quoted._
import scala.annotation._

export PDecimal.PDecimal
export PDecimal.toPDecimal
export PDecimal.toDecimal
// export PDecimal.+
// export PDecimal.*
// export PDecimal./


object PDecimal {

  opaque type PDecimal = BigDecimal

  def (n: BigDecimal) toPDecimal: Either[BigDecimal, PDecimal] = Either.cond(n >= 0, n, n)
  def (d: Double) toPDecimal: Either[Double, PDecimal] = {
    val n = BigDecimal(d)
    Either.cond(n >= java.math.BigDecimal.ZERO, n, d)
  }
  def (s: String) toPDecimal: Either[String, PDecimal] = for {
    n <- Either.catchOnly[NumberFormatException](BigDecimal(s)).leftMap(_.toString)
    d <- Either.cond(n >= java.math.BigDecimal.ZERO, n, s)
  } yield d

  def (n: PDecimal) toDecimal: BigDecimal = n

  given as Eql[PDecimal, PDecimal] = Eql.derived
  given [T] as Eql[Either[T, PDecimal], Either[T, PDecimal]] given (et: Eql[T, T]) = Eql.derived
  

  //TODO define an Ordering

  //TODO define a algebra.ring.Rig
  //TODO define a algebra.ring.MultiplicativeGroup
  //TODO define operators
}  

private def PDecimalImpl(s: String) given (qc: QuoteContext): Expr[PDecimal] = {
  PDecimal.toPDecimal(s) match {
    case Left(s) => qc.error(s"Invalid PDecimal: $s"); ???
    case Right(n) => '{PDecimal.toPDecimal(${s.toExpr}).right.get}
  }
}

inline def pdecimal(inline s: String): PDecimal = ${ PDecimalImpl(s) }

