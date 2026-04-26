package com.kaushalya.karnataka.core.di

import com.kaushalya.karnataka.data.auth.FirebaseAuthRepository
import com.kaushalya.karnataka.data.bookmark.BookmarkRepositoryImpl
import com.kaushalya.karnataka.data.chat.ChatRepositoryImpl
import com.kaushalya.karnataka.data.hire.HireRepositoryImpl
import com.kaushalya.karnataka.data.jobpost.JobPostRepositoryImpl
import com.kaushalya.karnataka.data.report.ReportRepositoryImpl
import com.kaushalya.karnataka.data.review.ReviewRepositoryImpl
import com.kaushalya.karnataka.data.worker.WorkerRepositoryImpl
import com.kaushalya.karnataka.domain.repository.AuthRepository
import com.kaushalya.karnataka.domain.repository.BookmarkRepository
import com.kaushalya.karnataka.domain.repository.ChatRepository
import com.kaushalya.karnataka.domain.repository.HireRepository
import com.kaushalya.karnataka.domain.repository.JobPostRepository
import com.kaushalya.karnataka.domain.repository.ReportRepository
import com.kaushalya.karnataka.domain.repository.ReviewRepository
import com.kaushalya.karnataka.domain.repository.WorkerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: FirebaseAuthRepository): AuthRepository

    @Binds @Singleton
    abstract fun bindWorkerRepository(impl: WorkerRepositoryImpl): WorkerRepository

    @Binds @Singleton
    abstract fun bindReviewRepository(impl: ReviewRepositoryImpl): ReviewRepository

    @Binds @Singleton
    abstract fun bindHireRepository(impl: HireRepositoryImpl): HireRepository

    @Binds @Singleton
    abstract fun bindBookmarkRepository(impl: BookmarkRepositoryImpl): BookmarkRepository

    @Binds @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

    @Binds @Singleton
    abstract fun bindReportRepository(impl: ReportRepositoryImpl): ReportRepository

    @Binds @Singleton
    abstract fun bindJobPostRepository(impl: JobPostRepositoryImpl): JobPostRepository
}
