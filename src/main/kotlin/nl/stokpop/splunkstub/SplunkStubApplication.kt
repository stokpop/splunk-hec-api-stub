package nl.stokpop.splunkstub

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SplunkStubApplication

fun main(args: Array<String>) {
	runApplication<SplunkStubApplication>(*args)
}
