ThisBuild / scalaVersion := "3.2.0"
Compile / compileOrder := CompileOrder.JavaThenScala
exportJars := true

lazy val word = (project in file("."))
  .settings(
    libraryDependencies ++= Seq("com.squareup.okhttp3" % "okhttp" % "4.10.0",
    "org.jsoup" % "jsoup" % "1.15.3",
   "org.json4s" %% "json4s-native" % "4.0.6",
    )
  )
