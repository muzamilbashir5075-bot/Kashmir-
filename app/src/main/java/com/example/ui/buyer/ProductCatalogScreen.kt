package com.example.ui.buyer

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.ProductEntity
import com.example.ui.MarketViewModel
import java.text.NumberFormat
import java.util.Locale

// Sort options for the Kashmiri Shawl Catalog
enum class CatalogSortOption(val displayName: String) {
    FEATURED("Featured & Heritage"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low"),
    RATING("Highest Rated")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCatalogScreen(viewModel: MarketViewModel) {
    val context = LocalContext.current
    val products by viewModel.allProducts.collectAsState()
    val isRefreshing by viewModel.isCatalogRefreshing.collectAsState()
    val wishlistItems by viewModel.getWishlistFlow().collectAsState(initial = emptyList())

    // UI state for search, filter & layout mode
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All Shawls") }
    var selectedWorkType by remember { mutableStateOf("All") }
    var selectedSort by remember { mutableStateOf(CatalogSortOption.FEATURED) }
    var isGridView by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    // Categories list for top filter row
    val categories = listOf(
        "All Shawls",
        "Pure Pashmina",
        "Kani Weave",
        "Sozni Work",
        "Tilla Work",
        "Jamawar",
        "Embroidered",
        "Handspun"
    )

    // Filter and sort Kashmiri shawl listings
    val filteredProducts = remember(products, searchQuery, selectedCategory, selectedWorkType, selectedSort) {
        var list = products.filter { prod ->
            val queryMatch = searchQuery.isBlank() ||
                    prod.name.contains(searchQuery, ignoreCase = true) ||
                    prod.description.contains(searchQuery, ignoreCase = true) ||
                    prod.designId.contains(searchQuery, ignoreCase = true) ||
                    prod.material.contains(searchQuery, ignoreCase = true) ||
                    prod.workType.contains(searchQuery, ignoreCase = true) ||
                    prod.color.contains(searchQuery, ignoreCase = true)

            val categoryMatch = when (selectedCategory) {
                "All Shawls" -> true
                "Pure Pashmina" -> prod.material.contains("Pashmina", ignoreCase = true) || prod.category.contains("Pashmina", ignoreCase = true)
                "Kani Weave" -> prod.workType.contains("Kani", ignoreCase = true) || prod.name.contains("Kani", ignoreCase = true)
                "Sozni Work" -> prod.workType.contains("Sozni", ignoreCase = true) || prod.category.contains("Sozni", ignoreCase = true)
                "Tilla Work" -> prod.workType.contains("Tilla", ignoreCase = true) || prod.name.contains("Tilla", ignoreCase = true)
                "Jamawar" -> prod.workType.contains("Jamawar", ignoreCase = true) || prod.name.contains("Jamawar", ignoreCase = true)
                "Embroidered" -> prod.category.contains("Embroidered", ignoreCase = true) || prod.workType.contains("Embroider", ignoreCase = true)
                "Handspun" -> prod.material.contains("Handspun", ignoreCase = true) || prod.description.contains("Handspun", ignoreCase = true)
                else -> prod.category.equals(selectedCategory, ignoreCase = true)
            }

            val workMatch = selectedWorkType == "All" || prod.workType.contains(selectedWorkType, ignoreCase = true)

            queryMatch && categoryMatch && workMatch
        }

        // Apply sorting
        when (selectedSort) {
            CatalogSortOption.FEATURED -> list.sortedByDescending { it.rating }
            CatalogSortOption.PRICE_LOW_HIGH -> list.sortedBy { if (it.discountPrice > 0) it.discountPrice else it.price }
            CatalogSortOption.PRICE_HIGH_LOW -> list.sortedByDescending { if (it.discountPrice > 0) it.discountPrice else it.price }
            CatalogSortOption.RATING -> list.sortedByDescending { it.rating }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val refreshRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationAnim"
    )

    // Palette accents for Kashmiri Royal heritage
    val goldColor = Color(0xFFD4AF37)
    val emeraldColor = Color(0xFF1B4D3E)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Kashmiri Shawl Catalog",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif
                                )
                            )
                        }
                        Text(
                            text = "Authentic Pashmina, Kani & Heritage Weaves",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // Refresh Catalog action
                    IconButton(
                        onClick = {
                            viewModel.refreshCatalog {
                                Toast.makeText(context, "Kashmiri Shawl catalog updated", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.testTag("refresh_catalog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Catalog",
                            modifier = if (isRefreshing) Modifier.rotate(refreshRotation) else Modifier,
                            tint = if (isRefreshing) goldColor else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Grid / List layout toggle
                    IconButton(
                        onClick = { isGridView = !isGridView },
                        modifier = Modifier.testTag("toggle_layout_button")
                    ) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.ViewAgenda else Icons.Default.GridView,
                            contentDescription = if (isGridView) "Switch to List View" else "Switch to Grid View"
                        )
                    }

                    // Sort menu button
                    Box {
                        IconButton(
                            onClick = { showSortMenu = true },
                            modifier = Modifier.testTag("sort_catalog_button")
                        ) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort Options")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            CatalogSortOption.values().forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = option.displayName,
                                            fontWeight = if (selectedSort == option) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedSort == option) goldColor else MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    leadingIcon = {
                                        if (selectedSort == option) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = goldColor)
                                        }
                                    },
                                    onClick = {
                                        selectedSort = option
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("catalog_search_input"),
                placeholder = { Text("Search by weave, needlework, design ID, color...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear Search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = goldColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            // Horizontal Scrollable Category & Weave Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = {
                            Text(
                                text = cat,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = goldColor.copy(alpha = 0.2f),
                            selectedLabelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            selectedBorderColor = goldColor
                        )
                    )
                }
            }

            // Results count and active filter summary bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredProducts.size} Authentic Shawls",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Sorted by: ${selectedSort.displayName.split(":").first()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = goldColor
                )
            }

