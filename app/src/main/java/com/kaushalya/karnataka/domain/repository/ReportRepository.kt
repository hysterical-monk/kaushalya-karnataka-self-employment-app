package com.kaushalya.karnataka.domain.repository

interface ReportRepository {
    suspend fun report(reporterId: String, targetType: String, targetId: String, reason: String): Result<Unit>
}
