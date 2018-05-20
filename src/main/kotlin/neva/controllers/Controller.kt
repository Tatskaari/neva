package neva.controllers

import fi.iki.elonen.NanoHTTPD.*

/**
 * This is where all the helper functions for controllers will live.
 */
fun ok(content: String): Response {
  return newFixedLengthResponse(content)
}
