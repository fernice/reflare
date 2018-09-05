package modern.reflare.cache

import de.krall.flare.url.Url
import java.awt.image.BufferedImage
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

    private val images: MutableMap<Url, CompletableFuture<BufferedImage>> = mutableMapOf()

    fun image(url: Url): CompletableFuture<BufferedImage> {
        val cachedRequest = images[url]

        if (cachedRequest != null) {
            return cachedRequest
        }

        val future = CompletableFuture.supplyAsync(Supplier {
            ImageIO.read(URL(url.value))
        }, executor)

        images[url] = future
        return future
    }
}