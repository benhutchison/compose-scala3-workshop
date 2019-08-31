package scala3workshop.quotedmacros

import scala.compiletime._
import scala.quoted._
import scala.tasty.Reflection


// Macro: 
// - must be inline
// - method body must be a splice ${...}
// - In this case, <: means that it is a "whitebox" macro; the macro will
//   decide the static return type. 
// - Here, the inline `cond` parameter must be statically known; 
//   the static type of the expression will be whichever branch got selected.


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


// Defines a pattern match `Const(...)` on Exprs. 
// Techniques: 
// - Unlocks low-level AST access by importing `tasty` from the QuoteContext
// - Converts the Expr into the low-level Term with `unseal`
// - Term has a number of subclasses, including `Literal` and `Block`
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
