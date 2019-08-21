val dottyVersion = "0.17.0-RC1"

lazy val commonSettings = Seq(
  version := "0.1.0",

  scalaVersion := dottyVersion,

  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core" % "2.0.0-M4",
    "org.typelevel" %% "mouse" % "0.22",
    "org.typelevel" %% "algebra" % "2.0.0-M2"
,    "com.novocode" % "junit-interface" % "0.11" % "test"
  ).map(_.withDottyCompat(dottyVersion)),
)

lazy val root = project
  .in(file("."))
  .dependsOn(macros)
  .settings(
    name := "Melbourne :: Compose 2019 Scala 3 workshop",
    commonSettings
  )

//nat project defines macros. Macros must be defined in a separate project to their usage
//to ensure they are compiled before being referenced.
//Note how the root project `.dependsOn(nat)`
lazy val macros = project
  .in(file("macros"))
  .settings(
    commonSettings
  )  

lazy val solutions = project
  .in(file("solutions"))
  .dependsOn(root)
  .settings(
    commonSettings
  )  