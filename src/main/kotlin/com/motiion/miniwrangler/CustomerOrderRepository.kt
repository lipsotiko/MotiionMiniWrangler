package com.motiion.miniwrangler

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerOrderRepository : JpaRepository<CustomerOrder, Long>
