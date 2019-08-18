package scala3workshop.nat.macros

import mouse.all._

import cats.implicits._

import scala.quoted._

import scala.language.implicitConversions

import scala3workshop.nat._
import scala3workshop.nat.macros._

//Converts a String constant into a Nat, or emits a compiler error if it isnt a valid Nat 
inline def nat(inline expr: String): Nat =
  ${ natImpl(expr) }

private def natImpl(expr: String) given (ctx: quoted.QuoteContext) : Expr[Nat] = {
  val n = Either.catchOnly[NumberFormatException]((expr).toInt).leftMap(_.toString).flatMap(n => 
    Either.cond(n >= 0, n, s"$n is negative. ")).right.getOrElse {
      ctx.error(s"Invalid Nat (required Int >= 0): $expr"); ???}
  '{ toNatUnsafe( ${n.toExpr} )}
  }



