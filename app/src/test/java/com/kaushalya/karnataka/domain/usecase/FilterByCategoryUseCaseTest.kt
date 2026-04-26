package com.kaushalya.karnataka.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.kaushalya.karnataka.domain.model.Worker
import org.junit.Test

class FilterByCategoryUseCaseTest {

    private val useCase = FilterByCategoryUseCase()

    private val ravi = worker("ravi", "Ravi", listOf("electrician"), locality = "Jayanagar")
    private val sita = worker("sita", "Sita", listOf("plumber", "electrician"), locality = "Indiranagar")
    private val anil = worker("anil", "Anil", listOf("carpenter"), locality = "Yelahanka")

    @Test
    fun `null category and empty query returns all`() {
        val r = useCase(listOf(ravi, sita, anil), query = "", categoryId = null)
        assertThat(r).containsExactly(ravi, sita, anil)
    }

    @Test
    fun `category filter narrows down`() {
        val r = useCase(listOf(ravi, sita, anil), query = "", categoryId = "electrician")
        assertThat(r).containsExactly(ravi, sita)
    }

    @Test
    fun `query matches name case-insensitively`() {
        val r = useCase(listOf(ravi, sita, anil), query = "RAVI", categoryId = null)
        assertThat(r).containsExactly(ravi)
    }

    @Test
    fun `query matches locality`() {
        val r = useCase(listOf(ravi, sita, anil), query = "yelahanka", categoryId = null)
        assertThat(r).containsExactly(anil)
    }

    @Test
    fun `query matches category text`() {
        val r = useCase(listOf(ravi, sita, anil), query = "carp", categoryId = null)
        assertThat(r).containsExactly(anil)
    }

    @Test
    fun `category and query intersect`() {
        val r = useCase(listOf(ravi, sita, anil), query = "indira", categoryId = "electrician")
        assertThat(r).containsExactly(sita)
    }

    private fun worker(id: String, name: String, cats: List<String>, locality: String) = Worker(
        id = id, displayName = name, phone = "", bio = "", photoUrl = null,
        town = "Bengaluru", locality = locality, categories = cats,
        isAvailable = true, averageRating = 4.5f, ratingCount = 10, thumbsUpCount = 5
    )
}
