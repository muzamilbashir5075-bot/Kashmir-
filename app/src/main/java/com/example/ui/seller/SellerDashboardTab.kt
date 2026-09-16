package com.example.ui.seller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.MarketViewModel

@Composable
fun SellerDashboardTab(viewModel: MarketViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allProducts by viewModel.adminProducts.collectAsState()
    val allOrders by viewModel.adminOrders.collectAsState()

    val sellerProducts = allProducts.filter { it.sellerId == currentUser?.id }
    val sellerOrders = allOrders.filter { it.sellerId == currentUser?.id }
    val totalSalesAmount = sellerOrders.filter { it.orderStatus == "Delivered" }.sumOf { it.totalAmount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Seller Dashboard",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Welcome back, ${currentUser?.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MetricCard(title = "Total Products", value = "${sellerProducts.size}", modifier = Modifier.weight(1f))
                MetricCard(title = "Total Orders", value = "${sellerOrders.size}", modifier = Modifier.weight(1f))
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MetricCard(title = "Delivered Sales", value = "₹$totalSalesAmount", modifier = Modifier.weight(1f))
                MetricCard(title = "Shop Rating", value = "4.9 ★", modifier = Modifier.weight(1f))
            }
        }

        item {
            Button(
                onClick = { viewModel.switchRoleToBuyer() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Switch Back to Buyer Mode")
            }
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
        }
    }
}
