package com.kaushalya.karnataka.presentation.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

private data class Slide(
    val icon: ImageVector,
    val title: String,
    val body: String
)

private val SLIDES = listOf(
    Slide(
        icon = Icons.Filled.PersonSearch,
        title = "Find skilled workers near you",
        body = "Electricians, plumbers, carpenters, tailors and more — all from your locality, with verified ratings."
    ),
    Slide(
        icon = Icons.Filled.Build,
        title = "Earn from your skills",
        body = "Workers — show off your work, set fair prices, and let neighbours hire you in a tap."
    ),
    Slide(
        icon = Icons.Filled.Stars,
        title = "Built on trust, by neighbours",
        body = "Every worker is rated by real customers in your community. Real reviews. Real service."
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { SLIDES.size })
    val scope = rememberCoroutineScope()

    fun complete() {
        viewModel.markCompleted()
        onFinish()
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {

        // Top bar — Skip on the right (hidden on last slide)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (pagerState.currentPage < SLIDES.lastIndex) {
                TextButton(onClick = ::complete) { Text("Skip") }
            } else {
                Spacer(Modifier.height(48.dp))
            }
        }

        // Slides
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) { page ->
            SlideContent(SLIDES[page])
        }

        // Page indicators
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(SLIDES.size) { i ->
                val width by animateDpAsState(
                    targetValue = if (pagerState.currentPage == i) 24.dp else 8.dp,
                    animationSpec = tween(durationMillis = 300),
                    label = "indicator-$i"
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(8.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == i) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                )
            }
        }

        // Action button: Next or Get Started
        Button(
            onClick = {
                if (pagerState.currentPage < SLIDES.lastIndex) {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                } else {
                    complete()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (pagerState.currentPage < SLIDES.lastIndex) "Next" else "Get started")
        }
    }
}

@Composable
private fun SlideContent(slide: Slide) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = slide.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(96.dp)
            )
        }
        Spacer(Modifier.height(40.dp))
        Text(
            text = slide.title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = slide.body,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
