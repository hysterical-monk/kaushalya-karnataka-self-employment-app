package com.kaushalya.karnataka.domain.repository

import com.kaushalya.karnataka.domain.model.HireRequest
import com.kaushalya.karnataka.domain.model.HireStatus
import kotlinx.coroutines.flow.Flow

interface HireRepository {

    fun observeIncoming(workerId: String): Flow<List<HireRequest>>

    fun observeOutgoing(customerId: String): Flow<List<HireRequest>>

    suspend fun sendHireRequest(request: HireRequest): Result<String>

    suspend fun updateStatus(requestId: String, status: HireStatus): Result<Unit>
}
