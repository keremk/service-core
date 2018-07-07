package com.xing.jobs.servicecore.config

import com.twitter.util.{Duration, StorageUnit}
import com.twitter.conversions.storage._
import com.twitter.conversions.time._

trait SystemGettable {
  def getEnvVar(name: String): Option[String]
  def getPropsVar(name: String): Option[String]
}

class System extends SystemGettable {
  final def getEnvVar(name: String): Option[String] = sys.env.get(name)
  final def getPropsVar(name: String): Option[String] = sys.props.get(name)
}

trait ConfigUtils {
  final def envVar(name: String)(implicit system: SystemGettable): Option[String] = system.getEnvVar(name)

  final def envVarOrFail(name: String)(implicit system: SystemGettable): String = {
    envVar(name) match {
      case Some(e) => e
      case None => sys.error(s"Required environment variable '$name' not found")
    }
  }

  final def envVarOrDefault(name: String, defaultValue: String)(implicit system: SystemGettable): String = {
    envVar(name) match {
      case Some(e) => e
      case None => defaultValue
    }
  }

  final def propertyVar(name: String)(implicit system: SystemGettable): Option[String] = system.getPropsVar(name)

  final def propertyVarOrFail(name: String)(implicit system: SystemGettable): String = {
    propertyVar(name) match {
      case Some(e) => e
      case None => sys.error(s"Required system property '$name' not found")
    }
  }
}

trait SystemConfig extends ConfigUtils {
  final implicit val system: SystemGettable = new System()

  // The ID of the service, used in logging, tracing, metrics, etc.
  final val systemId: String = envVarOrFail("SYSTEM-ID")

  final val systemName: String = envVarOrFail("SYSTEM-NAME")

  final val coreLoggerName: String = systemId

  final def environment: String = envVarOrFail("ENV")

  final def listenAddress: String = s":${envVarOrDefault("PORT", "3030")}"

  // The maximum size of the thread pool used for asynchronous work (outside of the Finagle/Netty request handler).
  final val maxThreadPoolSize: Int = envVarOrDefault("MAX-THREAD-POOL-SIZE", "5").toInt

  // Reject requests greater than this size.
  final val maxRequestSize: StorageUnit = envVarOrDefault("MAX-REQUEST-SIZE", "5").toInt.megabytes

  // The maximum amount of time a server is allowed to spend handling the incoming request, see `CommonParams#withRequestTimeout`.
  final val requestTimeout: Duration = envVarOrDefault("MAX-REQUEST-TIMEOUT", "60").toInt.seconds

  // Service concurrency: https://twitter.github.io/finagle/guide/Servers.html#concurrency-limit
  final val maxConcurrentRequests: Int = envVarOrDefault("MAX-CONCURRENT-REQUESTS", "1750").toInt
  final val maxWaiters: Int = envVarOrDefault("MAX-WAITERS", "500").toInt
}

object Config extends SystemConfig with ConfigUtils

