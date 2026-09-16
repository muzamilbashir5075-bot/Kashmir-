package com.example.ui.seller

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.data.ProductEntity
import com.example.ui.MarketViewModel

@Composable
fun SellerMainScreen(viewModel: MarketViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }
    val tabs = listOf("Dashboard", "Products", "Add", "Orders", "Wallet", "Settings")

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            if (index == 2 && selectedTab != 2) {
                                // Reset editing product when switching directly to Add tab from bottom bar
                                editingProduct = null
                            }
                            selectedTab = index
                        },
                        icon = {
                            when (index) {
                                0 -> Icon(Icons.Default.Dashboard, contentDescription = title)
                                1 -> Icon(Icons.Default.Inventory, contentDescription = title)
                                2 -> Icon(Icons.Default.AddBox, contentDescription = title)
                                3 -> Icon(Icons.Default.ShoppingBag, contentDescription = title)
                                4 -> Icon(Icons.Default.Wallet, contentDescription = title)
                                5 -> Icon(Icons.Default.Settings, contentDescription = title)
                            }
                        },
                        label = { Text(if (index == 2 && editingProduct != null) "Edit" else title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> SellerDashboardTab(viewModel)
                1 -> SellerProductsTab(
                    viewModel = viewModel,
                    onEditProduct = { product ->
                        editingProduct = product
                        selectedTab = 2
                    }
                )
                2 -> SellerAddProductTab(
                    viewModel = viewModel,
                    initialProduct = editingProduct,
                    onSaved = {
                        editingProduct = null
                        selectedTab = 1
                    },
                    onCancelEdit = {
                        editingProduct = null
                        selectedTab = 1
                    }
                )
                3 -> SellerOrdersTab(viewModel)
                4 -> SellerEarningsTab(viewModel)
                5 -> SellerPaymentSettingsTab(viewModel)
            }
        }
    }
}
