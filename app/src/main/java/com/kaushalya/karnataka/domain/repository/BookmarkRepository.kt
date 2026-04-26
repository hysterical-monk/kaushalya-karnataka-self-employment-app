package com.kaushalya.karnataka.domain.repository

import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    fun observeBookmarkedIds(customerId: String): Flow<Set<String>>
    suspend fun toggle(customerId: String, workerId: String, currentlyBookmarked: Boolean): Result<Unit>
}
