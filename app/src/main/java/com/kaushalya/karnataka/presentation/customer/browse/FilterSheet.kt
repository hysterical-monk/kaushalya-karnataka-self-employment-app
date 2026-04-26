package com.kaushalya.karnataka.presentation.customer.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(
    initial: BrowseFilters,
    onApply: (BrowseFilters) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var minRating by remember { mutableStateOf(initial.minRating) }
    var onlyAvailable by remember { mutableStateOf(initial.onlyAvailable) }
    var maxPrice by remember { mutableStateOf((initial.maxPriceInr ?: 5000).toFloat()) }
    var priceCapped by remember { mutableStateOf(initial.maxPriceInr != null) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Filter workers", style = MaterialTheme.typography.titleLarge)

            // Min rating
            Column {
                Text("Minimum rating: ${"%.1f".format(minRating)} ★", style = MaterialTheme.typography.bodyMedium)
                Slider(value = minRating, onValueChange = { minRating = it }, valueRange = 0f..5f, steps = 9)
            }

            // Availability
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Show only available", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                Switch(checked = onlyAvailable, onCheckedChange = { onlyAvailable = it })
            }

            // Price cap
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Cap maximum price", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                Switch(checked = priceCapped, onCheckedChange = { priceCapped = it })
            }
            if (priceCapped) {
                Column {
                    Text("Up to ₹${maxPrice.toInt()}", style = MaterialTheme.typography.bodyMedium)
                    Slider(value = maxPrice, onValueChange = { maxPrice = it }, valueRange = 100f..5000f, steps = 48)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                TextButton(
                    onClick = {
                        onApply(BrowseFilters())
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Reset") }
                Button(
                    onClick = {
                        onApply(
                            BrowseFilters(
                                minRating = minRating,
                                onlyAvailable = onlyAvailable,
                                maxPriceInr = if (priceCapped) maxPrice.toInt() else null
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Apply") }
            }
        }
    }
}
