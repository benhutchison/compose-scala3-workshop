val dottyVersion = "0.17.0-RC1"

lazy val commonSettings = Seq(
  version := "0.1.0",

  scalaVersion := dottyVersion,

  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core" % "2.0.0-M4",
    "org.typelevel" %% "mouse" % "0.22",
    "org.typelevel" %% "algebra" % "2.0.0-M2",
    "com.novocode" % "junit-interface" % "0.11" % Test,
    "org.specs2" %% "specs2-core" % "4.6.0" % Test,
    "org.scalacheck" %% "scalacheck" % "1.14.0" % Test,
  ).map(_.withDottyCompat(dottyVersion)),
)

lazy val root = project
  .in(file("."))
  .dependsOn(nat)
  .dependsOn(pdecimal)
  .dependsOn(bound)
  .settings(
    name := "Melbourne :: Compose 2019 Scala 3 workshop",
    commonSettings
  )

//nat project defines macros. Macros must be defined in a separate project to their usage
//to ensure they are compiled before being referenced.
//Note how the root project `.dependsOn(nat)`
lazy val nat = project
  .in(file("nat"))
  .settings(
    commonSettings
  )  

//pdecimal project defines macros. Macros must be defined in a separate project to their usage
//to ensure they are compiled before being referenced.
//Note how the root project `.dependsOn(pdecimal)`
lazy val pdecimal = project
  .in(file("pdecimal"))
  .settings(
    commonSettings
  )  

//interval project defines macros
lazy val bound = project
  .in(file("bound"))
  .settings(
    commonSettings
  )   

lazy val solutions = project
  .in(file("solutions"))
  .dependsOn(root)
  .settings(
    commonSettings
  )  