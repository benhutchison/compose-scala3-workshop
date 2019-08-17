val dottyVersion = "0.17.0-RC1"

lazy val commonSettings = Seq(
  version := "0.1.0",

  scalaVersion := dottyVersion,

  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core" % "2.0.0-M4",
    "com.novocode" % "junit-interface" % "0.11" % "test"
  ).map(_.withDottyCompat(dottyVersion)),
)

lazy val root = project
  .in(file("."))
  .settings(
    name := "Melbourne :: Compose 2019 Scala 3 workshop",
    commonSettings
  )

lazy val solutions = project
  .in(file("solutions"))
  .dependsOn(root)
  .settings(
    commonSettings
  )  