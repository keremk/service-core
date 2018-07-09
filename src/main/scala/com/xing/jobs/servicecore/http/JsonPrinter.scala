package com.xing.jobs.servicecore.http

import java.nio.ByteBuffer
import com.twitter.io.Buf
import io.circe.{Json, Printer}

import com.xing.jobs.servicecore.io.BufOps

trait JsonPrinter {
  private val printer = Printer.noSpaces.copy(dropNullValues = true)

  /**
    * @note Use sparingly, prefer `jsonToByteBuffer` or `jsonToBuff` if printing to a response (as it's faster).
    */
  final def jsonToString(json: Json): String = printer.pretty(json)

  final def jsonToByteBuffer(json: Json): ByteBuffer = printer.prettyByteBuffer(json)

  final def jsonToBuf(json: Json): Buf = BufOps.byteBufferToBuf(jsonToByteBuffer(json))
}

object JsonPrinter extends JsonPrinter