package com.example

import com.example.data.ProductEntity
import com.example.ui.seller.kashmiriShawlPhotoPresets
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductCatalogTest {

    @Test
    fun `test kashmiri shawl product attributes include image description and price`() {
        val shawl = ProductEntity(
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
            description = "Mastercrafted using traditional eyeless wooden spools in Kanihama.",
            category = "Kashmiri Shawls",
            imagesJson = "drawable://img_shawl_kani"
        )

        assertTrue(shawl.imagesJson.isNotEmpty())
        assertTrue(shawl.description.isNotEmpty())
        assertTrue(shawl.price > 0.0)
        assertEquals("Pure Pashmina", shawl.material)
        assertEquals("Kani Weave", shawl.workType)
    }

    @Test
    fun `test seller catalog photos support multiple images and primary cover extraction`() {
        val photos = listOf(
            "file:///data/user/0/com.example/catalog_photos/shawl_01.jpg",
            "drawable://img_shawl_kani",
            "https://images.unsplash.com/photo-1578632767115-351597cf2477"
        )
        val imagesJson = photos.joinToString(",")

        val product = ProductEntity(
            sellerId = 2L,
            name = "Royal Tilla Work Black Pashmina Shawl",
            designId = "KSM-TL-009",
            price = 28000.0,
            color = "Jet Black & Antique Gold",
            material = "Pure Pashmina",
            workType = "Tilla Zari Work",
            size = "100 x 200 cm",
            description = "Intricate metallic gold embroidery on handspun pashmina base.",
            category = "Embroidered Shawls",
            imagesJson = imagesJson
        )

        val parsedPhotos = product.imagesJson.split(",").map { it.trim() }
        assertEquals(3, parsedPhotos.size)
        assertEquals("file:///data/user/0/com.example/catalog_photos/shawl_01.jpg", parsedPhotos.first())
        assertEquals("file:///data/user/0/com.example/catalog_photos/shawl_01.jpg", parsedPhotos.firstOrNull())
    }

    @Test
    fun `test kashmiri shawl artisan photo presets are valid`() {
        assertTrue("Artisan presets should not be empty", kashmiriShawlPhotoPresets.isNotEmpty())
        for (preset in kashmiriShawlPhotoPresets) {
            assertTrue("Preset title should be valid", preset.title.isNotBlank())
            assertTrue("Preset weave type should be valid", preset.weaveType.isNotBlank())
            assertTrue("Preset image URL should be valid", preset.imageUrl.isNotBlank())
        }
    }
}
