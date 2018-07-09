package com.xing.jobs.servicecore.logger

import java.util.logging.{Level, LogManager}

import com.twitter.app.App
import org.slf4j.bridge.SLF4JBridgeHandler

trait Slf4jLogging {self: App =>
  init {
    // Turn off Java util logging so that slf4j can configure it
    LogManager.getLogManager.getLogger("").getHandlers.toList.foreach { l =>
      l.setLevel(Level.OFF)
    }
    org.slf4j.LoggerFactory.getLogger("slf4j-logging").debug("Installing SLF4J logging")
    SLF4JBridgeHandler.install()
  }

  onExit {
    org.slf4j.LoggerFactory.getLogger("slf4j-logging").debug("Uninstalling SLF4J logging")
    SLF4JBridgeHandler.uninstall()
  }
}