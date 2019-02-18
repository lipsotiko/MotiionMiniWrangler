package com.motiion.miniwrangler

import org.springframework.web.bind.annotation.RestController

@RestController
class MiniWranglerController {
    fun importOrders(sampleOrders: String) {
        println(sampleOrders)
    }
}
