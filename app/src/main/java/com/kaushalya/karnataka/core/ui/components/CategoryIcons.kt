package com.kaushalya.karnataka.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Carpenter
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Yard
import androidx.compose.ui.graphics.vector.ImageVector
import com.kaushalya.karnataka.domain.model.Category

fun Category.icon(): ImageVector = when (this) {
    Category.ELECTRICIAN -> Icons.Filled.ElectricBolt
    Category.PLUMBER -> Icons.Filled.Plumbing
    Category.CARPENTER -> Icons.Filled.Carpenter
    Category.PAINTER -> Icons.Filled.Brush
    Category.MASON -> Icons.Filled.Construction
    Category.MECHANIC -> Icons.Filled.Build
    Category.APPLIANCE_REPAIR -> Icons.Filled.HomeRepairService
    Category.TAILOR -> Icons.Filled.ContentCut
    Category.GARDENER -> Icons.Filled.Yard
    Category.DRIVER -> Icons.Filled.DirectionsCar
    Category.OTHER -> Icons.Filled.MoreHoriz
}
