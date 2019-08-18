package scala3workshop.nat.macros

import mouse.all._

import cats.implicits._

import scala.quoted._

import scala.language.implicitConversions

import scala3workshop.nat._


//Converts a Int constant into a Nat, or emits a compiler error if negative
inline def nat(inline n: Int): Nat =
  ${ natImpl(n) }

private def natImpl(n: Int) given (ctx: QuoteContext) : Expr[Nat] =
  if (n < 0) {
    ctx.error(s"Invalid Nat (require >= 0): $n")
    ???
  } else '{ toNatUnsafe( ${n.toExpr} )}
  



