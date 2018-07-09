package com.xing.jobs.servicecore.errors

import com.xing.jobs.servicecore.http.ResponseOps
import com.xing.jobs.servicecore.errors.ExceptionEncoder.exceptionEncoder
import com.xing.jobs.servicecore.errors.ExceptionEncoder.throwableEncoder

import io.finch.Encode

trait ErrorEncoders {
  implicit val exceptionEncode: Encode.Json[Exception] = ResponseOps.exceptionJsonEncode(exceptionEncoder)
  implicit val throwableEncode: Encode.Json[Throwable] = ResponseOps.throwableJsonEncode(throwableEncoder)
}

object ErrorEncoders extends ErrorEncoders