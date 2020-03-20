package org.fernice.reflare.cache

import org.fernice.flare.url.Url
import org.fernice.reflare.internal.ImageHelper
import org.fernice.reflare.util.concurrentMap
import java.awt.Image
import java.lang.RuntimeException
import java.net.URL
import java.security.AccessController
import java.security.PrivilegedAction
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Supplier
import javax.imageio.ImageIO

object ImageCache {

    private val cachedImages = concurrentMap<Url, CompletableFuture<out Image>>()

    private val threadCount = AtomicInteger()
    private val unboundExecutor = Executor { runnable ->
        val thread = Thread(runnable, "fernice-image-loader-${threadCount.getAndIncrement()}")
        thread.isDaemon = true
        thread.start()

    }
    private val boundExecutor = ThreadPoolExecutor(0, 8, 50, TimeUnit.MILLISECONDS, LinkedBlockingQueue()) { runnable ->
        val thread = Thread(runnable, "fernice-shared-image-loader-${threadCount.getAndIncrement()}")
        thread.isDaemon = true
        thread
    }

    fun fetch(url: Url, callback: (() -> Unit)? = null): CompletableFuture<out Image> {
        val future = cachedImages.computeIfAbsent(url) {
            execute(shareExecutor = callback != null) {
                if (url.value.startsWith("/")) {
                    ImageHelper.getMultiResolutionImageResource(url.value)
                } else {
                    ImageIO.read(URL(url.value)) ?: throw RuntimeException("image could not be processed: ImageIO.read() returned null")
                }
            }
        }

        if (callback != null && !future.isDone) {
            future.thenRun(callback)
        }

        return future
    }

    private fun <T> execute(shareExecutor: Boolean, block: () -> T): CompletableFuture<T> {
        val threadExecutor = if (shareExecutor) boundExecutor else unboundExecutor
        return CompletableFuture.supplyAsync(Supplier {
            AccessController.doPrivileged(PrivilegedAction { block() })
        }, threadExecutor)
    }
}
