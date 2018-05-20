package neva

import fi.iki.elonen.NanoHTTPD
import neva.routing.getRoutes

object Application : NanoHTTPD("localhost", 9000) {
  override fun serve(session: IHTTPSession): Response {
    val action = getRoutes("out/production/classes").find { it.path == session.uri }
    return action?.invoke() ?: newFixedLengthResponse("Not found")
  }
}

fun main(args: Array<String>) {
  Application.start()
  while(Application.isAlive){
    Thread.sleep(1000)
  }
}