package com.motiion.miniwrangler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MiniWranglerApplication

fun main(args: Array<String>) {
	runApplication<MiniWranglerApplication>(*args)
}
