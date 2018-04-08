#! /usr/local/bin/amm

import ammonite.ops._
import ammonite.ops.ImplicitWd._


@main
def list(port: String) = {
  val progs = %%("sudo", "lsof","-i",s":${port}")
  val lines = progs.out.lines
  //pprint.pprintln(lines)
  val apps = lines.tail.map(_.split(" ").filterNot(_=="").take(2))
  apps
}

@main
def kill(port: String = "8080", tokill: String = "java") = {
  val apps = list(port)
  pprint.pprintln(apps)
  for{ a <- apps
       app = a.head
       p = a(1)
       if app == tokill
  } {
    println(s"killing $app with process $p at port $port")
    %("sudo", "kill", "-9", p)
  }
}