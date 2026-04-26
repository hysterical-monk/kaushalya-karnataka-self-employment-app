package com.kaushalya.karnataka.domain.usecase

import com.kaushalya.karnataka.domain.model.Worker
import javax.inject.Inject

class FilterByCategoryUseCase @Inject constructor() {

    operator fun invoke(workers: List<Worker>, query: String, categoryId: String?): List<Worker> {
        val q = query.trim().lowercase()
        return workers.filter { w ->
            val matchesCat = categoryId == null || w.categories.any { it.equals(categoryId, ignoreCase = true) }
            val matchesQ = q.isBlank() ||
                w.displayName.lowercase().contains(q) ||
                w.locality.lowercase().contains(q) ||
                w.categories.any { it.lowercase().contains(q) }
            matchesCat && matchesQ
        }
    }
}
