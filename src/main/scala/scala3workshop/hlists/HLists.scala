package scala3workshop.hlists

import scala.compiletime.{S, constValue}

// Tuples are now represented as HLists!
//
// They have a recursive definition, which retains a different 
// static type for every element.

// sealed trait Tuple 
// trait NonEmptyTuple extends Tuple
// abstract class *:[+H, +T <: Tuple] extends NonEmptyTuple
// class Unit extends Tuple

// The *: operator joins a new element at the front of a tuple. 
// Unit () is the empty tuple.
//
// These two lines do the same thing:
//   val tuple: (String, Boolean, Int) = ("a", true, 1)
//   val tuple: (String, Boolean, Int) = "a" *: true *: 1 *: ()


object TupleTypes {
  // These definitions use recursive match types, which iterate over every type in the tuple
  // to tell us static facts about it.

  // For example: Unit returns the literal type '0', 
  // but a nonEmpty literal returns the successor S[_] of the Size of the tail.  
  type Size[X <: Tuple] <: Int = X match {
    case Unit => 0
    case x *: xs => S[Size[xs]]
  }

  // Type of the head of a tuple (not recursive)
  type Head[X <: NonEmptyTuple] // = ???

  // Type of the tail of a tuple (not recursive)
  type Tail[X <: NonEmptyTuple] <: Tuple // = ???

  // Type of the concatenation of two tuples
  type Concat[X <: Tuple, +Y <: Tuple] <: Tuple // = ???

  // Type of the element a position N in the tuple X
  type Elem[X <: Tuple, N <: Int] // = ???

  // Converts a tuple `(T1, ..., Tn)` to `(F[T1], ..., F[Tn])`
  // (Hard)
  type Map[Tup <: Tuple, F[_]] <: Tuple // = ???


}
