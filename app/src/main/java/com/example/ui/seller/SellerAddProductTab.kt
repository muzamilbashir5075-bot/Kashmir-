package com.example.ui.seller

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProductEntity
import com.example.ui.MarketViewModel
import com.example.ui.buyer.KashmiriShawlImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

data class ShawlPreset(
    val title: String,
    val weaveType: String,
    val imageUrl: String
)

val kashmiriShawlPhotoPresets = listOf(
    ShawlPreset(
        title = "Authentic Kanihama Kani Weave",
        weaveType = "Kani Weave • Ladakhi Pashmina",
        imageUrl = "drawable://img_shawl_kani"
    ),
    ShawlPreset(
        title = "Royal Jamawar Needlework",
        weaveType = "Sozni & Jamawar • Crimson Wool",
        imageUrl = "drawable://img_shawl_pashmina"
    ),
    ShawlPreset(
        title = "Floral Sozni Embroidered Wrap",
        weaveType = "Silk Needlework • Ivory Cashmere",
        imageUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477"
    ),
    ShawlPreset(
        title = "Gold Tilla Zari Threadwork",
        weaveType = "Tilla Metallic Work • Royal Black",
        imageUrl = "https://images.unsplash.com/photo-1548036328-c9fa89d128fa"
    ),
    ShawlPreset(
        title = "Traditional Paisley Pashmina",
        weaveType = "Heritage Jaalidar Weave",
        imageUrl = "https://images.unsplash.com/photo-1606760227091-3dd870d97f1d"
    ),
    ShawlPreset(
        title = "Handspun Natural Fleece Wrap",
        weaveType = "Undyed Raw Cashmere",
        imageUrl = "https://images.unsplash.com/photo-1584917865442-de89df76afd3"
    )
)

