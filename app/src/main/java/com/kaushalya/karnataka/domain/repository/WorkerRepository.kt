package com.kaushalya.karnataka.domain.repository

import com.kaushalya.karnataka.domain.model.PortfolioPhoto
import com.kaushalya.karnataka.domain.model.ServiceCard
import com.kaushalya.karnataka.domain.model.Worker
import kotlinx.coroutines.flow.Flow

interface WorkerRepository {

    fun observeWorkers(category: String?): Flow<List<Worker>>

    fun observeWorker(workerId: String): Flow<Worker?>

    fun observeServices(workerId: String): Flow<List<ServiceCard>>

    fun observePortfolio(workerId: String): Flow<List<PortfolioPhoto>>

    suspend fun upsertWorkerProfile(worker: Worker): Result<Unit>

    suspend fun setAvailability(workerId: String, available: Boolean): Result<Unit>

    suspend fun upsertService(service: ServiceCard): Result<Unit>

    suspend fun deleteService(workerId: String, serviceId: String): Result<Unit>

    suspend fun addPortfolioPhoto(workerId: String, localImageUri: String, caption: String): Result<Unit>

    suspend fun deletePortfolioPhoto(workerId: String, photoId: String): Result<Unit>
}
