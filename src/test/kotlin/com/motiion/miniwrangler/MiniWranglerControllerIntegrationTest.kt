package com.motiion.miniwrangler

import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.test.context.junit4.SpringRunner
import java.math.BigDecimal
import java.time.LocalDate

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MiniWranglerControllerIntegrationTest {

  @Autowired
  lateinit var restTemplate: TestRestTemplate

  @Autowired
  lateinit var customerOrderRepository: CustomerOrderRepository

  val sampleOrders = getSampleDataFromResource("fixtures", "orders.csv")

  @Test
  fun customer_may_persist_orders_to_motiion_database() {
    val entity = restTemplate.postForEntity<String>("/api/import-orders-csv", sampleOrders, String::class)
    assertThat(entity.statusCode.is2xxSuccessful)

    val orders = customerOrderRepository.findAll()
    assertThat(orders.size).isEqualTo(2)

    assertThat(orders[0].orderId).isEqualTo(1000)
    assertThat(orders[0].orderDate).isEqualTo(LocalDate.parse("2018-01-01"))
    assertThat(orders[0].productId).isEqualTo("P-10001")
    assertThat(orders[0].productName).isEqualTo("Arugola")
    assertEquals(orders[0].quantity, BigDecimal(5250.50).setScale(2))
    assertThat(orders[0].unit).isEqualTo("kg")

    assertThat(orders[1].orderId).isEqualTo(1001)
    assertThat(orders[1].orderDate).isEqualTo(LocalDate.parse("2017-12-12"))
    assertThat(orders[1].productId).isEqualTo("P-10002")
    assertThat(orders[1].productName).isEqualTo("Iceberg lettuce")
    assertEquals(orders[1].quantity, BigDecimal(500).setScale(2))
    assertThat(orders[1].unit).isEqualTo("kg")
  }
}
