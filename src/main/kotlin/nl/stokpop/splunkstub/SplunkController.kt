package nl.stokpop.splunkstub

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger
import java.util.regex.Pattern

@RestController
class SplunkController {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
        private val pattern: Pattern = Pattern.compile("\\{\"index\":")
        private val totalCounter: AtomicInteger = AtomicInteger(0)
        private val twoSeconds: Duration = Duration.ofSeconds(2);
    }
    @PostMapping("/**")
    suspend fun submitEventBatch(@RequestBody data: String): String {
        val matcher = pattern.matcher(data)
        var counter = 0
        while (matcher.find()) {
            counter += 1
        }
        totalCounter.addAndGet(counter)
        log.info("submitted batch with $counter events (total: $totalCounter)")
        actualDelay(twoSeconds)
        return "{ 'done':'true' }"
    }

    @GetMapping("/**")
    suspend fun testDelay(): String {

        val sb = StringBuilder()
        var found = 0
        while (sb.length < 100_000) {
            sb.append(" foo ${System.currentTimeMillis()} ")
            if (Pattern.compile("(\\w*)*").matcher(sb.toString()).find()) found++
        }
        println("Found: $found")

        actualDelay(twoSeconds)
        return "{ 'done':'true' }"
    }

    suspend fun actualDelay(duration: Duration) {
        delay(duration.toMillis())
    }

}