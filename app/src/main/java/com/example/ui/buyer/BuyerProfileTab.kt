package com.example.ui.buyer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.OrderEntity
import com.example.ui.MarketViewModel
import com.example.ui.components.ContactSupportSection
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.io.FileOutputStream

import kotlinx.coroutines.flow.map

@Composable
fun BuyerProfileTab(viewModel: MarketViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val orders by viewModel.getOrdersForBuyer().collectAsState(initial = emptyList())
    val wishlistItems by viewModel.getWishlistFlow().collectAsState(initial = emptyList())
    val products by viewModel.adminProducts.collectAsState()
    
    // Internal Navigation State
    var currentSection by remember { mutableStateOf("MENU") } // "MENU", "ORDERS", "WISHLIST"
    
    // Edit Profile State
    var showEditProfile by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(currentUser?.name ?: "") }
    var editPhone by remember { mutableStateOf(currentUser?.phone ?: "") }
    var editAddress by remember { mutableStateOf(currentUser?.address ?: "") }

    // Review State
    var reviewProductId by remember { mutableStateOf<Long?>(null) }
    var reviewComment by remember { mutableStateOf("") }
    var reviewRating by remember { mutableStateOf(5) }

    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                try {
                    val contentResolver = context.contentResolver
                    val dir = File(context.filesDir, "profile_pics")
                    if (!dir.exists()) dir.mkdirs()
                    val file = File(dir, "avatar_${System.currentTimeMillis()}.jpg")
                    contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    }
                    viewModel.updateUserAvatar("file://${file.absolutePath}")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    )

    // Update state when user changes
    LaunchedEffect(currentUser) {
        if (!showEditProfile) {
            editName = currentUser?.name ?: ""
            editPhone = currentUser?.phone ?: ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (currentSection == "MENU") {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "My Profile",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                        }
                                ) {
                                    AsyncImage(
                                        model = currentUser?.avatarUrl?.ifEmpty { "https://images.unsplash.com/photo-1534528741775-53994a69daeb" },
                                        contentDescription = "Avatar",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    Surface(
                                        modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-4).dp, y = (-4).dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = CircleShape
                                    ) {
                                        Icon(
                                            Icons.Default.CameraAlt,
                                            contentDescription = "Change Picture",
                                            modifier = Modifier.padding(4.dp).size(14.dp),
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = currentUser?.name ?: "User",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = currentUser?.email ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = currentUser?.phone ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    
                                    if (!currentUser?.address.isNullOrBlank()) {
                                        androidx.compose.material3.Text(
                                            text = currentUser?.address ?: "",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            OutlinedButton(
                                onClick = { showEditProfile = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Manage Account Details")
                            }
                        }
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ProfileMenuButton(
                            icon = Icons.Default.ShoppingBag,
                            title = "My Order History & Tracking",
                            subtitle = "${orders.size} orders placed",
                            onClick = { currentSection = "ORDERS" }
                        )

                        ProfileMenuButton(
                            icon = Icons.Default.Favorite,
                            title = "Saved Items (Wishlist)",
                            subtitle = "${wishlistItems.size} items saved",
                            onClick = { currentSection = "WISHLIST" }
                        )
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        if (currentUser?.role != "SELLER") {
                            ProfileMenuButton(
                                icon = Icons.Default.Store,
                                title = "Become a Seller",
                                subtitle = "Start selling your products",
                                onClick = { viewModel.becomeSeller() }
                            )
                        } else {
                            ProfileMenuButton(
                                icon = Icons.Default.Storefront,
                                title = "Switch to Seller Dashboard",
                                subtitle = "Manage your shop",
                                onClick = { viewModel.switchRoleToSeller() }
                            )
                        }

                        ProfileMenuButton(
                            icon = Icons.Default.Logout,
                            title = "Logout",
                            subtitle = "Sign out of your account",
                            onClick = { viewModel.logout() },
                            isDestructive = true
                        )
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    ContactSupportSection()
                }

                item {
                    com.example.ui.components.AboutAppSection()
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        } else if (currentSection == "ORDERS") {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { currentSection = "MENU" }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "Order History",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                
                if (orders.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("You haven't placed any orders yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(orders) { order ->
                            OrderCard(
                                order = order,
                                onCancel = { viewModel.cancelOrder(order.id) },
                                onReviewClick = { prodId -> reviewProductId = prodId }
                            )
                        }
                    }
                }
            }
        } else if (currentSection == "WISHLIST") {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { currentSection = "MENU" }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "Saved Items",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                
                if (wishlistItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Your wishlist is empty.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(wishlistItems) { wishItem ->
                            val product = products.find { it.id == wishItem.productId }
                            if (product != null) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val photo = product.imagesJson.split(",").firstOrNull()?.trim() ?: ""
                                        KashmiriShawlImage(
                                            imageUrl = photo,
                                            contentDescription = product.name,
                                            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(product.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Text("₹${product.price.toInt()}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                                        }
                                        IconButton(onClick = { viewModel.toggleWishlist(product.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditProfile) {
        AlertDialog(
            onDismissRequest = { showEditProfile = false },
            title = { Text("Manage Account Details") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editAddress,
                        onValueChange = { editAddress = it },
                        label = { Text("Saved Address") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateUserProfile(editName, editPhone)
                        showEditProfile = false
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfile = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (reviewProductId != null) {
        AlertDialog(
            onDismissRequest = { reviewProductId = null },
            title = { Text("Write a Product Review") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Rating (1-5 Stars)")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        (1..5).forEach { star ->
                            IconButton(onClick = { reviewRating = star }) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = "$star stars",
                                    tint = if (star <= reviewRating) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        label = { Text("Write your review comment...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addReview(reviewProductId!!, reviewRating, reviewComment) {
                            reviewProductId = null
                            reviewComment = ""
                        }
                    }
                ) {
                    Text("Submit Review")
                }
            },
            dismissButton = {
                TextButton(onClick = { reviewProductId = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileMenuButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = if (isDestructive) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Go",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun OrderCard(order: OrderEntity, onCancel: () -> Unit, onReviewClick: (Long) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Order #${order.orderNumber}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(text = order.orderStatus, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
            }
            Text(text = "Amount: ₹${order.totalAmount} | Payment: ${order.paymentMethod} (${order.paymentStatus})", style = MaterialTheme.typography.bodySmall)
            Text(text = "Delivery Address: ${order.address}, ${order.village}, ${order.district}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                if (order.orderStatus == "Order Placed" || order.orderStatus == "Seller Confirmed") {
                    TextButton(onClick = onCancel) {
                        Text("Cancel Order", color = MaterialTheme.colorScheme.error)
                    }
                }
                if (order.orderStatus == "Delivered") {
                    Button(onClick = { onReviewClick(1L) }) { // sample productId 1 or order items
                        Text("Give Review")
                    }
                }
            }
        }
    }
}
