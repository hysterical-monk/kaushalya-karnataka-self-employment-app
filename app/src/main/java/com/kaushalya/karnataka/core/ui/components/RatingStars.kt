package com.kaushalya.karnataka.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.floor

@Composable
fun RatingStars(
    rating: Float,
    count: Int? = null,
    modifier: Modifier = Modifier
) {
    val full = floor(rating).toInt()
    val half = (rating - full) >= 0.5f
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        repeat(5) { i ->
            val tint = MaterialTheme.colorScheme.primary
            when {
                i < full -> Icon(Icons.Filled.Star, null, tint = tint, modifier = Modifier.size(16.dp))
                i == full && half -> Icon(Icons.Filled.StarHalf, null, tint = tint, modifier = Modifier.size(16.dp))
                else -> Icon(Icons.Outlined.Star, null, tint = tint, modifier = Modifier.size(16.dp))
            }
        }
        if (count != null) {
            Text(
                text = " ${"%.1f".format(rating)} ($count)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
