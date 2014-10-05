name := "Scala Burst Trie"

description := "Scala implementation of a Burst Trie"

organization := "com.nefariouszhen.trie"

version := "0.2-SNAPSHOT"

licenses := Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("https://github.com/nbauernfeind/scala-burst-trie"))

publishMavenStyle := true

scalaVersion := "2.11.2"

crossScalaVersions := Seq("2.10.4", "2.11.2")

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomExtra :=
<scm>
   <connection>scm:git:https://github.com/nbauernfeind/scala-burst-trie.git</connection>
   <developerConnection>scm:git:ssh://git@github.com:nbauernfeind/scala-burst-trie.git</developerConnection>
   <url>https://github.com/nbauernfeind/scala-burst-trie</url>
</scm>
<developers>
  <developer>
    <id>nbauernfeind</id>
    <name>Nathaniel Bauernfeind</name>
    <email>nbauernfeind@gmail.com</email>
    <roles>
      <role>developer</role>
    </roles>
  </developer>
</developers>
