package com.example.ui.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.MarketViewModel
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun AuthScreen(viewModel: MarketViewModel) {
    var authMethod by remember { mutableStateOf("EMAIL") } // EMAIL, PHONE
    var isLogin by remember { mutableStateOf(true) }
    
    var email by remember { mutableStateOf("buyer@kashmirmarket.com") }
    var password by remember { mutableStateOf("buyer123") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("BUYER") }
    
    var otpCode by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf("") }
    var resendToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }
    var isOtpSent by remember { mutableStateOf(false) }
    var resendCountdown by remember { mutableIntStateOf(0) }
    var isResending by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Clean up any old portrait/custom photo files so no portrait pic is shown
    LaunchedEffect(Unit) {
        try {
            val oldFile = File(context.filesDir, "user_custom_bg.jpg")
            if (oldFile.exists()) {
                oldFile.delete()
            }
        } catch (_: Exception) {}
    }

    LaunchedEffect(isOtpSent, resendCountdown) {
        if (isOtpSent && resendCountdown > 0) {
            delay(1000L)
            resendCountdown -= 1
        }
    }

    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    // Luxury Royal Kashmiri Color Palette
    val goldBright = Color(0xFFFFD700)
    val goldPale = Color(0xFFFFF0B3)
    val goldWarm = Color(0xFFE5B83B)
    val royalDeepBg = Color(0xFF0D131A)

    val goldGradient = Brush.horizontalGradient(
        colors = listOf(goldBright, goldPale, goldWarm)
    )

    // Sleek text field styling for high contrast and readability on luxury background
    val transparentTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.Black.copy(alpha = 0.45f),
        unfocusedContainerColor = Color.Black.copy(alpha = 0.30f),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = goldBright,
        unfocusedBorderColor = Color.White.copy(alpha = 0.45f),
        focusedLabelColor = goldBright,
        unfocusedLabelColor = Color.White.copy(alpha = 0.85f),
        cursorColor = goldBright
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Luxury Kashmiri Pashmina gold embroidery & dark velvet background
        Image(
            painter = painterResource(id = R.drawable.img_auth_bg),
            contentDescription = "Kashmir Luxury Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Elegant dark velvet gradient scrim for luxury contrast & easy readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.50f),
                            Color.Black.copy(alpha = 0.65f),
                            Color.Black.copy(alpha = 0.82f)
                        )
                    )
                )
        )

        // Main Glass Container Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = royalDeepBg.copy(alpha = 0.70f)
            ),
            border = BorderStroke(1.5.dp, goldBright.copy(alpha = 0.50f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ==========================================
                // STYLISH BRAND HEADER: MUZAMIL BASHIR
                // ==========================================
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF2A2010).copy(alpha = 0.85f),
                                    Color(0xFF141008).copy(alpha = 0.95f)
                                )
                            )
                        )
                        .border(
                            BorderStroke(1.dp, goldBright.copy(alpha = 0.6f)),
                            RoundedCornerShape(18.dp)
                        )
                        .padding(vertical = 14.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Ornamental top badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = null,
                                tint = goldBright,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "KASHMIR SHAWL ARTISAN HOUSE",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = goldBright,
                                letterSpacing = 2.sp
                            )
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = null,
                                tint = goldBright,
                                modifier = Modifier.size(13.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // STYLISH TEXT: MUZAMIL BASHIR
                        Text(
                            text = "MUZAMIL BASHIR",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Serif,
                            color = Color.White,
                            letterSpacing = 2.5.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Subtle golden separator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth(0.65f)
                                .padding(vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(goldBright.copy(alpha = 0.4f))
                            )
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = goldBright,
                                modifier = Modifier
                                    .size(12.dp)
                                    .padding(horizontal = 2.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(goldBright.copy(alpha = 0.4f))
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // STYLISH TEXT: +919682124357 (Interactive Click-to-Call / Copy)
                        Surface(
                            shape = CircleShape,
                            color = goldBright.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, goldBright.copy(alpha = 0.7f)),
                            modifier = Modifier.clickable {
                                try {
                                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919682124357"))
                                    context.startActivity(dialIntent)
                                } catch (_: Exception) {
                                    clipboardManager.setText(AnnotatedString("+919682124357"))
                                    Toast.makeText(context, "Copied: +919682124357", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Call",
                                    tint = goldBright,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "+919682124357",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    color = goldBright
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy number",
                                    tint = goldBright.copy(alpha = 0.75f),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Seamless Login / Sign Up Page Selector Tabs
                TabRow(
                    selectedTabIndex = if (isLogin) 0 else 1,
                    containerColor = Color.Black.copy(alpha = 0.35f),
                    contentColor = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .padding(2.dp),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[if (isLogin) 0 else 1]),
                            color = goldBright,
                            height = 3.dp
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = isLogin,
                        onClick = { 
                            isLogin = true 
                            errorMessage = "" 
                            successMessage = "" 
                        },
                        text = { 
                            Text(
                                "Login", 
                                fontWeight = if (isLogin) FontWeight.Bold else FontWeight.Medium,
                                color = if (isLogin) goldBright else Color.White.copy(alpha = 0.7f),
                                fontSize = 15.sp
                            ) 
                        }
                    )
                    Tab(
                        selected = !isLogin,
                        onClick = { 
                            isLogin = false 
                            errorMessage = "" 
                            successMessage = "" 
                        },
                        text = { 
                            Text(
                                "Sign Up", 
                                fontWeight = if (!isLogin) FontWeight.Bold else FontWeight.Medium,
                                color = if (!isLogin) goldBright else Color.White.copy(alpha = 0.7f),
                                fontSize = 15.sp
                            ) 
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                
                // Auth Method Selector (Email / Phone) with luxury pill design
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = authMethod == "EMAIL",
                        onClick = { 
                            authMethod = "EMAIL" 
                            errorMessage = ""
                        },
                        label = { Text("Email", color = Color.White) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.White.copy(alpha = 0.10f),
                            selectedContainerColor = goldWarm.copy(alpha = 0.35f)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = authMethod == "EMAIL",
                            borderColor = Color.White.copy(alpha = 0.30f),
                            selectedBorderColor = goldBright
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = authMethod == "PHONE",
                        onClick = { 
                            authMethod = "PHONE" 
                            errorMessage = ""
                            isOtpSent = false
                            resendCountdown = 0
                        },
                        label = { Text("Phone OTP", color = Color.White) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.White.copy(alpha = 0.10f),
                            selectedContainerColor = goldWarm.copy(alpha = 0.35f)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = authMethod == "PHONE",
                            borderColor = Color.White.copy(alpha = 0.30f),
                            selectedBorderColor = goldBright
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))

                if (authMethod == "EMAIL") {
                    if (!isLogin) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            colors = transparentTextFieldColors,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Mobile Number") },
                            colors = transparentTextFieldColors,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        // Role selector
                        Text(
                            text = "Select Account Role", 
                            style = MaterialTheme.typography.labelMedium, 
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilterChip(
                                selected = role == "BUYER",
                                onClick = { role = "BUYER" },
                                label = { Text("Buyer", color = Color.White) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = Color.White.copy(alpha = 0.10f),
                                    selectedContainerColor = goldWarm.copy(alpha = 0.35f)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = role == "BUYER",
                                    borderColor = Color.White.copy(alpha = 0.30f),
                                    selectedBorderColor = goldBright
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = role == "SELLER",
                                onClick = { role = "SELLER" },
                                label = { Text("Seller", color = Color.White) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = Color.White.copy(alpha = 0.10f),
                                    selectedContainerColor = goldWarm.copy(alpha = 0.35f)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = role == "SELLER",
                                    borderColor = Color.White.copy(alpha = 0.30f),
                                    selectedBorderColor = goldBright
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        colors = transparentTextFieldColors,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = transparentTextFieldColors,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                } else if (authMethod == "PHONE") {
                    if (!isOtpSent) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Mobile Number (with +country code)") },
                            colors = transparentTextFieldColors,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    } else {
                        OutlinedTextField(
                            value = otpCode,
                            onValueChange = { otpCode = it },
                            label = { Text("Enter OTP Code") },
                            colors = transparentTextFieldColors,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { 
                                    isOtpSent = false 
                                    otpCode = ""
                                    resendCountdown = 0
                                },
                                colors = ButtonDefaults.textButtonColors(contentColor = goldBright)
                            ) {
                                Text("Change Number")
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (resendCountdown > 0) {
                                Text(
                                    text = "Resend OTP in ${resendCountdown}s",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.80f)
                                )
                            } else {
                                TextButton(
                                    onClick = {
                                        if (phone.isBlank()) return@TextButton
                                        isResending = true
                                        errorMessage = ""
                                        successMessage = ""
                                        viewModel.sendPhoneOtp(
                                            activity = context as Activity,
                                            phone = phone,
                                            resendToken = resendToken,
                                            onCodeSent = { vId, token ->
                                                isResending = false
                                                verificationId = vId
                                                if (token != null) resendToken = token
                                                resendCountdown = 30
                                                successMessage = "OTP resent successfully!"
                                            },
                                            onResult = { success, msg ->
                                                isResending = false
                                                if (!success) errorMessage = msg
                                            }
                                        )
                                    },
                                    enabled = !isResending,
                                    colors = ButtonDefaults.textButtonColors(contentColor = goldBright)
                                ) {
                                    Text(if (isResending) "Resending..." else "Resend OTP")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage, 
                        color = Color(0xFFFF6B6B), 
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
                if (successMessage.isNotEmpty()) {
                    Text(
                        text = successMessage, 
                        color = Color(0xFF69F0AE), 
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Button(
                    onClick = {
                        errorMessage = ""
                        successMessage = ""
                        if (authMethod == "EMAIL") {
                            if (isLogin) {
                                viewModel.loginWithEmail(email, password) { success, msg ->
                                    if (!success) errorMessage = msg
                                }
                            } else {
                                if (name.isBlank() || email.isBlank() || password.isBlank()) {
                                    errorMessage = "Please fill in all required fields"
                                    return@Button
                                }
                                viewModel.signUpWithEmail(name, email, password, phone, role) { success, msg ->
                                    if (!success) errorMessage = msg
                                }
                            }
                        } else if (authMethod == "PHONE") {
                            if (!isOtpSent) {
                                if (phone.isBlank()) {
                                    errorMessage = "Please enter phone number"
                                    return@Button
                                }
                                viewModel.sendPhoneOtp(
                                    activity = context as Activity,
                                    phone = phone,
                                    resendToken = null,
                                    onCodeSent = { vId, token ->
                                        verificationId = vId
                                        resendToken = token
                                        isOtpSent = true
                                        resendCountdown = 30
                                        successMessage = "OTP sent to $phone"
                                    },
                                    onResult = { success, msg ->
                                        if (!success) errorMessage = msg
                                    }
                                )
                            } else {
                                if (otpCode.isBlank()) {
                                    errorMessage = "Please enter OTP"
                                    return@Button
                                }
                                viewModel.verifyPhoneOtp(verificationId, otpCode, phone) { success, msg ->
                                    if (!success) errorMessage = msg
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = goldBright,
                        contentColor = Color(0xFF1E1400)
                    )
                ) {
                    val btnText = if (authMethod == "EMAIL") {
                        if (isLogin) "Login with Email" else "Sign Up with Email"
                    } else {
                        if (!isOtpSent) "Send OTP" else "Verify OTP & Login"
                    }
                    Text(text = btnText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))
                
                // Google Sign In Button
                OutlinedButton(
                    onClick = {
                        errorMessage = ""
                        viewModel.loginWithGoogle(context) { success, msg ->
                            if (!success) errorMessage = msg
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White,
                        containerColor = Color.White.copy(alpha = 0.12f)
                    ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
                ) {
                    Text(text = "Sign in with Google", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
                
                Spacer(modifier = Modifier.height(10.dp))

                if (authMethod == "EMAIL") {
                    TextButton(
                        onClick = {
                            isLogin = !isLogin
                            errorMessage = ""
                            successMessage = ""
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                    ) {
                        Text(
                            text = if (isLogin) "Don't have an account? Sign Up" else "Already have an account? Login",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))

                // Quick Demo Profiles for Instant Testing
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "DEMO PROFILES (1-TAP LOGIN)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = goldBright,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                email = "buyer@kashmirmarket.com"
                                password = "buyer123"
                                errorMessage = ""
                                viewModel.loginWithEmail("buyer@kashmirmarket.com", "buyer123") { success, msg ->
                                    if (!success) errorMessage = msg
                                }
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, goldBright.copy(alpha = 0.5f))
                        ) {
                            Text("Buyer", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                email = "seller.a@kashmirmarket.com"
                                password = "seller123"
                                errorMessage = ""
                                viewModel.loginWithEmail("seller.a@kashmirmarket.com", "seller123") { success, msg ->
                                    if (!success) errorMessage = msg
                                }
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, goldBright.copy(alpha = 0.5f))
                        ) {
                            Text("Artisan", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                email = "admin@kashmirmarket.com"
                                password = "admin123"
                                errorMessage = ""
                                viewModel.loginWithEmail("admin@kashmirmarket.com", "admin123") { success, msg ->
                                    if (!success) errorMessage = msg
                                }
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, goldBright.copy(alpha = 0.5f))
                        ) {
                            Text("Admin", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
