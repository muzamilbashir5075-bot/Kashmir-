package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Users
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = 'SELLER'")
    fun getAllSellersFlow(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = 'BUYER'")
    fun getAllBuyersFlow(): Flow<List<UserEntity>>

    // Seller Profiles
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSellerProfile(profile: SellerProfileEntity)

    @Update
    suspend fun updateSellerProfile(profile: SellerProfileEntity)

    @Query("SELECT * FROM seller_profiles WHERE sellerId = :sellerId LIMIT 1")
    suspend fun getSellerProfile(sellerId: Long): SellerProfileEntity?

    @Query("SELECT * FROM seller_profiles WHERE sellerId = :sellerId LIMIT 1")
    fun getSellerProfileFlow(sellerId: Long): Flow<SellerProfileEntity?>

    @Query("SELECT * FROM seller_profiles")
    fun getAllSellerProfilesFlow(): Flow<List<SellerProfileEntity>>

    // Products
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): ProductEntity?

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    fun getProductByIdFlow(id: Long): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE isHidden = 0 AND isApproved = 1")
    fun getAllActiveProductsFlow(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isHidden = 0 AND isApproved = 1")
    suspend fun getAllActiveProducts(): List<ProductEntity>

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int

    @Query("SELECT * FROM products WHERE sellerId = :sellerId")
    fun getProductsBySellerFlow(sellerId: Long): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products")
    fun getAllProductsAdminFlow(): Flow<List<ProductEntity>>

    // Categories
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("SELECT * FROM categories")
    fun getAllCategoriesFlow(): Flow<List<CategoryEntity>>

    // Cart Items
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Update
    suspend fun updateCartItem(item: CartItemEntity)

    @Delete
    suspend fun deleteCartItem(item: CartItemEntity)

    @Query("SELECT * FROM cart_items WHERE buyerId = :buyerId")
    fun getCartItemsFlow(buyerId: Long): Flow<List<CartItemEntity>>

    @Query("DELETE FROM cart_items WHERE buyerId = :buyerId")
    suspend fun clearCart(buyerId: Long)

    // Wishlist
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlistItem(item: WishlistItemEntity)

    @Delete
    suspend fun deleteWishlistItem(item: WishlistItemEntity)

    @Query("SELECT * FROM wishlist_items WHERE buyerId = :buyerId")
    fun getWishlistItemsFlow(buyerId: Long): Flow<List<WishlistItemEntity>>

    @Query("SELECT EXISTS(SELECT * FROM wishlist_items WHERE buyerId = :buyerId AND productId = :productId)")
    suspend fun isWishlisted(buyerId: Long, productId: Long): Boolean

    // Orders
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    suspend fun getOrderById(id: Long): OrderEntity?

    @Query("SELECT * FROM orders WHERE buyerId = :buyerId ORDER BY createdAt DESC")
    fun getOrdersByBuyerFlow(buyerId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE sellerId = :sellerId ORDER BY createdAt DESC")
    fun getOrdersBySellerFlow(sellerId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrdersAdminFlow(): Flow<List<OrderEntity>>

    // Order Items
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItem(item: OrderItemEntity)

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItems(orderId: Long): List<OrderItemEntity>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItemsFlow(orderId: Long): Flow<List<OrderItemEntity>>

    // Payments
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long

    @Update
    suspend fun updatePayment(payment: PaymentEntity)

    @Query("SELECT * FROM payments WHERE orderId = :orderId LIMIT 1")
    suspend fun getPaymentForOrder(orderId: Long): PaymentEntity?

    @Query("SELECT * FROM payments")
    fun getAllPaymentsFlow(): Flow<List<PaymentEntity>>

    // Messages
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("SELECT * FROM messages WHERE (senderId = :userId1 AND receiverId = :userId2) OR (senderId = :userId2 AND receiverId = :userId1) ORDER BY timestamp ASC")
    fun getMessagesBetweenFlow(userId1: Long, userId2: Long): Flow<List<MessageEntity>>

    @Query("SELECT DISTINCT senderId FROM messages WHERE receiverId = :userId UNION SELECT DISTINCT receiverId FROM messages WHERE senderId = :userId")
    suspend fun getChatContactIds(userId: Long): List<Long>

    // Reviews
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Delete
    suspend fun deleteReview(review: ReviewEntity)

    @Query("SELECT * FROM reviews WHERE productId = :productId")
    fun getReviewsForProductFlow(productId: Long): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews")
    fun getAllReviewsAdminFlow(): Flow<List<ReviewEntity>>

    @Query("SELECT EXISTS(SELECT * FROM orders o JOIN order_items oi ON o.id = oi.orderId WHERE o.buyerId = :buyerId AND oi.productId = :productId AND o.orderStatus = 'Delivered')")
    suspend fun hasCompletedOrderForProduct(buyerId: Long, productId: Long): Boolean

    // Reports
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Update
    suspend fun updateReport(report: ReportEntity)

    @Query("SELECT * FROM reports ORDER BY createdAt DESC")
    fun getAllReportsFlow(): Flow<List<ReportEntity>>

    // Notifications
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getNotificationsForUserFlow(userId: Long): Flow<List<NotificationEntity>>

    // Settlements
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettlement(settlement: SellerSettlementEntity)
    
    @Update
    suspend fun updateSettlement(settlement: SellerSettlementEntity)
    
    @Query("SELECT * FROM seller_settlements WHERE sellerId = :sellerId")
    suspend fun getSettlement(sellerId: Long): SellerSettlementEntity?
    
    @Query("SELECT * FROM seller_settlements WHERE sellerId = :sellerId")
    fun getSettlementFlow(sellerId: Long): Flow<SellerSettlementEntity?>
    
    // Payout Requests
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayoutRequest(request: PayoutRequestEntity)
    
    @Update
    suspend fun updatePayoutRequest(request: PayoutRequestEntity)
    
    @Query("SELECT * FROM payout_requests WHERE sellerId = :sellerId ORDER BY requestedAt DESC")
    fun getPayoutRequestsFlow(sellerId: Long): Flow<List<PayoutRequestEntity>>
    
    @Query("SELECT * FROM payout_requests ORDER BY requestedAt DESC")
    fun getAllPayoutRequestsFlow(): Flow<List<PayoutRequestEntity>>
    
    // Refund Requests
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRefundRequest(request: RefundRequestEntity)
    
    @Update
    suspend fun updateRefundRequest(request: RefundRequestEntity)
    
    @Query("SELECT * FROM refund_requests WHERE buyerId = :buyerId ORDER BY requestedAt DESC")
    fun getRefundRequestsForBuyerFlow(buyerId: Long): Flow<List<RefundRequestEntity>>
    
    @Query("SELECT * FROM refund_requests ORDER BY requestedAt DESC")
    fun getAllRefundRequestsFlow(): Flow<List<RefundRequestEntity>>

    // Admin Config
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdminConfig(config: AdminConfigEntity)

    @Query("SELECT * FROM admin_configs WHERE id = 1")
    fun getAdminConfigFlow(): Flow<AdminConfigEntity?>
    
}
