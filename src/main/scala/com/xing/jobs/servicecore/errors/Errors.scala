package com.xing.jobs.servicecore.errors

import scala.util.control.NoStackTrace

abstract class ApiError extends Exception with NoStackTrace

abstract class ApiError_(message: String, cause: Option[Throwable]) extends ApiError {
  override def getMessage = message

  override def getCause = cause.orNull

}

final case class AuthenticationFailedError(message: String, cause: Option[Throwable] = None) extends ApiError_(message, cause)

final case class GenericError(message: String, cause: Option[Throwable]) extends ApiError_(message, cause)

final case class JsonDecodeFailedError(message: String, cause: Option[Throwable] = None) extends ApiError_(message, cause)

final case class NotFoundError(message: String, cause: Option[Throwable] = None) extends ApiError_(message, cause)

final case class GenericMultipleErrors(message: String, cause: Option[Throwable] = None, errors: List[String]) extends ApiError_(message, cause)


object Errors {
  def error(message: String): GenericError = GenericError(message, None)

  def error(message: String, cause: Throwable): GenericError = GenericError(message, Some(cause))

  def authFailedError(message: String): AuthenticationFailedError = AuthenticationFailedError(message, None)

  def authFailedError(message: String, cause: Throwable): AuthenticationFailedError =
    AuthenticationFailedError(message, Some(cause))

  def jsonDecodeFailedError(message: String): JsonDecodeFailedError = JsonDecodeFailedError(message, None)

  def jsonDecodeFailedError(message: String, cause: Throwable): JsonDecodeFailedError =
    JsonDecodeFailedError(message, Some(cause))

  def notFoundError(message: String): NotFoundError = NotFoundError(message, None)

  def notFoundError(message: String, cause: Throwable): NotFoundError = NotFoundError(message, Some(cause))
}