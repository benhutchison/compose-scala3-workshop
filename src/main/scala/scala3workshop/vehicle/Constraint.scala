package scala3workshop.vehicle

import scala.math.Ordering.Implicits._

import scala.language.implicitConversions

import cats.data.{Validated}
import cats.implicits._

import scala3workshop.nat._
import scala3workshop.pdecimal._
import scala3workshop.bound._


/** Models one or more constraints around the presence or absence values of type T. 
 * 
 * Use in the definition of vehcile categories.
 */
import BoundOps._

// //workaround compiler bugs
// object ConstraintOps {

//   def accepts[T](cons: Constraint[T], candidate: T): Boolean = cons 
// }

enum Constraint[T] {


  def accepts(candidate: T): Boolean = this match {
    case Is(value) => candidate == value
    case Any() => true
    case Not(cons) => !cons.accepts(candidate)
    case Or(c1, c2) => c1.accepts(candidate) || c2.accepts(candidate)
    case And(c1, c2) => c1.accepts(candidate) && c2.accepts(candidate)
    //power of GADTs ought to be on display here, but the type inferencer has lost the plot
    case ExistsWithin(bound, ord) => candidate.asInstanceOf[Option[T]] match {
      case Some(t) => bound.asInstanceOf[Bound[T]].accepts(t) given ord.asInstanceOf[Ordering[T]]
      case None => false
    }
  }

  //Satisifed if the value == `value`
  case Is(value: T) extends Constraint[T]
  //Satisfied by any value
  case Any[T]() extends Constraint[T]
  //Inverts a constraint
  case Not(constraint: Constraint[T]) extends Constraint[T]
  //satisified if either `c1` or `c2` are satisfied
  case Or(c1: Constraint[T], c2: Constraint[T]) extends Constraint[T]
  //satisified if both `c1` and `c2` are satisfied
  case And(c1: Constraint[T], c2: Constraint[T]) extends Constraint[T]
  //requires the value to exist and satisfy the Bound
  case ExistsWithin(bound: Bound[T], o: Ordering[T]) extends Constraint[Option[T]]
}

