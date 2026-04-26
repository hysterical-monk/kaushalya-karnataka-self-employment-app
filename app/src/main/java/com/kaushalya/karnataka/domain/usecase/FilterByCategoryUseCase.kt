package com.kaushalya.karnataka.domain.usecase

import com.kaushalya.karnataka.domain.model.Worker
import javax.inject.Inject

class FilterByCategoryUseCase @Inject constructor() {

    operator fun invoke(workers: List<Worker>, query: String, categoryId: String?): List<Worker> =
        invoke(workers, query, categoryId, town = null, minRating = 0f, onlyAvailable = false, maxPrice = null)

    /**
     * Multi-axis filter. All non-null/non-default constraints AND together.
     */
    operator fun invoke(
        workers: List<Worker>,
        query: String,
        categoryId: String?,
        town: String?,
        minRating: Float,
        onlyAvailable: Boolean,
        maxPrice: Int?
    ): List<Worker> {
        val q = query.trim().lowercase()
        return workers.filter { w ->
            val matchesCat = categoryId == null || w.categories.any { it.equals(categoryId, ignoreCase = true) }
            val matchesQ = q.isBlank() ||
                w.displayName.lowercase().contains(q) ||
                w.locality.lowercase().contains(q) ||
                w.categories.any { it.lowercase().contains(q) }
            val matchesTown = town == null || w.town.equals(town, ignoreCase = true)
            val matchesRating = w.averageRating >= minRating
            val matchesAvail = !onlyAvailable || w.isAvailable
            val matchesPrice = maxPrice == null || (w.minPriceInr != null && w.minPriceInr <= maxPrice)
            matchesCat && matchesQ && matchesTown && matchesRating && matchesAvail && matchesPrice
        }
    }
}
