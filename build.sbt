lazy val root = (project in file(".")).
  settings(
    name := "bayes-scala",
    organization := "com.github.danielkorzekwa",
    version := "0.7-SNAPSHOT",
    scalaVersion := "2.12.4",
    crossScalaVersions := Seq("2.12.4", "2.11.11"),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-encoding", "UTF-8",       // yes, this is 2 args
      "-unchecked",
      "-Xfuture"
      //"-Ywarn-unused-import"     // 2.11 only
    ),
    //coverageEnabled := true,
    coverageHighlighting := false,
    libraryDependencies ++= Seq(
      "org.apache.commons" % "commons-math3" % "3.6.1",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
      "org.scalanlp" %% "breeze" % "0.13.2",
      // test scoped
      "org.slf4j" % "slf4j-log4j12" % "1.7.2" % Test,
      "com.novocode" % "junit-interface" % "0.11" % Test
    ),
    
    resolvers ++= Seq(
    // other resolvers here
    // if you want to use snapshot builds (currently 0.12-SNAPSHOT), use this.
    "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
    "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
    )
  )
