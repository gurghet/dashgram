val circeVersion = "0.10.0"
val betterFilesVersion = "3.7.0"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

libraryDependencies += "com.softwaremill.sttp" %% "core" % "1.5.2"

libraryDependencies += "com.github.pathikrit" %% "better-files" % betterFilesVersion

enablePlugins(JavaAppPackaging)

packageName in Docker := "gurghet/dashgram"

version := "0.2.1"