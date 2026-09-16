package com.example.ui.buyer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.MarketViewModel

@Composable
fun BuyerSearchTab(viewModel: MarketViewModel) {
    val products by viewModel.allProducts.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedMaterial by remember { mutableStateOf("All") }

    val filtered = products.filter {
        val matchesSearch = it.name.contains(searchQuery, ignoreCase = true) ||
                it.designId.contains(searchQuery, ignoreCase = true) ||
                it.material.contains(searchQuery, ignoreCase = true) ||
                it.workType.contains(searchQuery, ignoreCase = true) ||
                it.color.contains(searchQuery, ignoreCase = true)

        val matchesMaterial = selectedMaterial == "All" || it.material.equals(selectedMaterial, ignoreCase = true)

        matchesSearch && matchesMaterial
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search by name, design ID, material, work...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val materials = listOf("All", "Pure Pashmina", "Cashmere Wool", "Handspun Pashmina")
            materials.forEach { mat ->
                FilterChip(
                    selected = selectedMaterial == mat,
                    onClick = { selectedMaterial = mat },
                    label = { Text(mat) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filtered) { product ->
                ProductCard(product = product, onProductClick = {
                    viewModel.selectProduct(product.id)
                })
            }
        }
    }
}
