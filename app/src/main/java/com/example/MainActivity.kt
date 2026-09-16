package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.MarketViewModel
import com.example.ui.admin.AdminMainScreen
import com.example.ui.auth.AuthScreen
import com.example.ui.buyer.BuyerMainScreen
import com.example.ui.buyer.ProductDetailScreen
import com.example.ui.chat.ChatScreen
import com.example.ui.seller.SellerMainScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MarketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val currentScreen by viewModel.currentScreen.collectAsState()
                    val selectedProductId by viewModel.selectedProductId.collectAsState()
                    val chatPartnerId by viewModel.chatPartnerId.collectAsState()

                    when {
                        chatPartnerId != null -> {
                            ChatScreen(
                                viewModel = viewModel,
                                partnerId = chatPartnerId!!,
                                onBack = { viewModel.selectChatPartner(null) }
                            )
                        }
                        selectedProductId != null -> {
                            ProductDetailScreen(
                                viewModel = viewModel,
                                productId = selectedProductId!!,
                                onBack = { viewModel.selectProduct(null) }
                            )
                        }
                        else -> {
                            when (currentScreen) {
                                "AUTH" -> AuthScreen(viewModel)
                                "BUYER_MAIN" -> BuyerMainScreen(viewModel)
                                "SELLER_MAIN" -> SellerMainScreen(viewModel)
                                "ADMIN_MAIN" -> AdminMainScreen(viewModel)
                                else -> AuthScreen(viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
