package com.kaushalya.karnataka

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.kaushalya.karnataka.core.prefs.ThemePrefs
import com.kaushalya.karnataka.core.prefs.ThemePrefsStore
import com.kaushalya.karnataka.core.ui.theme.KaushalyaTheme
import com.kaushalya.karnataka.presentation.nav.AppNavGraph
import com.kaushalya.karnataka.presentation.nav.AppRootViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

private const val SPLASH_HOLD_MS = 1400L
private const val SPLASH_FADE_MS = 450

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @javax.inject.Inject lateinit var themePrefsStore: ThemePrefsStore

    override fun onCreate(savedInstanceState: Bundle?) {
        // Exit the system splash immediately so our full-image Compose splash is what the user sees.
        installSplashScreen().setKeepOnScreenCondition { false }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themePrefs by themePrefsStore.observe().collectAsStateWithLifecycle(initialValue = ThemePrefs())
            KaushalyaTheme(prefs = themePrefs) {
                var showSplash by remember { mutableStateOf(true) }
                LaunchedEffect(Unit) {
                    delay(SPLASH_HOLD_MS)
                    showSplash = false
                }
                Crossfade(
                    targetState = showSplash,
                    animationSpec = tween(SPLASH_FADE_MS),
                    label = "splash"
                ) { isSplash ->
                    if (isSplash) {
                        SplashContent()
                    } else {
                        Surface(
                            modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            val navController = rememberNavController()
                            val rootViewModel: AppRootViewModel = hiltViewModel()
                            AppNavGraph(
                                navController = navController,
                                startDestination = rootViewModel.startDestination
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SplashContent() {
    var enter by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { enter = true }
    val scale by animateFloatAsState(
        targetValue = if (enter) 1f else 0.85f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "splash-scale"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFC72C)), // Karnataka flag yellow
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.splash_full),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .aspectRatio(1f)
                .scale(scale)
        )
    }
}
