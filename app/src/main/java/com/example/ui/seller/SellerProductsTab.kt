package com.example.ui.seller

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProductEntity
import com.example.ui.MarketViewModel
import com.example.ui.buyer.KashmiriShawlImage

@Composable
fun SellerProductsTab(
    viewModel: MarketViewModel,
    onEditProduct: (ProductEntity) -> Unit = {}
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allProducts by viewModel.adminProducts.collectAsState()
    val sellerProducts = allProducts.filter { it.sellerId == currentUser?.id }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "My Catalog Products",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${sellerProducts.size} Kashmiri Shawls in Catalog",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (sellerProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No catalog products listed yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Switch to the 'Add' tab to upload your first shawl with catalog photos.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sellerProducts) { product ->
                    SellerProductItemRow(
                        product = product,
                        onEdit = { onEditProduct(product) },
                        onDelete = { viewModel.deleteProduct(product) },
                        onToggleHide = {
                            viewModel.saveProduct(
                                product.id, product.sellerId, product.name, product.designId,
                                product.price, product.discountPrice, product.stock, product.color,
                                product.material, product.workType, product.size, product.description,
                                product.category, product.imagesJson, product.videoUrl, !product.isHidden, product.isSoldOut
                            )
                        },
                        onToggleSoldOut = {
                            viewModel.saveProduct(
                                product.id, product.sellerId, product.name, product.designId,
                                product.price, product.discountPrice, product.stock, product.color,
                                product.material, product.workType, product.size, product.description,
                                product.category, product.imagesJson, product.videoUrl, product.isHidden, !product.isSoldOut
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SellerProductItemRow(
    product: ProductEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleHide: () -> Unit,
    onToggleSoldOut: () -> Unit
) {
    val photoList = remember(product.imagesJson) {
        product.imagesJson.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }
    val primaryPhoto = photoList.firstOrNull() ?: ""
    val goldColor = Color(0xFFC59B27)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Product Catalog Photo Thumbnail
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    KashmiriShawlImage(
                        imageUrl = primaryPhoto,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Photo count badge
                    if (photoList.size > 1) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(2.dp),
                            shape = RoundedCornerShape(4.dp),
                            color = Color.Black.copy(alpha = 0.7f)
                        ) {
                            Text(
                                text = "${photoList.size}📷",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                // Title & Details
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = product.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Design ID: ${product.designId} • ${product.material}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "₹${product.price.toInt()}",
                            fontWeight = FontWeight.Bold,
                            color = goldColor,
                            fontSize = 15.sp
                        )
                        if (product.discountPrice > 0 && product.discountPrice < product.price) {
                            Text(
                                text = "₹${product.discountPrice.toInt()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Text(
                        text = "Stock: ${product.stock} | Hidden: ${product.isHidden} | Sold Out: ${product.isSoldOut}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Action Buttons Row: Edit Photos & Details, Hide, Sold Out
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit Photos", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onToggleHide,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (product.isHidden) "Unhide" else "Hide",
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(
                    onClick = onToggleSoldOut,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (product.isSoldOut) "Available" else "Sold Out",
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
