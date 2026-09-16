package com.example.ui.buyer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.ProductEntity
import com.example.ui.MarketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(viewModel: MarketViewModel, productId: Long, onBack: () -> Unit) {
    val products by viewModel.allProducts.collectAsState()
    val product = products.find { it.id == productId }

    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Product not found")
        }
        return
    }

    val images = product.imagesJson.split(",")
    var selectedImage by remember { mutableStateOf(images.firstOrNull() ?: "") }
    val reviews by viewModel.getReviewsForProduct(product.id).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { viewModel.selectChatPartner(product.sellerId) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Chat")
                    }

                    Button(
                        onClick = { viewModel.addToCart(product.id, 1) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Add to Cart")
                    }

                    Button(
                        onClick = {
                            viewModel.addToCart(product.id, 1)
                            viewModel.navigateTo("BUYER_MAIN") // or cart
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Buy Now")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Image
            KashmiriShawlImage(
                imageUrl = selectedImage,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            // Gallery thumbnails
            if (images.size > 1) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    images.forEach { img ->
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedImage = img }
                        ) {
                            KashmiriShawlImage(
                                imageUrl = img,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "Design ID: ${product.designId}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "Category: ${product.category}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${product.rating} (${product.reviewCount} reviews)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
            }

            // Price
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                if (product.discountPrice > 0) {
                    Text(
                        text = "₹${product.price}",
                        style = MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.LineThrough),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "₹${product.discountPrice}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = "₹${product.price}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Divider()

            // Specifications
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Product Specifications", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("• Material: ${product.material}")
                Text("• Work Type: ${product.workType}")
                Text("• Color: ${product.color}")
                Text("• Size: ${product.size}")
                Text("• Stock Available: ${product.stock} units")
            }

            Divider()

            Text("Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(product.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Divider()

            Text("Verified Buyer Reviews", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (reviews.isEmpty()) {
                Text("No reviews yet.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                reviews.forEach { rev ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(rev.buyerName, fontWeight = FontWeight.Bold)
                                Text("★ ${rev.rating}/5", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(rev.comment, style = MaterialTheme.typography.bodyMedium)
                            if (rev.isVerifiedPurchase) {
                                Text("✓ Verified Purchase", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.tertiary)
                            }
                        }
                    }
                }
            }
        }
    }
}
