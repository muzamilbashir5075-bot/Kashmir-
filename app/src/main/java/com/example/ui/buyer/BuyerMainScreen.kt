package com.example.ui.buyer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ui.MarketViewModel

@Composable
fun BuyerMainScreen(viewModel: MarketViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Catalog", "Search", "Wishlist", "Cart", "Profile")

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            when (index) {
                                0 -> Icon(Icons.Default.Storefront, contentDescription = title)
                                1 -> Icon(Icons.Default.Search, contentDescription = title)
                                2 -> Icon(Icons.Default.Favorite, contentDescription = title)
                                3 -> Icon(Icons.Default.ShoppingCart, contentDescription = title)
                                4 -> Icon(Icons.Default.Person, contentDescription = title)
                            }
                        },
                        label = { Text(title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> ProductCatalogScreen(viewModel)
                1 -> BuyerSearchTab(viewModel)
                2 -> BuyerWishlistTab(viewModel)
                3 -> BuyerCartTab(viewModel)
                4 -> BuyerProfileTab(viewModel)
            }
        }
    }
}
