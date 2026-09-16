package com.example.ui.seller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.MarketViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SellerEarningsTab(viewModel: MarketViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allOrders by viewModel.getOrdersForSeller().collectAsState(initial = emptyList())
    val payoutRequests by viewModel.getPayoutRequestsFlow().collectAsState(initial = emptyList())

    val deliveredOrders = allOrders.filter { it.orderStatus == "Delivered" || it.paymentStatus == "COD Collected" || it.paymentStatus == "Paid" }
    
    val grossSales = deliveredOrders.sumOf { it.totalAmount }
    val commission = grossSales * 0.08 // 8% configurable platform commission
    val shippingDeduction = deliveredOrders.size * 50.0 // standard shipping fee deduction
    
    val netEarnings = grossSales - commission - shippingDeduction
    val requestedPayoutsTotal = payoutRequests.sumOf { it.amount } // Simplification: subtract all requests
    val availableBalance = maxOf(0.0, netEarnings * 0.85 - requestedPayoutsTotal)
    val pendingSettlement = netEarnings * 0.15

    var showPayoutModal by remember { mutableStateOf(false) }
    var payoutAmount by remember { mutableStateOf("") }
    var payoutMethod by remember { mutableStateOf("Bank Transfer") }
    var accountDetails by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var payoutMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Seller Wallet & Earnings",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Available Balance for Payout", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(text = "₹${String.format("%.2f", availableBalance)}", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Pending Settlement: ₹${String.format("%.2f", pendingSettlement)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showPayoutModal = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Request Payout")
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            EarningsMetricCard("Gross Sales", "₹${String.format("%.2f", grossSales)}", Modifier.weight(1f))
            EarningsMetricCard("Platform Fee (8%)", "₹${String.format("%.2f", commission)}", Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            EarningsMetricCard("Shipping Ded.", "₹${String.format("%.2f", shippingDeduction)}", Modifier.weight(1f))
            EarningsMetricCard("Net Earnings", "₹${String.format("%.2f", netEarnings)}", Modifier.weight(1f))
        }

        Text(text = "Settlement History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        
        if (payoutRequests.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No payout requests yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(payoutRequests) { req ->
                    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                    val dateString = sdf.format(Date(req.requestedAt))
                    
                    val statusColor = when(req.status) {
                        "Paid" -> MaterialTheme.colorScheme.tertiary
                        "Failed", "Reversed" -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.secondary
                    }
                    
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Payout ID: ${req.id}", fontWeight = FontWeight.Bold)
                                Text(req.status, color = statusColor, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Amount: ₹${String.format("%.2f", req.amount)} | Date: $dateString", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Method: ${req.payoutMethod} (${req.accountDetails})", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }

    if (showPayoutModal) {
        AlertDialog(
            onDismissRequest = { if (!isProcessing) showPayoutModal = false },
            title = { Text("Request Payout") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (payoutMessage != null) {
                        Text(
                            text = payoutMessage!!,
                            color = if (payoutMessage!!.contains("Failed") || payoutMessage!!.contains("Insufficient")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Text("Available to withdraw: ₹${String.format("%.2f", availableBalance)}", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = payoutAmount,
                        onValueChange = { payoutAmount = it },
                        label = { Text("Amount (₹)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = payoutMethod,
                        onValueChange = { payoutMethod = it },
                        label = { Text("Payout Method (e.g., Bank Transfer, UPI)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = accountDetails,
                        onValueChange = { accountDetails = it },
                        label = { Text("Account Details (A/C No & IFSC or UPI ID)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    enabled = !isProcessing,
                    onClick = {
                        val amount = payoutAmount.toDoubleOrNull()
                        if (amount == null || amount <= 0 || amount > availableBalance) {
                            payoutMessage = "Invalid amount or insufficient balance."
                            return@Button
                        }
                        if (accountDetails.isBlank()) {
                            payoutMessage = "Please provide account details."
                            return@Button
                        }
                        
                        isProcessing = true
                        payoutMessage = null
                        viewModel.requestPayout(amount, payoutMethod, accountDetails) { success, msg ->
                            isProcessing = false
                            if (success) {
                                showPayoutModal = false
                            } else {
                                payoutMessage = msg
                            }
                        }
                    }
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Submit Request")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showPayoutModal = false }, enabled = !isProcessing) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EarningsMetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
