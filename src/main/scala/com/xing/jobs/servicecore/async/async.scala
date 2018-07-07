package com.xing.jobs.servicecore.async

import java.util.concurrent.ExecutorService
import com.twitter.util.FuturePool
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors.{newSingleThreadExecutor, newFixedThreadPool}
import scala.concurrent.ExecutionContext.fromExecutor

import com.xing.jobs.servicecore.async.AsyncOps.shutdownExecutorService
import com.xing.jobs.servicecore.config.Config

package object async {
  lazy val singleThreadedExecutor: ExecutorService = newSingleThreadExecutor
  lazy val singleThreadedExecutionContext: ExecutionContext = fromExecutor(singleThreadedExecutor)
  lazy val singleThreadedFuturePool: FuturePool = FuturePool.interruptible(singleThreadedExecutor)

  lazy val executorService: ExecutorService = newFixedThreadPool(Config.maxThreadPoolSize)
  lazy val futurePool: FuturePool = FuturePool.interruptible(executorService)
  lazy val globalAsyncExecutionContext: ExecutionContext = scala.concurrent.ExecutionContext.fromExecutor(executorService)

  sys.addShutdownHook(shutdownExecutorService(executorService))
}
