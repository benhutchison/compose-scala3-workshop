package scala3workshop.bound

import BoundOps._
import BoundaryType._

import scala.Predef.{any2stringadd => _}

//Required import to enable <, <=, > etc operators on a type `T: Ordering`
import scala.math.Ordering.Implicits._

import org.specs2.mutable._


import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}

import scala.language.implicitConversions

//requires Eql[Nat, Nat] instance to use == operator
import scala.language.strictEquality


object BoundSpec extends Specification {

  val i = BoundaryType.Inclusive
  val e = BoundaryType.Exclusive
  val genBoundaryType: Gen[BoundaryType] = Gen.oneOf(i, e)
  val genBoundary: Gen[Boundary[Double]] = for {
    value <- arbitrary[Double]
    typ <- genBoundaryType
  } yield Boundary(value, typ)

  val genSide: Gen[BoundSide] = Gen.oneOf(BoundSide.Lower, BoundSide.Upper)
  val genInterval: Gen[Bound[Double]] = for {
    b1 <- genBoundary
    b2 <- genBoundary
  } yield BoundOps.interval(b1.min(b2), b1.max(b2)).right.get

  val genHalfBound: Gen[Bound[Double]] = for {
    b <- genBoundary
    side <- genSide
  } yield Bound.HalfBound(b, side)

  val genExact: Gen[Bound[Double]] = for {
    value <- arbitrary[Double]
  } yield Bound.Exact(value)

  val genBound: Gen[Bound[Double]] = Gen.frequency(
    (3, genInterval),
    (3, genHalfBound),
    (3, genExact),
    (1, Gen.const(Bound.Unbounded[Double]())),
    (1, Gen.const(Bound.Empty[Double]()))
  )

  given as Arbitrary[Bound[Double]] = Arbitrary(genBound)

  val pointBelowLo = 1.99
  val pointOnLo = 2.0
  val pointAboveLo = 2.01
  val pointBelowHi = 2.99
  val pointOnHi = 2.0
  val pointAboveHi = 2.01

  val inclusive2to3 = BoundOps.inclusive(pointOnLo, pointOnHi)
  val gte2 = BoundOps.gte(pointOnLo)
  val gt2 = BoundOps.gt(pointOnLo)
  val lt2 = BoundOps.lt(pointOnLo)
  val lte2 = BoundOps.lte(pointOnLo)

  // Unfortunately, Specs2 Autoexample operator `eg` uses a Scala2 macro and so unavailable in Dotty
  "inclusive2to3.accepts(pointBelowLo)" ! {inclusive2to3.accepts(pointBelowLo) must beFalse}
  "inclusive2to3.accepts(pointOnLo)" ! {inclusive2to3.accepts(pointOnLo) must beTrue}
  "inclusive2to3.accepts(pointAboveLo)" ! {inclusive2to3.accepts(pointAboveLo) must beTrue}
  "inclusive2to3.accepts(pointBelowHi)" ! {inclusive2to3.accepts(pointBelowHi) must beTrue}
  "inclusive2to3.accepts(pointOnHi)" ! {inclusive2to3.accepts(pointOnHi) must beTrue}
  "inclusive2to3.accepts(pointAboveHi)" ! {inclusive2to3.accepts(pointAboveHi) must beFalse}

  "lt2.accepts(pointBelowLo)" ! {lt2.accepts(pointBelowLo) must beTrue}
  "lt2.accepts(pointOnLo)" ! {lt2.accepts(pointOnLo) must beFalse}
  "lte2.accepts(pointBelowLo)" ! {lte2.accepts(pointBelowLo) must beTrue}
  "lte2.accepts(pointOnLo)" ! {lte2.accepts(pointOnLo) must beTrue}
  "lte2.accepts(pointAboveLo)" ! {lte2.accepts(pointAboveLo) must beFalse}

}

