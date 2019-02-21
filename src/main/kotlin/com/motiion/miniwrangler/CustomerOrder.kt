package com.motiion.miniwrangler

import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class CustomerOrder(@Id
                    @GeneratedValue(strategy = GenerationType.IDENTITY)
                    var id: Long,
                    var orderId: Long,
                    var orderDate: LocalDate,
                    var productId: String,
                    var productName: String,
                    var quantity: BigDecimal,
                    var unit: String)