private fun copyUriToLocalStorage(context: Context, uri: Uri): String {
    return try {
        val dir = File(context.filesDir, "catalog_photos")
        if (!dir.exists()) dir.mkdirs()
        val extension = context.contentResolver.getType(uri)?.substringAfterLast("/") ?: "jpg"
        val fileName = "shawl_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.$extension"
        val file = File(dir, fileName)
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        "file://${file.absolutePath}"
    } catch (_: Exception) {
        uri.toString()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerAddProductTab(
    viewModel: MarketViewModel,
    initialProduct: ProductEntity? = null,
    onSaved: () -> Unit,
    onCancelEdit: (() -> Unit)? = null
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var name by remember(initialProduct) { mutableStateOf(initialProduct?.name ?: "") }
    var designId by remember(initialProduct) { mutableStateOf(initialProduct?.designId ?: "KSM-KN-007") }
    var price by remember(initialProduct) { mutableStateOf(initialProduct?.price?.let { if (it > 0) it.toInt().toString() else "" } ?: "24500") }
    var discountPrice by remember(initialProduct) { mutableStateOf(initialProduct?.discountPrice?.let { if (it > 0) it.toInt().toString() else "" } ?: "21999") }
    var stock by remember(initialProduct) { mutableStateOf(initialProduct?.stock?.toString() ?: "8") }
    var color by remember(initialProduct) { mutableStateOf(initialProduct?.color ?: "Royal Kashmiri Blue") }
    var material by remember(initialProduct) { mutableStateOf(initialProduct?.material ?: "Pure Pashmina") }
    var workType by remember(initialProduct) { mutableStateOf(initialProduct?.workType ?: "Kani Weave") }
    var size by remember(initialProduct) { mutableStateOf(initialProduct?.size ?: "100 x 200 cm") }
    var category by remember(initialProduct) { mutableStateOf(initialProduct?.category ?: "Kashmiri Shawls") }
    var description by remember(initialProduct) {
        mutableStateOf(
            initialProduct?.description
                ?: "Authentic handwoven Kashmiri masterpiece featuring intricate motifs crafted over months by generational master artisans."
        )
    }
    var videoUrl by remember(initialProduct) { mutableStateOf(initialProduct?.videoUrl ?: "") }

    // Catalog Photos list
    var imageList by remember(initialProduct) {
        val initialList = initialProduct?.imagesJson?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
            ?: listOf("drawable://img_shawl_kani", "https://images.unsplash.com/photo-1606760227091-3dd870d97f1d")
        mutableStateOf(initialList)
    }

    var showPresetDialog by remember { mutableStateOf(false) }
    var showUrlDialog by remember { mutableStateOf(false) }
    var customUrlInput by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf("") }

    // Native Zero-Permission Android Photo Picker (Multi-selection)
    val multiPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 6)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            coroutineScope.launch(Dispatchers.IO) {
                val localPaths = uris.map { copyUriToLocalStorage(context, it) }
                withContext(Dispatchers.Main) {
                    imageList = (imageList + localPaths).distinct()
                    validationError = ""
                }
            }
        }
    }

    val goldAccent = Color(0xFFC59B27)
    val goldSurface = Color(0xFFFFF9E6)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Edit Mode Header Banner
        if (initialProduct != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Editing Product",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = initialProduct.name.ifBlank { initialProduct.designId },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (onCancelEdit != null) {
                        TextButton(onClick = onCancelEdit) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cancel Edit")
                        }
                    }
                }
            }
        }

        // Title Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (initialProduct != null) "Edit Product & Photos" else "Add New Product",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Upload authentic Kashmiri shawl catalog photos and craft details",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // =========================================================================
        // 1. CATALOG PHOTOS UPLOAD SECTION (CRITICAL FEATURE)
        // =========================================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Section Title with Photo Count Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Collections,
                            contentDescription = null,
                            tint = goldAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Catalog Photos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (imageList.isNotEmpty()) goldAccent.copy(alpha = 0.15f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, if (imageList.isNotEmpty()) goldAccent.copy(alpha = 0.5f) else MaterialTheme.colorScheme.error.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "${imageList.size} Photos",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (imageList.isNotEmpty()) goldAccent else MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = "Upload high-resolution photos of the shawl, borders, and weave details. The first photo is your main catalog cover.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Photo Preview Carousel
                if (imageList.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        itemsIndexed(imageList) { index, photoUrl ->
                            Card(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(145.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    width = if (index == 0) 2.dp else 1.dp,
                                    color = if (index == 0) goldAccent else MaterialTheme.colorScheme.outlineVariant
                                )
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    KashmiriShawlImage(
                                        imageUrl = photoUrl,
                                        contentDescription = "Catalog photo ${index + 1}",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                    // Primary Cover Badge
                                    if (index == 0) {
                                        Surface(
                                            color = goldAccent,
                                            shape = RoundedCornerShape(bottomEnd = 8.dp),
                                            modifier = Modifier.align(Alignment.TopStart)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = Color.Black,
                                                    modifier = Modifier.size(11.dp)
                                                )
                                                Text(
                                                    text = "COVER",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color.Black
                                                )
                                            }
                                        }
                                    }

                                    // Remove Photo Button
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color.Black.copy(alpha = 0.70f))
                                            .clickable {
                                                imageList = imageList.toMutableList().also { it.removeAt(index) }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Delete photo",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }

                                    // "Set as Cover" Button for non-primary images
                                    if (index > 0) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .fillMaxWidth()
                                                .background(Color.Black.copy(alpha = 0.75f))
                                                .clickable {
                                                    val mutable = imageList.toMutableList()
                                                    val item = mutable.removeAt(index)
                                                    mutable.add(0, item)
                                                    imageList = mutable
                                                }
                                                .padding(vertical = 4.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Set Cover",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // "Add More" Card in the row
                        item {
                            Surface(
                                modifier = Modifier
                                    .width(105.dp)
                                    .height(145.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        multiPhotoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = "Add More Photos",
                                        tint = goldAccent,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "+ Add More",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Empty state when no photos selected yet
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                multiPhotoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = BorderStroke(1.5.dp, goldAccent.copy(alpha = 0.6f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(goldAccent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = goldAccent,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Upload Catalog Photos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap here to select photos from your device gallery, or choose authentic artisan presets below.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Upload Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = {
                            multiPhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = goldAccent.copy(alpha = 0.20f),
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = goldAccent
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Device Gallery", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = { showPresetDialog = true },
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, goldAccent.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = goldAccent
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Artisan Presets", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    IconButton(
                        onClick = { showUrlDialog = true },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "Add image URL",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Validation error display if user attempted saving without required info
        if (validationError.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = validationError,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // =========================================================================
        // 2. PRODUCT SPECIFICATIONS SECTION
        // =========================================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Shawl Details & Pricing",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; validationError = "" },
                    label = { Text("Product / Shawl Name *") },
                    placeholder = { Text("e.g. Authentic Kanihama Kani Pashmina Shawl") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = designId,
                    onValueChange = { designId = it },
                    label = { Text("Design ID / Artisan SKU *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it; validationError = "" },
                        label = { Text("Original Price (₹) *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = discountPrice,
                        onValueChange = { discountPrice = it },
                        label = { Text("Offer Price (₹)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = { Text("Stock Quantity") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = color,
                        onValueChange = { color = it },
                        label = { Text("Colour / Shade") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = material,
                        onValueChange = { material = it },
                        label = { Text("Material") },
                        placeholder = { Text("Pure Pashmina, Cashmere") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = workType,
                        onValueChange = { workType = it },
                        label = { Text("Weave / Work Type") },
                        placeholder = { Text("Kani Weave, Sozni, Tilla") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = size,
                        onValueChange = { size = it },
                        label = { Text("Dimensions") },
                        placeholder = { Text("100 x 200 cm") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = videoUrl,
                    onValueChange = { videoUrl = it },
                    label = { Text("Artisan Weaving Video URL (Optional)") },
                    placeholder = { Text("https://...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Craftsmanship Narrative & Heritage Story") },
                    placeholder = { Text("Describe the weaving technique, months taken, and authenticity...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        }

        // =========================================================================
        // 3. SUBMIT / SAVE PRODUCT BUTTON
        // =========================================================================
        Button(
            onClick = {
                val seller = currentUser
                if (seller == null) {
                    validationError = "Seller account not active. Please log in as a seller."
                    return@Button
                }
                if (name.isBlank()) {
                    validationError = "Please enter product name"
                    return@Button
                }
                if (price.toDoubleOrNull() == null || (price.toDoubleOrNull() ?: 0.0) <= 0.0) {
                    validationError = "Please enter a valid price"
                    return@Button
                }
                if (imageList.isEmpty()) {
                    validationError = "Please upload or select at least one catalog photo."
                    return@Button
                }

                isSaving = true
                val finalImagesJson = imageList.joinToString(",")

                viewModel.saveProduct(
                    productId = initialProduct?.id ?: 0L,
                    sellerId = initialProduct?.sellerId ?: seller.id,
                    name = name.trim(),
                    designId = designId.trim().ifBlank { "KSM-SHW-${System.currentTimeMillis() % 1000}" },
                    price = price.toDoubleOrNull() ?: 0.0,
                    discountPrice = discountPrice.toDoubleOrNull() ?: (price.toDoubleOrNull() ?: 0.0),
                    stock = stock.toIntOrNull() ?: 1,
                    color = color.trim(),
                    material = material.trim(),
                    workType = workType.trim(),
                    size = size.trim(),
                    description = description.trim(),
                    category = category.trim().ifBlank { "Kashmiri Shawls" },
                    imagesJson = finalImagesJson,
                    videoUrl = videoUrl.trim(),
                    isHidden = initialProduct?.isHidden ?: false,
                    isSoldOut = initialProduct?.isSoldOut ?: false
                )

                isSaving = false
                onSaved()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isSaving,
            colors = ButtonDefaults.buttonColors(
                containerColor = goldAccent,
                contentColor = Color.Black
            )
        ) {
            if (isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Saving to Catalog...", fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (initialProduct != null) "Update Product & Photos" else "Publish Product to Catalog",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // =========================================================================
    // DIALOG: ARTISAN SHAWL PRESETS
    // =========================================================================
    if (showPresetDialog) {
        AlertDialog(
            onDismissRequest = { showPresetDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = goldAccent)
                    Text("Select Kashmiri Shawl Preset")
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Choose from verified authentic Kashmiri shawl imagery to add directly to your catalog listing:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    kashmiriShawlPhotoPresets.forEach { preset ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    if (!imageList.contains(preset.imageUrl)) {
                                        imageList = imageList + preset.imageUrl
                                    }
                                    showPresetDialog = false
                                    validationError = ""
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Card(
                                    modifier = Modifier.size(54.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    KashmiriShawlImage(
                                        imageUrl = preset.imageUrl,
                                        contentDescription = preset.title,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = preset.title,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = preset.weaveType,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Preset",
                                    tint = goldAccent
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPresetDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // =========================================================================
    // DIALOG: ADD IMAGE URL
    // =========================================================================
    if (showUrlDialog) {
        AlertDialog(
            onDismissRequest = { showUrlDialog = false },
            title = { Text("Add Catalog Photo URL") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Enter a direct image link (HTTP/HTTPS) for the shawl:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = customUrlInput,
                        onValueChange = { customUrlInput = it },
                        label = { Text("Photo URL") },
                        placeholder = { Text("https://example.com/shawl.jpg") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clean = customUrlInput.trim()
                        if (clean.isNotEmpty() && !imageList.contains(clean)) {
                            imageList = imageList + clean
                            customUrlInput = ""
                            showUrlDialog = false
                            validationError = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = goldAccent, contentColor = Color.Black)
                ) {
                    Text("Add Photo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUrlDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
