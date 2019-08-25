package scala3workshop.nat


import scala.quoted._

export Nat.Nat
export Nat.toNat

object Nat {
  opaque type Nat = Int

  val Zero: Nat = 0
  val One: Nat = 1

  /** Use toNat to construct Nats at runtime */
  def (n: Int) toNat: Option[Nat] = 
    if (n >= 0) Some(n) 
    else None

  given as Ordering[Nat] given (a: Ordering[Int]) = a
  given as Liftable[Nat] given (a: Liftable[Int]) = a
}  

inline def nat(inline n: Int): Nat =
  ${ natImpl(n) }

def natImpl(n: Int) given (qc: QuoteContext): Expr[Nat] = {
  n.toNat match {
    case None => qc.error(s"Invalid Nat (require >= 0): $n"); '{Nat.Zero}
    case Some(nat) => nat.toExpr
  }
}

inline def natWhiteBox(n: Int) <: Any =
  ${ natWhiteboxImpl('n) }

def natWhiteboxImpl(n: Expr[Int]) given (qc: QuoteContext) : Expr[Any] = {
  import qc.tasty._

  n.unseal match {
    case Inlined(_, _, Literal(Constant(lit: Int))) if lit >= 0 => '{ $n.toNat.get }
    case term => term.seal
  }
}