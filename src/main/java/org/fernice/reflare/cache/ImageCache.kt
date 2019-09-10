package org.fernice.reflare.cache

import org.fernice.flare.url.Url
import org.fernice.reflare.internal.ImageHelper
import java.awt.Image
import java.net.URL
import java.util.concurrent.CompletableFuture
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Supplier
import javax.imageio.ImageIO

object ImageCache {

    private val threadCount = AtomicInteger()
    private val executor: ThreadPoolExecutor

    init {
        executor = ThreadPoolExecutor(0, 4, 50, TimeUnit.MILLISECONDS, LinkedBlockingQueue()) { runnable ->
            val thread = Thread(runnable, "image-loader-${threadCount.getAndIncrement()}")
            thread.isDaemon = true
            thread
        }
    }

    private val images: MutableMap<Url, CompletableFuture<out Image>> = mutableMapOf()

    fun image(url: Url, invoker: () -> Unit): CompletableFuture<out Image> {
        val cachedRequest = images[url]

        if (cachedRequest != null) {
            if (!cachedRequest.isDone) {
                cachedRequest.thenRun(invoker)
            }

            return cachedRequest
        }

        val future = if (url.value.startsWith("/")) {
            CompletableFuture.supplyAsync(Supplier {
                ImageHelper.getMultiResolutionImageResource(url.value)
            }, executor)
        } else {
            CompletableFuture.supplyAsync(Supplier {
                ImageIO.read(URL(url.value))
            }, executor)
        }

        future.thenRun(invoker)

        images[url] = future
        return future
    }
}