package scala3workshop.ex1_opaquetypes

import algebra.ring._
import mouse.all._

import scala.quoted._

import scala.language.implicitConversions

import scala3workshop.nat.Nat._
import scala3workshop.nat.macros._

val n: Nat = nat(6)

// val n2: Nat = nat(-6)

val n3: Either[Int, Nat] = 6.toNat 

val n4: Either[Int, Nat] = -6.toNat 



