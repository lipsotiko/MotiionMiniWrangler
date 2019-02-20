package com.motiion.miniwrangler

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MiniWranglerControllerIntegrationTest {

  @Autowired
  lateinit var restTemplate: TestRestTemplate

  @Autowired
  lateinit var orderRepository: OrderRepository

  val sampleOrders = getSampleDataFromResource("fixtures", "orders.csv")

  @Test
  fun customer_orders_are_persisted_to_motiion_database() {
    val entity = restTemplate.postForEntity<String>("/api/import-orders-csv", sampleOrders, String::class)
    assertThat(entity.statusCode.is2xxSuccessful)

    val importedOrders = orderRepository.findAll()
    assertThat(importedOrders.size).isEqualTo(2)

    assertThat(importedOrders[0].OrderID).isEqualTo(1000)
    assertThat(importedOrders[0].OrderDate).isEqualTo(LocalDate.parse("2018-01-01"))

    assertThat(importedOrders[1].OrderID).isEqualTo(1001)
    assertThat(importedOrders[1].OrderDate).isEqualTo(LocalDate.parse("2017-12-12"))
  }
}
