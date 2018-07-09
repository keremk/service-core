package com.xing.jobs.servicecore.logger

import com.xing.jobs.servicecore.config.Config
import com.xing.jobs.servicecore.async.async.futurePool

trait CoreLogger {
  final lazy val log = new Logger(Config.coreLoggerName)(futurePool)
}

object CoreLogger extends CoreLogger
