# Vehicle Categorisation Case Study

Ben Hutchison

In this workshop we will explore the capabilities of Scala 3, and functional, strongly-typed Scala more generally, via a "real world" case study.

## Problem Domain

The problem domain is vehicle classification; that is, specifying the rules by which different kinds of vehicles on the road in Australia are categorized. We will base our software model on the actual Australian Standards for vehicle categories. Using externally derived domain models
helps ensure we use software techniques that will scale to the real world. When we "make up" example domains, we can have a tendency to fit the problem to the point we're trying to make. 

Before digging into code, load the vehicle standard into a browser and take 5-10mins to review the document. Note especially section 4, which defines the different vehicle types.

[Vehicle Standard (Australian Design Rule - Definitions and Vehicle Categories) 2005](https://www.legislation.gov.au/Details/F2012C00326)

### Exercise: Conceptual Modelling

With  complex domain, there are many possible approaches, and different aspects of the model that can be emphasized. Before looking at my implementation, have a think for yourself about these questions:

- What are some key domain concepts that you would want to model to describe vehicle categories in a software model?
- What are the primary operations that a vehicle categorisation model should provide?
- Where can strong static types play a role in helping to ensure the integrity of the model?




## Strongly Typed, Total Functional Programming

My Approach is founded on the idea of *strongly typed, total functional programming*, making a few pragmatic concessions to focus on getting the most cost-effective value. I believe strongly typed total functional programming approaches tend to deliver composeable, correct and bit-rot resistant code that will have the lowest total-cost-of-ownership over a system's life (ie cost of development and all maintenance).

What does *total* imply over any other kind of functional programming? Well, typically people think of total FP as guaranteeing *termination* over and above regular FP; that is, the absence of infinite loops. My pragmatic experience to date has been that infite loops aren't a major problem, so Im not going to emphasize that aspect of totality. Rather, Im going to tackle the problem of abnormal termination (ie exceptional conditions), which (much more than infinite loops) are a regular hazard for the working softare engineer. 

Our basic strategy for preventing abnormal termination will be to use precise, desciptive types that "dont lie", and to write functions that are defined over their complete domain. This means that our program won't receive unepected inputs, which will help ensure we don't get any unexpected behaviors.

## Building Blocks

### [Nats](nat/src)

Nats are positive integers (including zero). Many real world quantities, like the number of wheels on a vehicle, can't be negative. So we can't use cant `Int` for such quantities if we want to write types that dont lie, since an `Int` might be negative.

Scala core doesn't give us built support for posiitve integers, but especially with opaque types,
a new Scala 3 feature, we can design a performant positive integer that has static guarantees but is represented by a native `Int` in the underlying machine.


### [PDecimals](pdecimal/src)

`PDecimal`s are positive decimals, the fractional analogs to `Nat`s. We choose not to use `Double`s but rather `BigDecimal`s because built-in floating point numbers are base2, and not base10, which can lead to perplexing errors. Remember, wherever possible types shouldn't lie!

### [Bounds](bound/src)

Bounds are an sbtraction for describing a range of number values that are acceptable. It might be a single number, any number, any number less than or greater than a threshold, an interval between two bounds, or some combination of these.

### [Vehicles and VehicleCategories](src)

With all the above tools working, we can define a DSL for describing vehicle categories. Our DSL should nebale us to classify a vehicle instances, based upon its characteristics.