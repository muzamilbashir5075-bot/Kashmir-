package com.example.ui.buyer

import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.CartItemEntity
import com.example.data.ProductEntity
import com.example.ui.MarketViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun BuyerCartTab(viewModel: MarketViewModel) {
    val cartItems by viewModel.getCartFlow().collectAsState(initial = emptyList())
    val allProducts by viewModel.allProducts.collectAsState()
    val scope = rememberCoroutineScope()

    var showCheckout by remember { mutableStateOf(false) }

    val cartWithProducts = cartItems.mapNotNull { cart ->
        val product = allProducts.find { it.id == cart.productId }
        if (product != null) cart to product else null
    }


    val totalAmount = cartWithProducts.sumOf { (cart, prod) ->
        (if (prod.discountPrice > 0) prod.discountPrice else prod.price) * cart.quantity
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Shopping Cart (${cartItems.size})",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        if (cartWithProducts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Your cart is empty", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartWithProducts) { (cart, product) ->
                    CartItemRow(cart = cart, product = product, onDelete = {
                        viewModel.removeCartItem(cart)
                    })
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("₹$totalAmount", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Delivery Charge", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Free", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                    }
                    Divider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("₹$totalAmount", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { showCheckout = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Proceed to Checkout", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showCheckout) {
        CheckoutDialog(
            viewModel = viewModel,
            cartWithProducts = cartWithProducts,
            onDismiss = { showCheckout = false },
            onSuccess = { showCheckout = false }
        )
    }
}

@Composable
fun CartItemRow(cart: CartItemEntity, product: ProductEntity, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Qty: ${cart.quantity} | ₹${if (product.discountPrice > 0) product.discountPrice else product.price}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun CheckoutDialog(
    viewModel: MarketViewModel,
    cartWithProducts: List<Pair<CartItemEntity, ProductEntity>>,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var fullName by remember { mutableStateOf(currentUser?.name ?: "") }
    var phone by remember { mutableStateOf(currentUser?.phone ?: "") }
    var address by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("Srinagar") }
    var state by remember { mutableStateOf("Jammu & Kashmir") }
    var pinCode by remember { mutableStateOf("190001") }

    val paymentMethods = remember { mutableStateMapOf<Long, String>() } // sellerId to method
    val groupedBySeller = cartWithProducts.groupBy { it.second.sellerId }

    var isProcessing by remember { mutableStateOf(false) }
    var paymentMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Secure Checkout") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (paymentMessage != null) {
                    Text(
                        text = paymentMessage!!,
                        color = if (paymentMessage!!.contains("Failed")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Text("Delivery Address", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Mobile Number") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Complete Address") }, modifier = Modifier.fillMaxWidth())
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = village, onValueChange = { village = it }, label = { Text("Village/Town") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = district, onValueChange = { district = it }, label = { Text("District") }, modifier = Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = state, onValueChange = { state = it }, label = { Text("State") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = pinCode, onValueChange = { pinCode = it }, label = { Text("PIN Code") }, modifier = Modifier.weight(1f))
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                var totalOrderAmount = 0.0
                groupedBySeller.forEach { (sellerId, items) ->
                    val sellerProfileFlow = viewModel.getSellerProfileFlow(sellerId)
                    val profile by sellerProfileFlow.collectAsState(initial = null)
                    val shopName = profile?.shopName ?: "Seller #$sellerId"
                    val onlineEnabled = profile?.onlinePaymentEnabled ?: true
                    val codEnabled = profile?.codEnabled ?: true
                    
                    val subtotal = items.sumOf { (c, p) -> (if (p.discountPrice > 0) p.discountPrice else p.price) * c.quantity }
                    val deliveryFee = 50.0 // Flat rate for demo
                    val discount = items.sumOf { (c, p) -> if (p.discountPrice > 0) (p.price - p.discountPrice) * c.quantity else 0.0 }
                    val finalPayable = subtotal + deliveryFee
                    totalOrderAmount += finalPayable
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "Order from: $shopName", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            items.forEach { (c, prod) ->
                                Text(text = "• ${prod.name} (Qty: ${c.quantity})", style = MaterialTheme.typography.bodySmall)
                            }
                            
                            // Billing Breakdown
                            Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp)) {
                                Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                        Text("Product Total:", style = MaterialTheme.typography.bodySmall)
                                        Text("₹$subtotal", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                        Text("Delivery Fee:", style = MaterialTheme.typography.bodySmall)
                                        Text("₹$deliveryFee", style = MaterialTheme.typography.bodySmall)
                                    }
                                    if (discount > 0) {
                                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                            Text("Discount/Coupon:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                            Text("-₹$discount", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                        Text("Final Payable Amount:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                        Text("₹$finalPayable", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            
                            Text("Select Payment Method:", style = MaterialTheme.typography.labelMedium)
                            
                            val availableMethods = mutableListOf<String>()
                            if (onlineEnabled) availableMethods.addAll(listOf("UPI", "Debit Card", "Credit Card", "Net Banking"))
                            if (codEnabled) availableMethods.add("Cash on Delivery")
                            
                            if (paymentMethods[sellerId] == null && availableMethods.isNotEmpty()) {
                                paymentMethods[sellerId] = availableMethods.first()
                            }
                            
                            availableMethods.forEach { method ->
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { paymentMethods[sellerId] = method }) {
                                    RadioButton(
                                        selected = paymentMethods[sellerId] == method,
                                        onClick = { paymentMethods[sellerId] = method }
                                    )
                                    Text(method, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isProcessing,
                onClick = {
                    isProcessing = true
                    paymentMessage = null
                    viewModel.placeOrders(
                        fullName = fullName,
                        phone = phone,
                        address = address,
                        village = village,
                        district = district,
                        state = state,
                        pinCode = pinCode,
                        cartItemsWithProduct = cartWithProducts,
                        paymentMethodsPerSeller = paymentMethods
                    ) { success, message ->
                        isProcessing = false
                        paymentMessage = message
                        if (success) {
                            onSuccess()
                        }
                    }
                }
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Pay Now")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isProcessing) {
                Text("Cancel")
            }
        }
    )
}
