package com.xing.jobs.servicecore.http

import com.twitter.finagle.http.{Fields, Response, Status, Version}
import com.twitter.finagle.http.Version.Http11
import com.twitter.io.Buf
import io.circe.{Encoder, Json}
import io.finch.Encode

import com.xing.jobs.servicecore.io.Charset.DefaultCharset
import com.xing.jobs.servicecore.io.BufOps.byteBufferToBuf
import com.xing.jobs.servicecore.http.HttpTime.currentTime

trait ResponseOps extends JsonPrinter {
  private val DefaultHttpVersion = Http11

  final def jsonBuf[A](a: A)(implicit encoder: Encode.Json[A]): Buf = encoder.apply(a, DefaultCharset)

  /**
    * Encode a response, with an enclosing `data` field at the root of the returned JSON.
    */
  final def dataJsonEncode[A](implicit encoder: Encoder[A]): Encode.Json[A] =
    Encode.json { (a, _) =>
      byteBufferToBuf(jsonToByteBuffer(Json.obj("data" -> encoder.apply(a))))
    }

  final def exceptionJsonEncode(implicit encoder: Encoder[Exception]): Encode.Json[Exception] =
    Encode.json { (e, _) =>
      byteBufferToBuf(jsonToByteBuffer(Json.obj("errors" -> Json.arr(encoder.apply(e)))))
    }

  final def throwableJsonEncode(implicit encoder: Encoder[Throwable]): Encode.Json[Throwable] =
    Encode.json { (e, _) =>
      byteBufferToBuf(jsonToByteBuffer(Json.obj("errors" -> Json.arr(encoder.apply(e)))))
    }

  final def jsonResponse[A](status: Status, a: A, version: Version = DefaultHttpVersion)(implicit encode: Encode.Json[A]): Response = {
    val response = newResponse(status, version)
    response.setContentTypeJson()
    response.content = jsonBuf(a)
    response
  }

  final def textResponse(status: Status, content: Buf, version: Version = DefaultHttpVersion): Response = {
    val response = newResponse(status, version)
    response.headerMap.add(Fields.ContentLength, content.length.toString)
    response.headerMap.add(Fields.ContentLanguage, "en")
    response.headerMap.add(Fields.ContentType, "text/plain")
    response.content = content
    response
  }

  private def newResponse(status: Status, version: Version) = {
    val response = Response()
    response.status = status
    response.version = version
    response.date = currentTime()
    response
  }
}

object ResponseOps extends ResponseOps