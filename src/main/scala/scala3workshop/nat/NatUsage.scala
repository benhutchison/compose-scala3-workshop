package scala3workshop.nat

import algebra.ring._
import mouse.all._

import scala.quoted._

import scala.language.implicitConversions


val n: Nat = nat(6)

// val n2: Nat = nat(-6)

val n3: Either[Int, Nat] = 6.toNat 

val n4: Either[Int, Nat] = -6.toNat 



