package com.kaushalya.karnataka.presentation.worker.profile

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kaushalya.karnataka.MainDispatcherRule
import com.kaushalya.karnataka.domain.model.Category
import com.kaushalya.karnataka.domain.model.PortfolioPhoto
import com.kaushalya.karnataka.domain.model.ServiceCard
import com.kaushalya.karnataka.domain.model.Worker
import com.kaushalya.karnataka.domain.repository.WorkerRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkerProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun fakeWorker(name: String = "Ravi", cats: List<String> = listOf("electrician")) = Worker(
        id = "uid-1",
        displayName = name,
        phone = "+919876543210",
        bio = "10 years experience",
        photoUrl = null,
        town = "Mysuru",
        locality = "Vijayanagar",
        categories = cats,
        isAvailable = true,
        averageRating = 4.5f,
        ratingCount = 12,
        thumbsUpCount = 9
    )

    private fun makeRepo(workerFlow: Flow<Worker?>): WorkerRepository = object : WorkerRepository {
        override fun observeWorkers(category: String?): Flow<List<Worker>> = flowOf(emptyList())
        override fun observeWorker(workerId: String): Flow<Worker?> = workerFlow
        override fun observeServices(workerId: String): Flow<List<ServiceCard>> = flowOf(emptyList())
        override fun observePortfolio(workerId: String): Flow<List<PortfolioPhoto>> = flowOf(emptyList())
        override suspend fun upsertWorkerProfile(worker: Worker): Result<Unit> = Result.success(Unit)
        override suspend fun setAvailability(workerId: String, available: Boolean): Result<Unit> = Result.success(Unit)
        override suspend fun upsertService(service: ServiceCard): Result<Unit> = Result.success(Unit)
        override suspend fun deleteService(workerId: String, serviceId: String): Result<Unit> = Result.success(Unit)
        override suspend fun addPortfolioPhoto(workerId: String, localImageUri: String, caption: String): Result<Unit> = Result.success(Unit)
        override suspend fun deletePortfolioPhoto(workerId: String, photoId: String): Result<Unit> = Result.success(Unit)
    }

    private fun makeAuth(uid: String = "uid-1"): FirebaseAuth = mockk(relaxed = true) {
        every { currentUser } returns mockk<FirebaseUser>(relaxed = true) {
            every { this@mockk.uid } returns uid
        }
    }

    @Test
    fun `hydrates state from observed worker`() = runTest {
        val flow = MutableStateFlow<Worker?>(fakeWorker())
        val vm = WorkerProfileViewModel(makeRepo(flow), makeAuth())
        advanceUntilIdle()

        vm.state.test {
            val s = awaitItem()
            assertThat(s.displayName).isEqualTo("Ravi")
            assertThat(s.locality).isEqualTo("Vijayanagar")
            assertThat(s.categories.map { it.id }).containsExactly("electrician")
            assertThat(s.available).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleCategory adds and removes`() = runTest {
        val flow = MutableStateFlow<Worker?>(fakeWorker())
        val vm = WorkerProfileViewModel(makeRepo(flow), makeAuth())
        advanceUntilIdle()

        vm.toggleCategory(Category.PLUMBER)
        assertThat(vm.state.value.categories.map { it.id }).containsExactly("electrician", "plumber")

        vm.toggleCategory(Category.ELECTRICIAN)
        assertThat(vm.state.value.categories.map { it.id }).containsExactly("plumber")
    }

    @Test
    fun `save with blank name produces error`() = runTest {
        val flow = MutableStateFlow<Worker?>(null)
        val vm = WorkerProfileViewModel(makeRepo(flow), makeAuth())
        vm.update { it.copy(displayName = "  ") }

        vm.save()
        advanceUntilIdle()

        assertThat(vm.state.value.error).isEqualTo("Name is required")
        assertThat(vm.state.value.saved).isFalse()
    }

    @Test
    fun `save with valid data marks saved`() = runTest {
        val flow = MutableStateFlow<Worker?>(fakeWorker())
        val vm = WorkerProfileViewModel(makeRepo(flow), makeAuth())
        advanceUntilIdle()

        vm.update { it.copy(displayName = "Ravi Kumar", town = "Mysuru") }
        vm.save()
        advanceUntilIdle()

        assertThat(vm.state.value.saving).isFalse()
        assertThat(vm.state.value.saved).isTrue()
        assertThat(vm.state.value.error).isNull()
    }
}
