package com.example.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AboutAppSection(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val goldColor = MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = "About", tint = goldColor)
                    Text(
                        text = "About Kashmir Shop Market",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (expanded) {
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                
                Text(
                    text = "Kashmir Shop Market is a digital marketplace created to connect customers with local shops, sellers, and businesses across Kashmir.\n\n" +
                            "Our goal is to make local shopping easier, faster, and more convenient by bringing a wide range of products and local businesses together on one platform.\n\n" +
                            "From fashion and traditional Kashmiri products to daily-use items, accessories, home products, and more, Kashmir Shop Market helps customers discover products from local sellers in one place.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "Our Mission",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Our mission is to support local businesses, help Kashmiri sellers reach more customers, and create a simple and reliable online shopping experience for everyone.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "Why Kashmir Shop Market?",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val points = listOf(
                    "Discover products from local sellers",
                    "Support Kashmir's local businesses",
                    "Easy product browsing",
                    "Seller-friendly marketplace",
                    "Simple and convenient shopping experience",
                    "A platform built with local businesses in mind"
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    points.forEach { point ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                            Text("•", style = MaterialTheme.typography.bodyMedium, color = goldColor, fontWeight = FontWeight.Bold)
                            Text(point, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Surface(
                    color = goldColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text(
                        text = "Kashmir Shop Market — Discover Local. Shop Smart. Support Kashmir.",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = goldColor,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}
