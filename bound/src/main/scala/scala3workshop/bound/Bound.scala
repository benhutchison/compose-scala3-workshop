package scala3workshop.bound

import algebra.instances.int._

import cats._
import cats.syntax.either._

//Required import to enable <, <=, > etc operators on a type `T: Ordering`
import scala.math.Ordering.Implicits._

import scala.language.implicitConversions


/** A set or range of acceptable values in an ordered set of T values */
enum Bound[T] {
  /** Accepts all values between lo and hi */
  case Interval private[bound] (lo: Boundary[T], hi: Boundary[T] ) extends Bound[T]
  /** Accepts all values on `side` of boundary `b` */
  case HalfBound(b: Boundary[T], side: BoundSide) extends Bound[T]
  /** Accepts exactly `value` */
  case Exact(value: T) extends Bound[T]
  /** Accepts any value */
  case Unbounded[T]() extends Bound[T]
  /** Accepts no value */
  case Empty[T]() extends Bound[T]
}
object BoundOps {
  import Bound._, BoundSide._

  def (bound: Bound[T]) accepts[T: Ordering](t: T) = bound match {
    case Interval(lo, hi) => Boundary.accepts(lo, t, Lower()) && Boundary.accepts(hi, t, Upper()) 
    case HalfBound(b, side) => Boundary.accepts(b, t, side)
    case Exact(value) => t == value
    case Unbounded() => true
    case Empty() => false
  } 



  def interval[T: Ordering](lower: Boundary[T], upper: Boundary[T]): Either[String, Bound[T]] = 
    if (lower.value <= upper.value)
      Bound.Interval(lower, upper).asRight[String]
    else
      s"A closed interval requires lower <= upper: lower=$lower, upper=$upper".asLeft[Bound[T]]
 
  def inclusive[T: Ordering](a: T, b: T): Bound[T] = {
    val lohi = if (a <= b) (a, b) else (b, a)
      Bound.Interval(Boundary(lohi._1, BoundaryType.Inclusive()), Boundary(lohi._2, BoundaryType.Inclusive()))
  }

  def gt[T](value: T): Bound[T] = Bound.HalfBound(Boundary.exclusive(value), Lower())
  def gte[T](value: T): Bound[T]  = Bound.HalfBound(Boundary.inclusive(value), Lower())
  def lt[T](value: T): Bound[T]  = Bound.HalfBound(Boundary.exclusive(value), Upper())
  def lte[T](value: T): Bound[T]  = Bound.HalfBound(Boundary.inclusive(value), Upper())

  /** Convex hull or union of a pair of Bounds, being smallest single Bound that accepts all values
   * the pair accept.
  */
  def hull[T](a: Bound[T], b: Bound[T]) given Ordering[T]: Bound[T] = (a, b) match {
    case (Unbounded(), _) => Unbounded()
    case (_, Unbounded()) => Unbounded()

    case (Empty(), x) => x
    case (x, Empty()) => x

    case (HalfBound(_, Lower()), HalfBound(_, Upper())) => Unbounded()
    case (HalfBound(_, Upper()), HalfBound(_, Lower())) => Unbounded()

    case (HalfBound(l1, Lower()), HalfBound(l2, Lower())) => HalfBound(l1.min(l2), Lower())
    case (HalfBound(h1, Upper()), HalfBound(h2, Upper())) => HalfBound(h1.max(h2), Upper())

    case (HalfBound(h1, Upper()), Interval(l, h2)) => HalfBound(h1.max(h2), Upper())
    case (HalfBound(h1, Upper()), Exact(h2)) => HalfBound(h1.max(Boundary.inclusive(h2)), Upper())
    case (Interval(l, h1), HalfBound(h2, Upper())) => HalfBound(h1.max(h2), Upper())
    case (Exact(h1), HalfBound(h2, Upper())) => HalfBound(Boundary.inclusive(h1).max(h2), Upper())

    case (HalfBound(l1, Lower()), Interval(l2, h)) => HalfBound(l1.min(l2), Lower())
    case (HalfBound(l1, Lower()), Exact(l2)) => HalfBound(l1.min(Boundary.inclusive(l2)), Lower())
    case (Interval(l1, h), HalfBound(l2, Lower())) => HalfBound(l1.min(l2), Lower())
    case (Exact(l1), HalfBound(l2, Lower())) => HalfBound(Boundary.inclusive(l1).min(l2), Lower())

    case (Interval(lo, hi), Interval(l, h)) => Interval(lo.min(l), hi.max(h))
    case (Interval(lo, hi), Exact(v)) => {
      val b = Boundary.inclusive(v)
      Interval(lo.min(b), hi.max(b))
    }
    case (Exact(v), Interval(lo, hi)) => {
      val b = Boundary.inclusive(v)
      Interval(lo.min(b), hi.max(b))
    }
    case (Exact(v1), Exact(v2)) => Interval(Boundary.inclusive(v1.min(v2)), Boundary.inclusive(v1.max(v2)))
  }

  //min and max are both the "most extreme" value, but in different directions 
  def min[T](x: Boundary[T], y: Boundary[T]) given (o: Ordering[T]) = 
   extremum(x, y) given o.reverse

  def max[T](x: Boundary[T], y: Boundary[T]) given (o: Ordering[T]) = 
    extremum(x, y) given o

  private def extremum[T](x: Boundary[T], y: Boundary[T]) given (extremumOrd: Ordering[T]): Boundary[T] =
    //when the boundary points are equally extreme, prefer one with an () BoundaryType
    (the[Ordering[Boundary[T]]] given Ordering.by(b => (b.value, b.typ))).max(x, y)

  //there are two Monoid interpretations, based on the union or intersection
  //of Bounds. We use union here arbitrarily.
  given UnionMonoid[T] as Monoid[Bound[T]] given (a: Ordering[T]) {
    def empty = Bound.Empty()

    def combine(a: Bound[T], b: Bound[T]): Bound[T] = hull(a, b)
  }

  //declares that comparing Bound with themselves is a supported operation
  //a.equals(b) is still used to do the check, the Eql trait has no members
  given [T] as Eql[Bound[T], Bound[T]] = Eql.derived

}

/** A boundary point `value` and whether the boundary is included or excluded */
case class Boundary[T](value: T, typ: BoundaryType)
object Boundary {
  import BoundaryType._
  import BoundSide._

  def inclusive[T](value: T) = Boundary(value, Inclusive())
  def exclusive[T](value: T) = Boundary(value, Exclusive())

  def accepts[T: Ordering](b: Boundary[T], t: T, side: BoundSide) = (side, b.typ) match {
    case (Lower(), Inclusive()) => t >= b.value
    case (Lower(), Exclusive()) => t > b.value
    case (Upper(), Inclusive()) => t <= b.value
    case (Upper(), Exclusive()) => t < b.value
  }

  given [T] as Ordering[Boundary[T]] given Ordering[T] = 
    Ordering.by(b => (b.value, b.typ))

}

enum BoundaryType {
  case Exclusive() /* open */
  case Inclusive() /* closed */
}
object BoundaryType {
  //ordinal is provided for enums automatically, reflecting position in the list
  //in this case, it makes sense to treat Inclusive boundaries as being "bigger"
  //since they include one more point
  given as Ordering[BoundaryType] = Ordering.by(_.ordinal)
}

enum BoundSide {
  case Lower()
  case Upper()
}
