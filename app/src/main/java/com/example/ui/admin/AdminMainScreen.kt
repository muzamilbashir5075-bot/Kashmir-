package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.MarketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainScreen(viewModel: MarketViewModel) {
    val users by viewModel.adminUsers.collectAsState()
    val products by viewModel.adminProducts.collectAsState()
    val orders by viewModel.adminOrders.collectAsState()
    val reports by viewModel.adminReports.collectAsState()

    val buyersCount = users.count { it.role == "BUYER" }
    val sellersCount = users.count { it.role == "SELLER" }
    val onlinePaymentsCount = orders.count { it.paymentMethod == "Online" }
    val codCount = orders.count { it.paymentMethod == "COD" }
    val totalSales = orders.filter { it.orderStatus == "Delivered" }.sumOf { it.totalAmount }
    val pendingOrdersCount = orders.count { it.orderStatus == "Order Placed" || it.orderStatus == "Seller Confirmed" }
    val cancelledOrdersCount = orders.count { it.orderStatus == "Cancelled" }

    var selectedTab by remember { mutableStateOf("Dashboard") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Control Panel") },
                actions = {
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(selected = selectedTab == "Dashboard", onClick = { selectedTab = "Dashboard" }, label = { Text("Dashboard") })
                FilterChip(selected = selectedTab == "Users", onClick = { selectedTab = "Users" }, label = { Text("Users") })
                FilterChip(selected = selectedTab == "Products", onClick = { selectedTab = "Products" }, label = { Text("Products") })
                FilterChip(selected = selectedTab == "Orders", onClick = { selectedTab = "Orders" }, label = { Text("Orders") })
                FilterChip(selected = selectedTab == "Commission", onClick = { selectedTab = "Commission" }, label = { Text("Commission") })
                FilterChip(selected = selectedTab == "Reports", onClick = { selectedTab = "Reports" }, label = { Text("Reports") })
                FilterChip(selected = selectedTab == "Support", onClick = { selectedTab = "Support" }, label = { Text("Support") })
            }

            when (selectedTab) {
                "Dashboard" -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                AdminMetricCard("Total Buyers", "$buyersCount", Modifier.weight(1f))
                                AdminMetricCard("Total Sellers", "$sellersCount", Modifier.weight(1f))
                            }
                        }
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                AdminMetricCard("Total Products", "${products.size}", Modifier.weight(1f))
                                AdminMetricCard("Total Orders", "${orders.size}", Modifier.weight(1f))
                            }
                        }
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                AdminMetricCard("Online Payments", "$onlinePaymentsCount", Modifier.weight(1f))
                                AdminMetricCard("COD Orders", "$codCount", Modifier.weight(1f))
                            }
                        }
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                AdminMetricCard("Total Sales", "₹$totalSales", Modifier.weight(1f))
                                AdminMetricCard("Pending Orders", "$pendingOrdersCount", Modifier.weight(1f))
                            }
                        }
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                AdminMetricCard("Cancelled Orders", "$cancelledOrdersCount", Modifier.weight(1f))
                            }
                        }
                    }
                }
                "Users" -> {
                    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(users) { u ->
                            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(u.name, fontWeight = FontWeight.Bold)
                                    Text("Email: ${u.email} | Role: ${u.role}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
                "Products" -> {
                    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(products) { p ->
                            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(p.name, fontWeight = FontWeight.Bold)
                                    Text("Design ID: ${p.designId} | Price: ₹${p.price} | Approved: ${p.isApproved}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
                "Orders" -> {
                    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(orders) { o ->
                            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Order #${o.orderNumber}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text("Status: ${o.orderStatus} | Total: ₹${o.totalAmount} | Payment: ${o.paymentMethod}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
                "Commission" -> {
                    var globalCommission by remember { mutableStateOf("8.0") }
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Marketplace Commission Control", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Configure global platform fee percentage deducted from seller payouts.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            OutlinedTextField(
                                value = globalCommission,
                                onValueChange = { globalCommission = it },
                                label = { Text("Global Commission Percentage (%)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Button(onClick = { /* save config */ }) {
                                Text("Save Commission Rule")
                            }
                        }
                    }
                }
                "Reports" -> {
                    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(reports) { r ->
                            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Reported Type: ${r.reportedType} (#${r.reportedId})", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                    Text("Reason: ${r.reason}", style = MaterialTheme.typography.bodyMedium)
                                    Text("Description: ${r.description}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
                "Support" -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            com.example.ui.components.AboutAppSection()
                        }
                        item {
                            com.example.ui.components.ContactSupportSection()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
        }
    }
}
