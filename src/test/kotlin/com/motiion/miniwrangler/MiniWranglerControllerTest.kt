package com.motiion.miniwrangler

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class MiniWranglerControllerTest {

  private val transformer: Transformer = mock()
  private val ordersRepository: OrderRepository = mock()
  private val objectMapper: ObjectMapper = mock()

  private lateinit var miniWranglerController: MiniWranglerController

  private val customerOrder1 = CustomerOrder(1, 1L)
  private val customerOrder2 = CustomerOrder(2, 2L)

  @Before
  fun set_up() {
    whenever(transformer.processCsv("orders csv"))
      .thenReturn(listOf("translated record 1", "translated record 2"))
    whenever(objectMapper.readValue("translated record 1", CustomerOrder::class.java))
      .thenReturn(customerOrder1)
    whenever(objectMapper.readValue("translated record 2", CustomerOrder::class.java))
      .thenReturn(customerOrder2)
    miniWranglerController = MiniWranglerController(transformer, ordersRepository, objectMapper)
    miniWranglerController.importOrders("orders csv")
  }

  @Test
  fun orders_are_process_by_the_translator() {
    verify(transformer).processCsv("orders csv")
  }

  @Test
  fun translated_orders_are_persisted_to_the_orders_repository() {
    verify(ordersRepository, times(1)).save(customerOrder1)
    verify(ordersRepository, times(1)).save(customerOrder2)
  }
}
