package com.motiion.miniwrangler

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MiniWranglerController(val transformer: Transformer,
                             val orderRepository: OrderRepository) {

  @PostMapping("/api/import-orders-csv")
  fun importOrders(@RequestBody ordersCsv: String) {
    transformer.process(ordersCsv)
  }

}
