package com.kaushalya.karnataka.domain.repository

import com.kaushalya.karnataka.domain.model.JobPost
import kotlinx.coroutines.flow.Flow

interface JobPostRepository {
    fun observeOpenJobs(town: String?, categoryIds: List<String>): Flow<List<JobPost>>
    fun observeMyJobs(customerId: String): Flow<List<JobPost>>
    suspend fun postJob(post: JobPost): Result<String>
    suspend fun closeJob(jobId: String): Result<Unit>
}
