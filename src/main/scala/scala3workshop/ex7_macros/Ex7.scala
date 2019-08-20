package scala3workshop.ex7_macros

import scala.compiletime._
import scala.quoted._
import scala.tasty.Reflection


// To experiment with the code in this chapter, either run `console` in sbt and 
// scala> import scala3workshop.ex7_macros._
// or run your arbitrary code here with `run`, selecting `Ex7`.
object Ex7 {
  def main(args: Array[String]): Unit = {
    // Put whatever code you want here to play around with the stuff in this chapter

  }
}


inline def macroIf[A, B](inline cond: Boolean, ifTrue: => A, ifFalse: => B) <: Any = 
  ${ macroIfImpl(cond, 'ifTrue, 'ifFalse) }

def macroIfImpl[A: Type, B: Type](
    cond: Boolean, 
    ifTrue: Expr[A], 
    ifFalse: Expr[B]) given QuoteContext: Expr[Any] = {

  if (cond) ifTrue else ifFalse
}

inline def swap[A, B](tuple: => (A,B)): (B, A) = 
 ${ swapExpr('tuple) }

private def swapExpr[A: Type, B: Type](tuple: Expr[(A,B)]) given QuoteContext: Expr[(B, A)] = {
  tuple match {
    case '{ ($x1, $x2) } => '{ ($x2, $x1) }
    case _ => '{ $tuple.swap }
  }
}

object Const {
  def unapply[T](expr: Expr[T]) given (qc: QuoteContext): Option[T] = {
    import qc.tasty._

    def rec(tree: Term): Option[T] = tree match {
      case Literal(c) => Some(c.value.asInstanceOf[T])
      case Block(Nil, e) => rec(e)
      case _ => None
    }
    rec(expr.unseal)
  }
}
// Ex. 7.1
