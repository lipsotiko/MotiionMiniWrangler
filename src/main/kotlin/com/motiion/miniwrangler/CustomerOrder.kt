package com.motiion.miniwrangler

import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class CustomerOrder(@Id
                         @GeneratedValue(strategy = GenerationType.IDENTITY)
                         val id: Long = 0,
                         val OrderID: Long = 0,
                         val OrderDate: LocalDate = LocalDate.MIN,
                         val ProductId: String = "",
                         val ProductName: String = "",
                         val Quantity: BigDecimal = BigDecimal.ZERO,
                         val Unit: String = "")
