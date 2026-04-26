package com.kaushalya.karnataka.domain.usecase

import com.kaushalya.karnataka.domain.model.Review
import javax.inject.Inject
import kotlin.math.roundToInt

class ComputeAverageRatingUseCase @Inject constructor() {

    operator fun invoke(reviews: List<Review>): Float {
        if (reviews.isEmpty()) return 0f
        val sum = reviews.sumOf { it.stars }
        val avg = sum.toFloat() / reviews.size.toFloat()
        return (avg * 10).roundToInt() / 10f
    }
}
