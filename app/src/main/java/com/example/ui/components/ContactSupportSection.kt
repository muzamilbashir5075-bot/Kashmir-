package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ContactSupportSection(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val goldColor = MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Contact Support",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Need help? Reach out to our support team for any queries.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))

            // Call
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        try {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:+919682121357")
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Ignore
                        }
                    }
                    .padding(vertical = 8.dp)
            ) {
                Icon(Icons.Default.Call, contentDescription = "Call", tint = goldColor)
                Column {
                    Text("Call Us", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                    Text("+91 9682121357", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Mail
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        try {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:muzamilbashir5074@gmail.com")
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Ignore
                        }
                    }
                    .padding(vertical = 8.dp)
            ) {
                Icon(Icons.Default.Email, contentDescription = "Email", tint = goldColor)
                Column {
                    Text("Email Support", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                    Text("muzamilbashir5074@gmail.com", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // WhatsApp
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://wa.me/919682121357")
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Ignore
                        }
                    }
                    .padding(vertical = 8.dp)
            ) {
                Text("💬", style = MaterialTheme.typography.titleLarge) // Simple emoji for WhatsApp
                Column {
                    Text("WhatsApp", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                    Text("+91 9682121357", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
