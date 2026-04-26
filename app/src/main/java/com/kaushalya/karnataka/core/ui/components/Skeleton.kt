package com.kaushalya.karnataka.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** A shimmering placeholder modifier — paints a moving gradient over a rounded shape. */
fun Modifier.shimmer(cornerRadiusDp: Dp = 8.dp): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translate by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer-translate"
    )
    val baseColor = MaterialTheme.colorScheme.surfaceVariant
    val highlight = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.6f)
    drawBehind {
        val width = size.width
        val r = androidx.compose.ui.geometry.CornerRadius(cornerRadiusDp.toPx(), cornerRadiusDp.toPx())
        drawRoundRect(color = baseColor, cornerRadius = r)
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color.Transparent, highlight, Color.Transparent),
                start = androidx.compose.ui.geometry.Offset(width * translate, 0f),
                end = androidx.compose.ui.geometry.Offset(width * (translate + 0.6f), 0f)
            ),
            cornerRadius = r
        )
    }
}

@Composable
fun SkeletonLine(modifier: Modifier = Modifier, height: Dp = 14.dp, widthFraction: Float = 1f) {
    Spacer(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .shimmer(cornerRadiusDp = 4.dp)
    )
}

@Composable
fun SkeletonCircle(size: Dp = 48.dp, modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .shimmer(cornerRadiusDp = size / 2)
    )
}

@Composable
fun SkeletonBox(modifier: Modifier = Modifier, cornerRadius: Dp = 12.dp) {
    Spacer(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .shimmer(cornerRadiusDp = cornerRadius)
    )
}

/** Browse / Bookmarks list skeleton — 6 worker rows. */
@Composable
fun BrowseSkeleton(modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        items(count = 6) { _ ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SkeletonCircle(size = 56.dp)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SkeletonLine(widthFraction = 0.5f, height = 16.dp)
                    SkeletonLine(widthFraction = 0.7f, height = 12.dp)
                    SkeletonLine(widthFraction = 0.3f, height = 12.dp)
                }
            }
        }
    }
}

/** Home screen skeleton — featured banner + horizontal rows. */
@Composable
fun HomeSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SkeletonLine(widthFraction = 0.6f, height = 28.dp)
        SkeletonLine(widthFraction = 0.4f, height = 14.dp)
        SkeletonBox(modifier = Modifier.fillMaxWidth().height(180.dp), cornerRadius = 16.dp)
        SkeletonLine(widthFraction = 0.4f, height = 18.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(3) {
                SkeletonBox(modifier = Modifier.size(width = 160.dp, height = 200.dp), cornerRadius = 12.dp)
            }
        }
    }
}

