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
import com.example.data.OrderEntity
import com.example.ui.MarketViewModel

@Composable
fun SellerOrdersTab(viewModel: MarketViewModel) {
    val orders by viewModel.getOrdersForSeller().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Customer Orders (${orders.size})",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders) { order ->
                SellerOrderCard(order = order, onStatusChange = { newStatus ->
                    viewModel.updateOrderStatus(order.id, newStatus)
                })
            }
        }
    }
}

@Composable
fun SellerOrderCard(order: OrderEntity, onStatusChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val statuses = listOf("Order Placed", "Seller Confirmed", "Packed", "Shipped", "Out for Delivery", "Delivered", "Cancelled")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Order #${order.orderNumber}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(text = order.orderStatus, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
            }
            Text(text = "Buyer: ${order.fullName} (${order.phone})", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Address: ${order.address}, ${order.district}, ${order.pinCode}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "Amount: ₹${order.totalAmount} | Payment: ${order.paymentMethod} (${order.paymentStatus})", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Update Status:", style = MaterialTheme.typography.labelMedium)
                Box {
                    Button(onClick = { expanded = true }) {
                        Text(order.orderStatus)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        statuses.forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st) },
                                onClick = {
                                    expanded = false
                                    onStatusChange(st)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
