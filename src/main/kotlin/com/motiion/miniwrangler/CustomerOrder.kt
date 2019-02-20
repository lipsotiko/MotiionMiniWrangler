package com.motiion.miniwrangler

import java.time.LocalDate
import javax.persistence.*

@Entity
data class CustomerOrder(@Id
                         @GeneratedValue(strategy = GenerationType.IDENTITY)
                         val id: Long,
                         val OrderID: Long,
                         val OrderDate: LocalDate)
