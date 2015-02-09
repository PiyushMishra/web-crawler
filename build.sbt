
name:="web-crawler"

version:="1.0"

scalaVersion:="2.11.4"

libraryDependencies += "edu.uci.ics" % "crawler4j" % "4.1"

libraryDependencies += "net.sourceforge.htmlunit" % "htmlunit" % "2.15"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.3.9"



assemblyExcludedJars in assembly := { 
  val cp = (fullClasspath in assembly).value
  cp filter {file => file.data.getName == "geronimo-stax-api_1.0_spec-1.0.1.jar"  || file.data.getName == "boilerpipe-1.1.0.jar" || 
  file.data.getName == "xmlbeans-2.3.0.jar"}
}

assemblyMergeStrategy in assembly := {
  case "logback.xml"                            => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

assemblyJarName in assembly := "mail-crawler"