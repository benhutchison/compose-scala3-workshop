package scala3workshop.bound

import algebra.instances.int._

import cats._
import cats.syntax.either._

//Required import to enable <, <=, > etc operators on a type `T: Ordering`
import scala.math.Ordering.Implicits._

import scala.language.implicitConversions

import scala.compiletime._
import scala.quoted._
import scala.annotation._


enum Bound[T] {
  case Interval private[bound] (lo: Boundary[T], hi: Boundary[T] ) extends Bound[T]
  case HalfBound(b: Boundary[T], side: BoundSide) extends Bound[T]
  case Unbounded[T]() extends Bound[T]
  case Empty[T]() extends Bound[T]
}
object Bound {
  import BoundSide._

  def (bound: Bound[T]) accepts[T: Ordering](t: T) = bound match {
    case Interval(lo, hi) => Boundary.accepts(lo, t, Lower) && Boundary.accepts(hi, t, Upper) 
    case HalfBound(b, side) => Boundary.accepts(b, t, side)
    case Unbounded() => true
    case Empty() => false
  } 

    //there are two Monoid interpretations, based on the union or intersection
  //of Bounds. We use union here arbitrarily.
  given UnionMonoid[T] as Monoid[Bound[T]] given (a: Ordering[T]) {
    def empty = Bound.Empty()

    def combine(a: Bound[T], b: Bound[T]): Bound[T] = (a, b) match {
      case (Unbounded(), _) => Unbounded()
      case (_, Unbounded()) => Unbounded()

      case (Empty(), x) => x
      case (x, Empty()) => x

      case (HalfBound(lo, Lower), HalfBound(hi, Upper)) => Interval(lo, hi)
      case (HalfBound(hi, Upper), HalfBound(lo, Lower)) => Interval(lo, hi)

      case (HalfBound(l, Lower), HalfBound(lo, Lower)) => HalfBound(lo.min(l), Lower)
      case (HalfBound(h, Upper), HalfBound(hi, Upper)) => HalfBound(h.max(h), Upper)

      case (HalfBound(h, Upper), Interval(lo, hi)) => Interval(lo, hi.max(h))
      case (Interval(lo, hi), HalfBound(h, Upper)) => Interval(lo, hi.max(h))

      case (HalfBound(l, Lower), Interval(lo, hi)) => Interval(lo.min(l), hi)
      case (Interval(lo, hi), HalfBound(l, Lower)) => Interval(lo.min(l), hi)

      case (Interval(lo, hi), Interval(l, h)) => Interval(lo.min(l), hi.max(h))
    }
  }

  def interval[T: Ordering](lower: Boundary[T], upper: Boundary[T]): Either[String, Bound[T]] = 
    if (lower.value <= upper.value)
      Interval(lower, upper).asRight[String]
    else
      s"A closed interval requires lower <= upper: lower=$lower, upper=$upper".asLeft[Bound[T]]
 
  def inclusive[T: Ordering](a: T, b: T): Bound[T] = {
    val lohi = if (a <= b) (a, b) else (b, a)
    Interval(Boundary(lohi._1, BoundaryType.Inclusive), Boundary(lohi._2, BoundaryType.Inclusive))
  }

  //declares that comparing Bound with themselves is a supported operation
  //a.equals(b) is still used to do the check, the Eql trait has no members
  given [T] as Eql[Bound[T], Bound[T]] = Eql.derived

}

case class Boundary[T](value: T, typ: BoundaryType)
object Boundary {
  import BoundaryType._
  import BoundSide._

  def accepts[T: Ordering](b: Boundary[T], t: T, side: BoundSide) = (side, b.typ) match {
    case (Lower, Inclusive) => t >= b.value
    case (Lower, Exclusive) => t > b.value
    case (Upper, Inclusive) => t <= b.value
    case (Upper, Exclusive) => t < b.value
  }

  given [T] as Ordering[Boundary[T]] given Ordering[T] = 
    Ordering.by(b => (b.value, b.typ))


  //min and max are both the "most extreme" value, but in different directions 
  def min[T](x: Boundary[T], y: Boundary[T]) given (o: Ordering[T]) = 
    extremum(x, y) given o.reverse

  def max[T](x: Boundary[T], y: Boundary[T]) given (o: Ordering[T]) = 
    extremum(x, y) given o

  private def extremum[T](x: Boundary[T], y: Boundary[T]) given (extremumOrd: Ordering[T]): Boundary[T] =
    //when the boundary points are equally extreme, prefer one with an Inclusive BoundaryType
    (the[Ordering[Boundary[T]]] given Ordering.by(b => (b.value, b.typ))).max(x, y)

}
enum BoundaryType {
  case Exclusive /* open */, Inclusive /* closed */
}
object BoundaryType {
  //ordinal is provided for enums automatically, reflecting position in the list
  //in this case, it makes sense to treat Inclusive boundaries as being "bigger"
  //since they include one more point
  given as Ordering[BoundaryType] = Ordering.by(_.ordinal)
}

enum BoundSide {
  case Lower, Upper
}
