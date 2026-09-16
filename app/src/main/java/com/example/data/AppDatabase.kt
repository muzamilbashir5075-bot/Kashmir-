package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        SellerProfileEntity::class,
        ProductEntity::class,
        CategoryEntity::class,
        CartItemEntity::class,
        WishlistItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        PaymentEntity::class,
        MessageEntity::class,
        SellerSettlementEntity::class,
        PayoutRequestEntity::class,
        RefundRequestEntity::class,
        AdminConfigEntity::class,
        ReviewEntity::class,
        ReportEntity::class,
        NotificationEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kashmir_shawl_market.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.appDao())
                    }
                }
            }

            suspend fun populateInitialData(dao: AppDao) {
                // 1. Categories
                val categories = listOf(
                    CategoryEntity(name = "Kashmiri Shawls", iconName = "shimmer"),
                    CategoryEntity(name = "Sozni Work", iconName = "needle"),
                    CategoryEntity(name = "Pashmina", iconName = "wool"),
                    CategoryEntity(name = "Embroidered Shawls", iconName = "embroidery"),
                    CategoryEntity(name = "Wool Shawls", iconName = "wool"),
                    CategoryEntity(name = "Designer Shawls", iconName = "design"),
                    CategoryEntity(name = "Plain Shawls", iconName = "plain"),
                    CategoryEntity(name = "Handmade", iconName = "hand"),
                    CategoryEntity(name = "New Arrivals", iconName = "new"),
                    CategoryEntity(name = "Best Sellers", iconName = "star")
                )
                categories.forEach { dao.insertCategory(it) }

                // 2. Admin User
                val adminId = dao.insertUser(
                    UserEntity(
                        email = "admin@kashmirmarket.com",
                        password = "admin123",
                        name = "Market Admin",
                        phone = "+919876543210",
                        role = "ADMIN",
                        avatarUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e"
                    )
                )

                // 3. Sellers (Seller A: COD ON, Online ON; Seller B: COD OFF, Online ON; Seller C: COD ON, Online OFF)
                val sellerAId = dao.insertUser(
                    UserEntity(
                        email = "seller.a@kashmirmarket.com",
                        password = "seller123",
                        name = "Ghulam Hassan",
                        phone = "+919700112233",
                        role = "SELLER",
                        isSellerActive = true,
                        avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d"
                    )
                )
                dao.insertSellerProfile(
                    SellerProfileEntity(
                        sellerId = sellerAId,
                        shopName = "Hassan Handloom & Pashmina",
                        location = "Zaina Kadal, Srinagar",
                        about = "Master artisan weaving genuine GI certified Pashmina and intricate Sozni embroidery for 35 years.",
                        totalSales = 340,
                        joinedDate = "2023",
                        rating = 4.9f,
                        reviewCount = 210,
                        onlinePaymentEnabled = true,
                        codEnabled = true
                    )
                )

                val sellerBId = dao.insertUser(
                    UserEntity(
                        email = "seller.b@kashmirmarket.com",
                        password = "seller123",
                        name = "Bilal Sofi",
                        phone = "+919700334455",
                        role = "SELLER",
                        isSellerActive = true,
                        avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e"
                    )
                )
                dao.insertSellerProfile(
                    SellerProfileEntity(
                        sellerId = sellerBId,
                        shopName = "Sofi Royal Tilla & Jamawar",
                        location = "Lal Chowk, Srinagar",
                        about = "Specializing in exquisite gold and silver Tilla wire embroidery on royal Cashmere shawls.",
                        totalSales = 185,
                        joinedDate = "2024",
                        rating = 4.7f,
                        reviewCount = 95,
                        onlinePaymentEnabled = true,
                        codEnabled = false // COD OFF for Seller B as per requirement example!
                    )
                )

                val sellerCId = dao.insertUser(
                    UserEntity(
                        email = "seller.c@kashmirmarket.com",
                        password = "seller123",
                        name = "Mehraj Wani",
                        phone = "+919700556677",
                        role = "SELLER",
                        isSellerActive = true,
                        avatarUrl = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7"
                    )
                )
                dao.insertSellerProfile(
                    SellerProfileEntity(
                        sellerId = sellerCId,
                        shopName = "Wani Heritage Handcrafted",
                        location = "Downtown Srinagar",
                        about = "Handspun fine Cashmere and vintage motifs crafted by generational weavers.",
                        totalSales = 92,
                        joinedDate = "2025",
                        rating = 4.8f,
                        reviewCount = 64,
                        onlinePaymentEnabled = false, // Online OFF, COD ON for Seller C as per requirement example!
                        codEnabled = true
                    )
                )

                // 4. Sample Buyer
                val buyerId = dao.insertUser(
                    UserEntity(
                        email = "buyer@kashmirmarket.com",
                        password = "buyer123",
                        name = "Aaliya Khan",
                        phone = "+919811223344",
                        role = "BUYER",
                        avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb"
                    )
                )

                // 5. Products
                val p1 = ProductEntity(
                    sellerId = sellerAId,
                    name = "Royal Jamawar Hand-Embroidered Pashmina Shawl",
                    designId = "KSM-JM-001",
                    price = 24999.0,
                    discountPrice = 19999.0,
                    stock = 5,
                    color = "Crimson Red",
                    material = "Pure Pashmina",
                    workType = "Jamawar & Sozni",
                    size = "100 x 200 cm",
                    description = "An exquisite masterpiece featuring elaborate multi-colored Sozni needlework across 100% authentic Ladakhi Pashmina wool. Handcrafted over 8 months by master artisans in Srinagar.",
                    category = "Pashmina",
                    imagesJson = "https://images.unsplash.com/photo-1606760227091-3dd870d97f1d,https://images.unsplash.com/photo-1584917865442-de89df76afd3",
                    videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
                    rating = 4.9f,
                    reviewCount = 28
                )

                val p2 = ProductEntity(
                    sellerId = sellerAId,
                    name = "Classic Sozni Embroidered Cashmere Shawl",
                    designId = "KSM-SZ-002",
                    price = 14500.0,
                    discountPrice = 12999.0,
                    stock = 8,
                    color = "Ivory Cream",
                    material = "Cashmere Wool",
                    workType = "Sozni Work",
                    size = "100 x 200 cm",
                    description = "Delicate floral Sozni embroidery worked with fine silk threads on an ultra-soft ivory cashmere drape. Perfect for formal winter evenings.",
                    category = "Sozni Work",
                    imagesJson = "https://images.unsplash.com/photo-1578632767115-351597cf2477,https://images.unsplash.com/photo-1583394838336-acd977736f90",
                    rating = 4.8f,
                    reviewCount = 19
                )

                val p3 = ProductEntity(
                    sellerId = sellerBId,
                    name = "Zardozi & Tilla Golden Thread Embroidered Shawl",
                    designId = "KSM-TL-003",
                    price = 32000.0,
                    discountPrice = 28999.0,
                    stock = 3,
                    color = "Royal Black & Gold",
                    material = "Fine Wool",
                    workType = "Tilla Work",
                    size = "100 x 200 cm",
                    description = "Stunning authentic gold Tilla metallic embroidery meticulously hand-pressed along the borders and pallu. A royal heritage piece.",
                    category = "Embroidered Shawls",
                    imagesJson = "https://images.unsplash.com/photo-1548036328-c9fa89d128fa,https://images.unsplash.com/photo-1512436991641-6745cdb1723f",
                    rating = 4.9f,
                    reviewCount = 42
                )

                val p4 = ProductEntity(
                    sellerId = sellerCId,
                    name = "Handspun Natural Undyed Pashmina Wrap",
                    designId = "KSM-NW-004",
                    price = 18000.0,
                    discountPrice = 15999.0,
                    stock = 6,
                    color = "Natural Beige",
                    material = "Handspun Pashmina",
                    workType = "Plain & Handspun",
                    size = "100 x 200 cm",
                    description = "Completely unbleached and undyed natural Pashmina fiber handspun on the traditional Charkha. Incomparable featherlight warmth.",
                    category = "Handmade",
                    imagesJson = "https://images.unsplash.com/photo-1608256246200-53e635b5b65f,https://images.unsplash.com/photo-1513094735237-8f2714d57c13",
                    rating = 4.7f,
                    reviewCount = 12
                )

                val p5 = ProductEntity(
                    sellerId = sellerAId,
                    name = "Authentic Kanihama Kani Weave Pashmina Shawl",
                    designId = "KSM-KN-005",
                    price = 38000.0,
                    discountPrice = 34500.0,
                    stock = 4,
                    color = "Emerald Green & Gold",
                    material = "Pure Pashmina",
                    workType = "Kani Weave",
                    size = "100 x 200 cm",
                    description = "Mastercrafted using traditional eyeless wooden spools (Tujis) in Kanihama, Kashmir. Features intricate royal paisley motifs woven over 9 continuous months. GI-tagged authentic Kashmiri heritage art.",
                    category = "Kashmiri Shawls",
                    imagesJson = "drawable://img_shawl_kani,https://images.unsplash.com/photo-1606760227091-3dd870d97f1d",
                    rating = 5.0f,
                    reviewCount = 31
                )

                val p6 = ProductEntity(
                    sellerId = sellerBId,
                    name = "Royal Jaalidar Sozni Hand-Spun Pashmina Shawl",
                    designId = "KSM-SZ-006",
                    price = 28500.0,
                    discountPrice = 24999.0,
                    stock = 7,
                    color = "Warm Ivory & Ruby",
                    material = "Pure Pashmina",
                    workType = "Sozni Work",
                    size = "100 x 200 cm",
                    description = "All-over Jaalidar needlework meticulously stitched with ultra-fine silk threads onto Grade-A Ladakhi Pashmina wool. Featherlight, sumptuous texture with timeless Chinar leaf motifs.",
                    category = "Sozni Work",
                    imagesJson = "drawable://img_shawl_pashmina,https://images.unsplash.com/photo-1578632767115-351597cf2477",
                    rating = 4.9f,
                    reviewCount = 24
                )

                val p7 = ProductEntity(
                    sellerId = sellerBId,
                    name = "Antique Golden Tilla Zari Pashmina Doshala",
                    designId = "KSM-TL-007",
                    price = 42000.0,
                    discountPrice = 37500.0,
                    stock = 2,
                    color = "Midnight Navy & Gold",
                    material = "Pure Pashmina",
                    workType = "Tilla Work",
                    size = "115 x 230 cm",
                    description = "Magnificent heritage Doshala adorned with genuine gold and silver wire Tilla embroidery along the borders. Draped by Kashmiri aristocracy for generations, perfect for royal wedding occasions.",
                    category = "Embroidered Shawls",
                    imagesJson = "drawable://brand_banner_bg,https://images.unsplash.com/photo-1548036328-c9fa89d128fa",
                    rating = 5.0f,
                    reviewCount = 18
                )

                val p8 = ProductEntity(
                    sellerId = sellerCId,
                    name = "Traditional Aari Crewel Floral Cashmere Shawl",
                    designId = "KSM-AR-008",
                    price = 11500.0,
                    discountPrice = 9800.0,
                    stock = 10,
                    color = "Sapphire Blue",
                    material = "Cashmere Wool",
                    workType = "Aari Embroidery",
                    size = "100 x 200 cm",
                    description = "Vibrant hand-guided Aari needle chain-stitching featuring Persian floral gardens and flowing vines. Woven from soft Cashmere wool for cozy, everyday elegance.",
                    category = "Wool Shawls",
                    imagesJson = "https://images.unsplash.com/photo-1583394838336-acd977736f90,https://images.unsplash.com/photo-1512436991641-6745cdb1723f",
                    rating = 4.8f,
                    reviewCount = 37
                )

                val p1Id = dao.insertProduct(p1)
                val p2Id = dao.insertProduct(p2)
                val p3Id = dao.insertProduct(p3)
                val p4Id = dao.insertProduct(p4)
                val p5Id = dao.insertProduct(p5)
                val p6Id = dao.insertProduct(p6)
                val p7Id = dao.insertProduct(p7)
                val p8Id = dao.insertProduct(p8)

                // 6. Sample Review
                dao.insertReview(
                    ReviewEntity(
                        productId = p1Id,
                        buyerId = buyerId,
                        buyerName = "Aaliya Khan",
                        rating = 5,
                        comment = "Breathtaking craftsmanship! The Sozni work is so refined and the Pashmina is pure luxury.",
                        isVerifiedPurchase = true
                    )
                )
            }
        }
    }
}
