package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val email: String,
    val password: String,
    val name: String,
    val phone: String,
    val role: String, // "BUYER", "SELLER", "ADMIN"
    val isSellerActive: Boolean = false,
    val avatarUrl: String = "",
    val address: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "seller_profiles")
data class SellerProfileEntity(
    @PrimaryKey val sellerId: Long,
    val shopName: String,
    val shopLogo: String = "",
    val shopBanner: String = "",
    val location: String = "Srinagar, Kashmir",
    val about: String = "Authentic handcrafted Kashmiri shawls and Pashmina directly from master artisans.",
    val totalSales: Int = 0,
    val joinedDate: String = "2025",
    val rating: Float = 4.9f,
    val reviewCount: Int = 120,
    val onlinePaymentEnabled: Boolean = true,
    val codEnabled: Boolean = true,
    val verificationStatus: String = "Approved", // "Draft", "Submitted", "Under Review", "Approved", "Rejected", "Suspended"
    val rejectionReason: String = "",
    val businessAddress: String = "",
    val village: String = "",
    val district: String = "Srinagar",
    val state: String = "Jammu and Kashmir",
    val pinCode: String = "190001",
    val panNumber: String = "",
    val gstinNumber: String = "",
    val bankAccountName: String = "",
    val bankAccountNumber: String = "",
    val ifscCode: String = "",
    val upiId: String = ""
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val sellerId: Long,
    val name: String,
    val designId: String,
    val price: Double,
    val discountPrice: Double = 0.0,
    val stock: Int = 10,
    val color: String,
    val material: String, // Pashmina, Wool, Cashmere, Silk
    val workType: String, // Sozni, Tilla, Aari, Jamawar, Plain
    val size: String, // Standard (100x200cm), Large, XL
    val description: String,
    val category: String, // Kashmiri Shawls, Sozni Work, Pashmina, Embroidered Shawls, etc.
    val imagesJson: String, // comma separated URLs or JSON list
    val videoUrl: String = "",
    val isHidden: Boolean = false,
    val isSoldOut: Boolean = false,
    val rating: Float = 4.8f,
    val reviewCount: Int = 15,
    val isApproved: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val iconName: String = "shimmer"
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val buyerId: Long,
    val productId: Long,
    val quantity: Int = 1
)

@Entity(tableName = "wishlist_items")
data class WishlistItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val buyerId: Long,
    val productId: Long
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val orderNumber: String,
    val buyerId: Long,
    val sellerId: Long,
    val totalAmount: Double,
    val deliveryCharge: Double = 0.0,
    val discount: Double = 0.0,
    val paymentMethod: String, // "Online", "COD"
    val paymentStatus: String, // "Payment Pending", "Paid", "COD Pending", "COD Collected", "Failed", "Refunded", "Cancelled"
    val orderStatus: String, // "Order Placed", "Seller Confirmed", "Packed", "Shipped", "Out for Delivery", "Delivered", "Cancelled", "Rejected", "Returned", "Refunded"
    val fullName: String,
    val phone: String,
    val address: String,
    val village: String,
    val district: String,
    val state: String,
    val pinCode: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val orderId: Long,
    val productId: Long,
    val productName: String,
    val productImageUrl: String = "",
    val price: Double,
    val quantity: Int
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val orderId: Long,
    val amount: Double,
    val method: String,
    val status: String, // "Pending", "Processing", "Paid", "Failed", "Refunded", "Cancelled"
    val transactionId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val senderId: Long,
    val receiverId: Long,
    val productId: Long = 0L,
    val orderId: Long = 0L,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val productId: Long,
    val buyerId: Long,
    val buyerName: String,
    val rating: Int, // 1 to 5
    val comment: String,
    val isVerifiedPurchase: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val reporterId: Long,
    val reportedType: String, // "Product", "Seller", "Buyer", "Message"
    val reportedId: Long,
    val reason: String, // "Fake product", "Fraud", "Wrong information", "Abuse", "Spam", "Other"
    val description: String,
    val status: String = "Pending", // "Pending", "Investigated", "Action Taken", "Dismissed"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val title: String,
    val message: String,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "seller_settlements")
data class SellerSettlementEntity(
    @PrimaryKey val sellerId: Long,
    val totalSales: Double = 0.0,
    val grossEarnings: Double = 0.0,
    val totalCommission: Double = 0.0,
    val pendingSettlement: Double = 0.0,
    val availableBalance: Double = 0.0,
    val paidAmount: Double = 0.0,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "payout_requests")
data class PayoutRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val sellerId: Long,
    val amount: Double,
    val status: String, // "Pending", "Processing", "Paid", "Failed", "Reversed"
    val payoutMethod: String, // "Bank Transfer", "UPI"
    val accountDetails: String, // "XXXX-XXXX-1234"
    val transactionId: String = "",
    val requestedAt: Long = System.currentTimeMillis(),
    val processedAt: Long = 0L
)

@Entity(tableName = "refund_requests")
data class RefundRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val orderId: Long,
    val buyerId: Long,
    val amount: Double,
    val reason: String,
    val status: String, // "Requested", "Approved", "Processing", "Refunded", "Failed", "Rejected"
    val transactionId: String = "",
    val requestedAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long = 0L
)

@Entity(tableName = "admin_configs")
data class AdminConfigEntity(
    @PrimaryKey val id: Int = 1,
    val codEnabled: Boolean = true,
    val marketplaceCommissionPercentage: Double = 5.0,
    val minimumOrderAmount: Double = 100.0,
    val autoSettlementEnabled: Boolean = false
)
