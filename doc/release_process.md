http://www.scala-sbt.org/0.13/docs/Using-Sonatype.html

1. Update version in sbt, e.g. 0.1-SNAPSHOT to 0.1
2. Create release in github
3. Call sbt clean test
4. Call publishSigned
5. Follow http://central.sonatype.org/pages/releasing-the-deployment.html (call close then release)
6. Check https://oss.sonatype.org/content/repositories/releases/com/github/danielkorzekwa/bayes-scala_2.11/
7. Update version, e.g. from 0.1 to 0.2-SNAPSHOT and commit