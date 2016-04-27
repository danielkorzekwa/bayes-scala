publishMavenStyle := true

pomIncludeRepository := { _ => false }

publishArtifact in Test := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomExtra := (
  <url>https://github.com/danielkorzekwa/bayes-scala</url>
  <licenses>
    <license>
      <name>BSD-style</name>
      <url>http://www.opensource.org/licenses/bsd-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:danielkorzekwa/bayes-scala.git</url>
    <connection>scm:git:git@github.com:danielkorzekwa/bayes-scala.git</connection>
  </scm>
  <developers>
    <developer>
      <id>danielkorzekwa</id>
      <name>Daniel Korzekwa</name>
      <url>https://github.com/danielkorzekwa</url>
    </developer>
  </developers>)