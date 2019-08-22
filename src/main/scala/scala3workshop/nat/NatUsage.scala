package scala3workshop.ex1_opaquetypes


import scala.quoted._

import scala.language.implicitConversions

import scala3workshop.nat._

val n: Nat = nat(6)

// val n2: Nat = nat(-6)

val n3: Option[Nat] = 6.toNat 

val n4: Option[Nat] = -6.toNat 



