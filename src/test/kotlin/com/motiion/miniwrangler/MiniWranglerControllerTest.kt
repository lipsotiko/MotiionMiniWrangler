package com.motiion.miniwrangler

import org.junit.Test

class MiniWranglerControllerTest {

    var miniWrangglerController = MiniWranglerController()

    @Test
    fun consumes_data_and_returns_translated_data() {
        val sampleOrders = getSampleDataFromResource("fixtures", "orders.csv")
        miniWrangglerController.importOrders(sampleOrders)
    }

}
