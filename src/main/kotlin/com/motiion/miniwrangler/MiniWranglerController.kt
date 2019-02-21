package com.motiion.miniwrangler

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MiniWranglerController(val transformer: Transformer,
                             val customerOrderRepository: CustomerOrderRepository,
                             val objectMapper: ObjectMapper) {
  @PostMapping("/api/import-orders-csv")

  fun importOrdersCsv(@RequestBody ordersCsv: String) {
    transformer.processCsv(ordersCsv).forEach { record ->
      customerOrderRepository.save(objectMapper.readValue(record, CustomerOrder::class.java))
    }
  }
}
