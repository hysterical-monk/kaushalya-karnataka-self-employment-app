package com.kaushalya.karnataka.presentation.customer.workerdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaushalya.karnataka.core.analytics.AnalyticsEvent
import com.kaushalya.karnataka.core.analytics.AnalyticsTracker
import com.kaushalya.karnataka.data.FirestorePaths
import com.kaushalya.karnataka.domain.model.HireRequest
import com.kaushalya.karnataka.domain.model.HireStatus
import com.kaushalya.karnataka.domain.model.PortfolioPhoto
import com.kaushalya.karnataka.domain.model.Review
import com.kaushalya.karnataka.domain.model.ServiceCard
import com.kaushalya.karnataka.domain.model.Worker
import com.kaushalya.karnataka.domain.repository.HireRepository
import com.kaushalya.karnataka.domain.repository.ReviewRepository
import com.kaushalya.karnataka.domain.repository.WorkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class WorkerDetailState(
    val worker: Worker? = null,
    val services: List<ServiceCard> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val portfolio: List<PortfolioPhoto> = emptyList(),
    val sending: Boolean = false,
    val toast: String? = null
)

@HiltViewModel
class WorkerDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val workerRepository: WorkerRepository,
    private val reviewRepository: ReviewRepository,
    private val hireRepository: HireRepository,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val analytics: AnalyticsTracker
) : ViewModel() {

    private val workerId: String = savedStateHandle.get<String>("workerId").orEmpty()

    private val toast = MutableStateFlow<String?>(null)
    private val sending = MutableStateFlow(false)

    val state: StateFlow<WorkerDetailState> = combine(
        workerRepository.observeWorker(workerId),
        workerRepository.observeServices(workerId),
        reviewRepository.observeReviews(workerId),
        workerRepository.observePortfolio(workerId),
        toast
    ) { w, s, r, p, t ->
        WorkerDetailState(worker = w, services = s, reviews = r, portfolio = p, sending = sending.value, toast = t)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WorkerDetailState())

    fun hire(message: String, service: ServiceCard?) {
        val customer = auth.currentUser ?: return
        sending.value = true
        viewModelScope.launch {
            val customerName = firestore.collection(FirestorePaths.USERS).document(customer.uid).get().await()
                .getString("displayName").orEmpty()
            val request = HireRequest(
                id = "",
                customerId = customer.uid,
                customerName = customerName.ifBlank { "Customer" },
                workerId = workerId,
                serviceId = service?.id,
                serviceTitle = service?.title,
                message = message.ifBlank { "Interested in your services" },
                status = HireStatus.PENDING,
                createdAtMillis = System.currentTimeMillis()
            )
            hireRepository.sendHireRequest(request)
                .onSuccess {
                    analytics.log(AnalyticsEvent.HireRequestSent(hasService = service != null))
                    toast.value = "Request sent"
                }
                .onFailure { toast.value = "Could not send: ${it.message}" }
            sending.value = false
        }
    }

    fun postReview(stars: Int, text: String) {
        val customer = auth.currentUser ?: return
        viewModelScope.launch {
            val customerName = firestore.collection(FirestorePaths.USERS).document(customer.uid).get().await()
                .getString("displayName").orEmpty()
            val review = Review(
                id = customer.uid,
                workerId = workerId,
                customerId = customer.uid,
                customerName = customerName.ifBlank { "Customer" },
                stars = stars,
                thumbsUp = stars >= 4,
                text = text,
                createdAtMillis = System.currentTimeMillis()
            )
            reviewRepository.postReview(review)
                .onSuccess {
                    analytics.log(AnalyticsEvent.ReviewPosted(stars = stars))
                    toast.value = "Review posted"
                }
                .onFailure { toast.value = "Could not post: ${it.message}" }
        }
    }

    fun consumeToast() { toast.value = null }
}
