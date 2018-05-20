package exampleapp.controllers

import fi.iki.elonen.NanoHTTPD
import neva.controllers.ok
import neva.routing.Get

class Index {
  @Get("/hello")
  fun index(): NanoHTTPD.Response {
    return ok(exampleapp.views.index("test"))
  }

  @Get("/superindex")
  fun superduper(): NanoHTTPD.Response {
    return ok(exampleapp.views.superIndex())
  }
}

