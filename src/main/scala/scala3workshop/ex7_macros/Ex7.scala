package scala3workshop.ex7_macros


// There is a new `inline` keyword, which forces (not recommends - forces!) the compiler to recompile 
// the code inline at every use-site.
//
// This exists for 2 reasons:
// 1. Advanced users can, with care, optimise code to remove indirection costs.
// 2. The inline functionality enables Scala 3's macro system.

// To experiment with the code in this chapter, either run `console` in sbt and 
// scala> import scala3workshop.ex7_macros._
// or run your arbitrary code here with `run`, selecting `Ex7`.
object Ex7 {
  def main(args: Array[String]): Unit = {
    // Put whatever code you want here to play around with the stuff in this chapter

  }
}

// Ex. 7.1
