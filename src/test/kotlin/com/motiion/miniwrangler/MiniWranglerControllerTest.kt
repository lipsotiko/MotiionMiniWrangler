package com.motiion.miniwrangler

import com.nhaarman.mockitokotlin2.mock
import org.junit.Test
import org.mockito.Mockito.verify

class MiniWranglerControllerTest {

  private val transformer: Transformer = mock()
  private val repository: OrderRepository = mock()

  private lateinit var miniWranglerController: MiniWranglerController

  @Test
  fun customer_order_is_passed_to_a_translator() {
    miniWranglerController = MiniWranglerController(transformer, repository)
    miniWranglerController.importOrders("sample orders")
    verify(transformer).process("sample orders")
  }

  @Test
  fun translated_order_is_persisted_to_the_orders_repository() {
    //       whenever(transformer.process("sample orders")).thenReturn("translated data")
//        miniWranglerController = MiniWranglerController(transformer, repository)
//        miniWranglerController.importOrders("sample orders")
//
  }
}
