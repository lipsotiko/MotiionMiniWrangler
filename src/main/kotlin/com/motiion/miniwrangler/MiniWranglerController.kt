package com.motiion.miniwrangler

import com.fasterxml.jackson.databind.ObjectMapper
import com.motiion.transformer.Transformer
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MiniWranglerController(val transformer: Transformer,
                             val customerOrderRepository: CustomerOrderRepository,
                             val objectMapper: ObjectMapper) {
  @PostMapping("/api/import-orders-csv")
  fun importOrdersCsv(@RequestBody ordersCsv: String): String {
    transformer.transform(ordersCsv).forEach { record ->
      customerOrderRepository.save(objectMapper.readValue(record, CustomerOrder::class.java))
    }

    return "${customerOrderRepository.count()} records exist in the Customer Orders table"
  }
}
