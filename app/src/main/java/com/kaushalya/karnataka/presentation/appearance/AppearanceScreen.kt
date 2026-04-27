package com.kaushalya.karnataka.presentation.appearance

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.kaushalya.karnataka.core.prefs.ColorPalette
import com.kaushalya.karnataka.core.prefs.ThemeMode
import com.kaushalya.karnataka.core.prefs.ThemePrefs
import com.kaushalya.karnataka.core.prefs.ThemePrefsStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppearanceViewModel @Inject constructor(
    private val store: ThemePrefsStore
) : ViewModel() {
    fun load(): ThemePrefs = store.load()
    fun save(p: ThemePrefs) = store.save(p)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    onBack: () -> Unit,
    viewModel: AppearanceViewModel = hiltViewModel()
) {
    var prefs by remember { mutableStateOf(viewModel.load()) }
    fun update(p: ThemePrefs) { prefs = p; viewModel.save(p) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Appearance") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Theme", style = MaterialTheme.typography.titleMedium)
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                ThemeMode.entries.forEachIndexed { i, m ->
                    SegmentedButton(
                        selected = prefs.mode == m,
                        onClick = { update(prefs.copy(mode = m)) },
                        shape = SegmentedButtonDefaults.itemShape(index = i, count = ThemeMode.entries.size),
                        label = { Text(when (m) {
                            ThemeMode.SYSTEM -> "System"; ThemeMode.LIGHT -> "Light"; ThemeMode.DARK -> "Dark"
                        }) }
                    )
                }
            }

            HorizontalDivider()
            Text("Color palette", style = MaterialTheme.typography.titleMedium)
            ColorPalette.entries.forEach { p ->
                PaletteRow(
                    palette = p,
                    selected = !prefs.dynamic && prefs.palette == p,
                    onClick = { update(prefs.copy(palette = p, dynamic = false)) }
                )
            }

            HorizontalDivider()
            ListItem(
                headlineContent = { Text("Material You dynamic colors") },
                supportingContent = {
                    Text(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                            "Match colors from your wallpaper"
                        else "Needs Android 12 or newer"
                    )
                },
                trailingContent = {
                    Switch(
                        checked = prefs.dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
                        onCheckedChange = { update(prefs.copy(dynamic = it)) },
                        enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                    )
                }
            )
        }
    }
}

@Composable
private fun PaletteRow(palette: ColorPalette, selected: Boolean, onClick: () -> Unit) {
    val (primary, secondary, tertiary) = paletteSwatch(palette)
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Swatch(primary)
            Swatch(secondary)
            Swatch(tertiary)
        }
        Text(palette.displayName, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        if (selected) Icon(Icons.Filled.Check, null, tint = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun Swatch(color: Color) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
    )
}

private fun paletteSwatch(p: ColorPalette): Triple<Color, Color, Color> = when (p) {
    ColorPalette.SAFFRON -> Triple(Color(0xFFB8860B), Color(0xFF2E7D32), Color(0xFFD32F2F))
    ColorPalette.INDIGO -> Triple(Color(0xFF3F51B5), Color(0xFFFF9800), Color(0xFF7B1FA2))
    ColorPalette.FOREST -> Triple(Color(0xFF2E7D32), Color(0xFF8D6E63), Color(0xFFCDDC39))
    ColorPalette.CORAL -> Triple(Color(0xFFE91E63), Color(0xFF00897B), Color(0xFFFFB300))
}
