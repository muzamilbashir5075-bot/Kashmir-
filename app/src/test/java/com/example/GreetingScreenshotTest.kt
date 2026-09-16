package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.ProductEntity
import com.example.ui.buyer.KashmiriShawlDetailedCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleShawl = ProductEntity(
        sellerId = 1L,
        name = "Authentic Kanihama Kani Weave Pashmina Shawl",
        designId = "KSM-KN-005",
        price = 38000.0,
        discountPrice = 34500.0,
        stock = 4,
        color = "Emerald Green & Gold",
        material = "Pure Pashmina",
        workType = "Kani Weave",
        size = "100 x 200 cm",
        description = "Mastercrafted using traditional eyeless wooden spools in Kanihama, Kashmir. Features intricate royal paisley motifs.",
        category = "Kashmiri Shawls",
        imagesJson = "drawable://img_shawl_kani"
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        KashmiriShawlDetailedCard(
            product = sampleShawl,
            isWishlisted = false,
            onProductClick = {},
            onWishlistToggle = {},
            onAddToCart = {},
            onChatArtisan = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
