package com.xing.jobs.servicecore.errors

import com.twitter.finagle.CancelledRequestException
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future
import io.finch.{Encode, Output}

import com.xing.jobs.servicecore.http.ResponseOps
import com.xing.jobs.servicecore.logger.Logger

abstract class ErrorHandler(logger: Logger) extends ResponseOps {
  def apiErrorHandler: PartialFunction[Throwable, Output[Nothing]]

  final def topLevelErrorHandler(request: Request, encoder: Encode.Json[Throwable]): PartialFunction[Throwable, Future[Response]] = {
    case e: AuthenticationFailedError => respond(Status.Unauthorized, e, encoder)
    case e: CancelledRequestException => respond(Status.ClientClosedRequest, e, encoder)
    case t: Throwable => unhandledException(request.uri, t, encoder)
  }

  private def unhandledException(requestUri: String, t: Throwable, encoder: Encode.Json[Throwable]): Future[Response] = {
    logUnhandledError(requestUri, t)
    respond(Status.InternalServerError, t, encoder)
  }

  //noinspection ScalaStyle
  private def logUnhandledError(requestUri: String, t: Throwable): Unit =
    try {
      logger.info(s"Unhandled exception on URI $requestUri with message $t")
    } catch {
      case e: Throwable =>
        Console.err.println(s"Unable to log unhandled exception: $e")
        throw e
    }

  private def respond(status: Status, t: Throwable, encoder: Encode.Json[Throwable]): Future[Response] = {
    val response = jsonResponse(status, t)(encoder)
    response.cacheControl = "no-cache"
    Future.value(response)
  }
}