package com.motiion.miniwrangler

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MiniWranglerController(val transformer: Transformer,
                             val orderRepository: OrderRepository,
                             val objectMapper: ObjectMapper) {

  @PostMapping("/api/import-orders-csv")
  fun importOrders(@RequestBody ordersCsv: String) {
      transformer.processCsv(ordersCsv).forEach { record ->
      orderRepository.save(objectMapper.convertValue(record, CustomerOrder::class.java))
    }
  }
}
