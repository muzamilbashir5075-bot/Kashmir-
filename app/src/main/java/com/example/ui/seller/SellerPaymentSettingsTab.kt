package com.example.ui.seller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.MarketViewModel
import com.example.ui.components.ContactSupportSection

@Composable
fun SellerPaymentSettingsTab(viewModel: MarketViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val sellerId = currentUser?.id ?: 0L
    val profileFlow = viewModel.getSellerProfileFlow(sellerId)
    val profile by profileFlow.collectAsState(initial = null)

    var onlineEnabled by remember(profile) { mutableStateOf(profile?.onlinePaymentEnabled ?: true) }
    var codEnabled by remember(profile) { mutableStateOf(profile?.codEnabled ?: true) }
    var savedMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Settings & Support",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Manage your shop preferences and contact support.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Online Payment Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Online Payment (UPI / Cards / Net Banking)", fontWeight = FontWeight.Bold)
                        Text("Accept secure online transactions through payment gateway", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = onlineEnabled,
                        onCheckedChange = { onlineEnabled = it }
                    )
                }

                Divider()

                // COD Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Cash on Delivery (COD)", fontWeight = FontWeight.Bold)
                        Text("Allow customers to pay when order is delivered. (Controlled independently by you)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = codEnabled,
                        onCheckedChange = { codEnabled = it }
                    )
                }
            }
        }

        if (savedMessage.isNotEmpty()) {
            Text(text = savedMessage, color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.bodyMedium)
        }

        Button(
            onClick = {
                viewModel.updateSellerPaymentSettings(sellerId, onlineEnabled, codEnabled)
                savedMessage = "Payment settings successfully updated!"
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Save Payment Settings", fontWeight = FontWeight.Bold)
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        ContactSupportSection()
        Spacer(modifier = Modifier.height(12.dp))
        com.example.ui.components.AboutAppSection()
        Spacer(modifier = Modifier.height(16.dp))
    }
}
