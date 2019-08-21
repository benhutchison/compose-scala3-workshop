package scala3workshop.nat

import scala.compiletime._
import scala.quoted._

export Nat.Nat
export Nat.toNat

object Nat {

  opaque type Nat = Int

  def (n: Int) toNat: Either[Int, Nat] = Either.cond(n >= 0, n, n)

  given as Ordering[Nat] given (a: Ordering[Int]) = a
  given as Liftable[Nat] given (a: Liftable[Int]) = a
}  

def natImpl(n: Int) given (qc: QuoteContext): Expr[Nat] = {
  Nat.toNat(n) match {
    case Left(int) => qc.error(s"Invalid Nat (require >= 0): $n"); ???
    case Right(nat) => nat.toExpr
  }
}

inline def nat(inline n: Int): Nat = ${ natImpl(n) }