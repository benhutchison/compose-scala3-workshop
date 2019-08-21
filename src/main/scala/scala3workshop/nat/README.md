Always question the status quo in programming practice

There's still a lot of room for improvement

Some programming practices are legacy of poor historical decisions that 
have hardened into industry-wide habits or assumptions  

the use of purely signed integers in Java, Scala and the ecosystem is an example

we build models of the world when we build software, and the world is full of 
non-negative numbers. 

Scala 3s Opaque Types provide a way to model non-negative integers, 
commonly called "Natural" numbers, using native Int as the underlying storage

we cant use the sign bit, but otherwise, suffer no performance penalty as they are 
regular Ints at runtime. 

The tradeoff of truly being Ints is we can't distinguish them via pattern-matching


an opaque type "forgets" the type and behavior of the underlying type, outside the context
where it is defined. The current package in this example.

To create nat literals:
import scala3workshop.nat._, Nat._, NatMacros._
nat(7): Nat