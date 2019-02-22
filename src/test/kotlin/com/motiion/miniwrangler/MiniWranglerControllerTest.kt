package com.motiion.miniwrangler

import com.fasterxml.jackson.databind.ObjectMapper
import com.motiion.transformer.Transformer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class MiniWranglerControllerTest {

  private val transformer: Transformer = mock()
  private val ordersRepositoryCustomer: CustomerOrderRepository = mock()
  private val objectMapper: ObjectMapper = mock()

  private lateinit var miniWranglerController: MiniWranglerController

  private val customerOrder1: CustomerOrder = mock()
  private val customerOrder2: CustomerOrder = mock()

  @Before
  fun set_up() {
    whenever(transformer.transform("orders csv"))
      .thenReturn(listOf("translated record 1", "translated record 2"))
    whenever(objectMapper.readValue("translated record 1", CustomerOrder::class.java))
      .thenReturn(customerOrder1)
    whenever(objectMapper.readValue("translated record 2", CustomerOrder::class.java))
      .thenReturn(customerOrder2)
    miniWranglerController = MiniWranglerController(transformer, ordersRepositoryCustomer, objectMapper)
    miniWranglerController.importOrdersCsv("orders csv")
  }

  @Test
  fun orders_are_process_by_the_translator() {
    verify(transformer).transform("orders csv")
  }

  @Test
  fun translated_orders_are_persisted_to_the_orders_repository() {
    verify(ordersRepositoryCustomer, times(1)).save(customerOrder1)
    verify(ordersRepositoryCustomer, times(1)).save(customerOrder2)
  }
}
