package com.motiion.miniwrangler

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.mockito.Mockito.verify

class MiniWranglerControllerTest {

    private val translator: Translator = mock()
    private val repository: OrderRepository = mock()

    private lateinit var miniWranglerController: MiniWranglerController

    @Test
    fun customer_order_is_passed_to_a_translator() {
        miniWranglerController = MiniWranglerController(translator, repository)
        miniWranglerController.importOrders("sample orders")
        verify(translator).translate("sample orders")
    }

    @Test
    fun translated_order_is_persisted_to_the_orders_repository() {
        whenever(translator.translate("sample orders")).thenReturn("translated data")
//        miniWranglerController = MiniWranglerController(translator, repository)
//        miniWranglerController.importOrders("sample orders")
//
    }
}
