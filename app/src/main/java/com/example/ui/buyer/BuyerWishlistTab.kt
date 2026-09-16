package com.example.ui.buyer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.MarketViewModel

@Composable
fun BuyerWishlistTab(viewModel: MarketViewModel) {
    val wishlistItems by viewModel.getWishlistFlow().collectAsState(initial = emptyList())
    val allProducts by viewModel.allProducts.collectAsState()

    val wishlistedProducts = allProducts.filter { prod -> wishlistItems.any { it.productId == prod.id } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "My Wishlist",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        if (wishlistedProducts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Your wishlist is empty", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(wishlistedProducts) { product ->
                    ProductCard(product = product, onProductClick = {
                        viewModel.selectProduct(product.id)
                    })
                }
            }
        }
    }
}
