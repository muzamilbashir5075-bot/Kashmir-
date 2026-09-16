@Composable
fun CheckoutModal(
    cartWithProducts: List<Pair<CartItemEntity, ProductEntity>>,
    viewModel: MarketViewModel,
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
