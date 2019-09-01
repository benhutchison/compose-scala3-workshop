package scala3workshop.nat

import scala.quoted._
import scala.quoted.matching.Const
import scala.annotation._

export Nat.Nat
export Nat.toNat
export Nat.toInt
export Nat.+
export Nat.*

object Nat {
  opaque type Nat = Int

  val Zero: Nat = 0
  val One: Nat = 1

  /** Use toNat to construct Nats at runtime */
  def (n: Int) toNat: Either[Int, Nat] = Either.cond(n >= 0, n, n)

  def (n: Nat) toInt: Int = n

  given as Ordering[Nat] given (a: Ordering[Int]) = a
  given as Liftable[Nat] given (a: Liftable[Int]) = a

  given as Eql[Nat, Nat] = Eql.derived
  given [T] as Eql[Either[T, Nat], Either[T, Nat]] given (et: Eql[T, T]) = Eql.derived

  given as algebra.ring.Rig[Nat] = algebra.instances.int.intAlgebra
  @alpha("plus") def (n: Nat) + (m: Nat) given (r: algebra.ring.Rig[Nat]) = r.plus(n, m)
  @alpha("times") def (n: Nat) * (m: Nat) given (r: algebra.ring.Rig[Nat]) = r.times(n, m)

}  

inline def nat(inline n: Int): Nat =
  ${ natImpl(n) }

def natImpl(n: Int) given (qc: QuoteContext): Expr[Nat] = {
  n.toNat match {
    case Left(_) => qc.error(s"Invalid Nat (require >= 0): $n"); '{Nat.Zero}
    case Right(nat) => nat.toExpr
  }
}

inline def natWhiteBox(n: Int) <: Any =
  ${ natWhiteboxImpl('n) }

def natWhiteboxImpl(n: Expr[Int]) given (qc: QuoteContext) : Expr[Any] = {
  n match {
    case Const(lit) if lit >= 0 => '{ $n.toNat.right.get }
    case term => term
  }
}