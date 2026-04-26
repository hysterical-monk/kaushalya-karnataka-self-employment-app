package com.kaushalya.karnataka.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.kaushalya.karnataka.domain.model.Review
import org.junit.Test

class ComputeAverageRatingUseCaseTest {

    private val useCase = ComputeAverageRatingUseCase()

    @Test
    fun `empty list returns zero`() {
        assertThat(useCase(emptyList())).isEqualTo(0f)
    }

    @Test
    fun `single five-star review returns 5_0`() {
        val reviews = listOf(review(5))
        assertThat(useCase(reviews)).isEqualTo(5.0f)
    }

    @Test
    fun `mixed reviews are averaged and rounded to 1 decimal`() {
        val reviews = listOf(review(5), review(4), review(3))
        // (5+4+3) / 3 = 4.0
        assertThat(useCase(reviews)).isEqualTo(4.0f)
    }

    @Test
    fun `non-integer average rounds to one decimal place`() {
        val reviews = listOf(review(5), review(4))
        // (5+4) / 2 = 4.5
        assertThat(useCase(reviews)).isEqualTo(4.5f)
    }

    @Test
    fun `lots of reviews compute correctly`() {
        val reviews = (1..10).map { review(if (it % 2 == 0) 5 else 4) }
        // 5 * 5 + 5 * 4 = 45 / 10 = 4.5
        assertThat(useCase(reviews)).isEqualTo(4.5f)
    }

    private fun review(stars: Int) = Review(
        id = "r${stars}",
        workerId = "w1",
        customerId = "c$stars",
        customerName = "Customer $stars",
        stars = stars,
        thumbsUp = stars >= 4,
        text = "",
        createdAtMillis = 0L
    )
}