            // Shawl Listings Content: Empty / Grid / Detailed List
            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Kashmiri Shawls Found",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try adjusting your search keywords or weave category filters.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                searchQuery = ""
                                selectedCategory = "All Shawls"
                                selectedWorkType = "All"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = goldColor)
                        ) {
                            Text("Reset All Filters", color = Color.Black)
                        }
                    }
                }
            } else if (isGridView) {
                // 2-Column Shopping Catalog Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        val isWishlisted = wishlistItems.any { it.productId == product.id }
                        KashmiriShawlGridCard(
                            product = product,
                            isWishlisted = isWishlisted,
                            onProductClick = { viewModel.selectProduct(product.id) },
                            onWishlistToggle = { viewModel.toggleWishlist(product.id) },
                            onAddToCart = {
                                viewModel.addToCart(product.id, 1)
                                Toast.makeText(context, "Added to cart: ${product.name}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            } else {
                // Detailed Card View (Rich Visuals, Full Craftsmanship Descriptions & Pricing)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        val isWishlisted = wishlistItems.any { it.productId == product.id }
                        KashmiriShawlDetailedCard(
                            product = product,
                            isWishlisted = isWishlisted,
                            onProductClick = { viewModel.selectProduct(product.id) },
                            onWishlistToggle = { viewModel.toggleWishlist(product.id) },
                            onAddToCart = {
                                viewModel.addToCart(product.id, 1)
                                Toast.makeText(context, "Added to cart: ${product.name}", Toast.LENGTH_SHORT).show()
                            },
                            onChatArtisan = {
                                viewModel.selectChatPartner(product.sellerId)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Detailed Card for Kashmiri Shawl Listing:
 * Features prominent shawl photography, artisan craftsmanship descriptions,
 * specs badges, and clear price breakdowns in Indian Rupees.
 */
@Composable
fun KashmiriShawlDetailedCard(
    product: ProductEntity,
    isWishlisted: Boolean,
    onProductClick: () -> Unit,
    onWishlistToggle: () -> Unit,
    onAddToCart: () -> Unit,
    onChatArtisan: () -> Unit
) {
    var isDescriptionExpanded by remember { mutableStateOf(false) }
    val goldColor = Color(0xFFD4AF37)
    val emeraldColor = Color(0xFF1B4D3E)
    val firstImage = product.imagesJson.split(",").firstOrNull() ?: ""

    val formatCurrency = remember {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        format.maximumFractionDigits = 0
        format
    }

    val currentPrice = if (product.discountPrice > 0) product.discountPrice else product.price
    val hasDiscount = product.discountPrice > 0 && product.discountPrice < product.price
    val discountPercent = if (hasDiscount) {
        (((product.price - product.discountPrice) / product.price) * 100).toInt()
    } else 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onProductClick() }
            .testTag("catalog_card_${product.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Shawl Image Banner with Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            ) {
                KashmiriShawlImage(
                    imageUrl = firstImage,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Soft subtle gradient scrim for badge contrast
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.40f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.50f)
                                )
                            )
                        )
                )

                // Top-Left: Authenticity & Craft Badges
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.70f),
                        border = BorderStroke(1.dp, goldColor.copy(alpha = 0.8f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = null,
                                tint = goldColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "GI Tagged",
                                color = goldColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = emeraldColor.copy(alpha = 0.85f)
                    ) {
                        Text(
                            text = product.workType,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Top-Right: Wishlist Heart Button
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.55f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(36.dp)
                        .clickable { onWishlistToggle() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isWishlisted) "Remove from Wishlist" else "Add to Wishlist",
                            tint = if (isWishlisted) Color(0xFFFF4081) else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Bottom Overlay: Design ID & Stock Status
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.65f)
                    ) {
                        Text(
                            text = "Design #${product.designId}",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (product.stock in 1..5) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFD32F2F).copy(alpha = 0.85f)
                        ) {
                            Text(
                                text = "Only ${product.stock} left in stock",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // Shawl Information & Craftsmanship Description
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Title and Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            fontSize = 17.sp
                        ),
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = goldColor.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = goldColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${product.rating}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = " (${product.reviewCount})",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Specification badges (Material, Dimensions, Color)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SpecChip(label = "Material", value = product.material)
                    SpecChip(label = "Weave", value = product.workType)
                    SpecChip(label = "Size", value = product.size)
                    SpecChip(label = "Color", value = product.color)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Detailed Kashmiri Shawl Craftsmanship Description
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Artisan Description & Weave Technique",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = goldColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 3,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 20.sp
                        )
                        if (product.description.length > 120) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isDescriptionExpanded) "Show less" else "Read full artisan story...",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { isDescriptionExpanded = !isDescriptionExpanded }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Pricing and Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Price Breakdown
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = formatCurrency.format(currentPrice),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 22.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )

                            if (hasDiscount) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = formatCurrency.format(product.price),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        textDecoration = TextDecoration.LineThrough
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (hasDiscount) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF2E7D32).copy(alpha = 0.15f),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = "Special Price • $discountPercent% OFF",
                                    color = Color(0xFF2E7D32),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        } else {
                            Text(
                                text = "Free Insured Shipping Across India",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Action Buttons (Chat Artisan & Add to Cart)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onChatArtisan,
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Chat with Artisan",
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Button(
                            onClick = onAddToCart,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = goldColor,
                                contentColor = Color(0xFF1F1600)
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("add_to_cart_${product.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Add to Cart",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Compact Grid Card for Kashmiri Shawl Catalog:
 * Optimized for rapid scanning in 2-column mode with image, description preview, and price.
 */
@Composable
fun KashmiriShawlGridCard(
    product: ProductEntity,
    isWishlisted: Boolean,
    onProductClick: () -> Unit,
    onWishlistToggle: () -> Unit,
    onAddToCart: () -> Unit
) {
    val goldColor = Color(0xFFD4AF37)
    val firstImage = product.imagesJson.split(",").firstOrNull() ?: ""

    val formatCurrency = remember {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        format.maximumFractionDigits = 0
        format
    }

    val currentPrice = if (product.discountPrice > 0) product.discountPrice else product.price
    val hasDiscount = product.discountPrice > 0 && product.discountPrice < product.price

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onProductClick() }
            .testTag("catalog_grid_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column {
            // Shawl Photo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                KashmiriShawlImage(
                    imageUrl = firstImage,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Wishlist heart
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(30.dp)
                        .clickable { onWishlistToggle() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isWishlisted) Color(0xFFFF4081) else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Work Type Tag
                Surface(
                    shape = RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 6.dp, bottomEnd = 6.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Text(
                        text = product.workType,
                        color = goldColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            // Information & Price
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Short Craftsmanship Description snippet
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = formatCurrency.format(currentPrice),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 15.sp
                        )
                        if (hasDiscount) {
                            Text(
                                text = formatCurrency.format(product.price),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    textDecoration = TextDecoration.LineThrough
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Add to Cart circular button
                    FilledIconButton(
                        onClick = onAddToCart,
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = goldColor,
                            contentColor = Color(0xFF1F1600)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add to Cart",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Robust image loader for Kashmiri Shawls:
 * Supports drawable resource schemes ("drawable://img_shawl_kani", "drawable://img_shawl_pashmina"),
 * remote HTTP URLs, with graceful local fallback to authentic shawl photography if offline.
 */
@Composable
fun KashmiriShawlImage(
    imageUrl: String,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current

    when {
        imageUrl.contains("img_shawl_kani") -> {
            Image(
                painter = painterResource(id = R.drawable.img_shawl_kani),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        imageUrl.contains("img_shawl_pashmina") -> {
            Image(
                painter = painterResource(id = R.drawable.img_shawl_pashmina),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        imageUrl.contains("brand_banner_bg") -> {
            Image(
                painter = painterResource(id = R.drawable.brand_banner_bg),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        else -> {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl.ifBlank { R.drawable.img_shawl_pashmina })
                    .crossfade(true)
                    .error(R.drawable.img_shawl_pashmina)
                    .placeholder(R.drawable.img_shawl_pashmina)
                    .build(),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
    }
}

/**
 * Small pill badge for Kashmiri Shawl attributes (Material, Weave, Size, Color)
 */
@Composable
fun SpecChip(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label: ",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
