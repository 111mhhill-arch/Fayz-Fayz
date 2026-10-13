package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String, // "USER", "MERCHANT", "ADMIN"
    val name: String,
    val phone: String,
    val email: String,
    val passwordHash: String,
    val city: String,
    val gender: String, // "male", "female"
    val age: Int,
    val status: String = "ACTIVE", // "ACTIVE", "DISABLED"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "style_profiles")
data class StyleProfileEntity(
    @PrimaryKey val userId: Long,
    val heightCm: Int = 175,
    val weightKg: Int = 70,
    val size: String = "M",
    val bodyShape: String = "Athletic",
    val skinTone: String = "Medium / Wheatish",
    val favoriteColors: String = "Navy, Black, Beige",
    val styleType: String = "style_casual"
)

@Entity(tableName = "merchants")
data class MerchantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val storeName: String,
    val ownerName: String,
    val phone: String,
    val email: String,
    val city: String,
    val address: String,
    val storeType: String,
    val logoUrl: String = "",
    val bannerUrl: String = "",
    val description: String = "",
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED", "SUSPENDED"
    val followerCount: Int = 0,
    val rating: Float = 4.8f,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val merchantId: Long,
    val storeName: String,
    val name: String,
    val description: String,
    val category: String, // e.g., "cat_men", "cat_women", "cat_shoes"
    val gender: String, // "male", "female", "unisex", "kids"
    val style: String, // "style_casual", "style_formal", "style_luxury"
    val price: Double,
    val discountPrice: Double? = null,
    val stockQuantity: Int,
    val sizes: String = "S, M, L, XL",
    val colors: String = "Black, Navy",
    val city: String,
    val sku: String = "",
    val imageUrl: String = "",
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val rating: Float = 4.7f,
    val salesCount: Int = 0,
    val isFeatured: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val productId: Long,
    val quantity: Int,
    val selectedSize: String,
    val selectedColor: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val targetType: String, // "PRODUCT", "STORE"
    val targetId: Long,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderNumber: String,
    val userId: Long,
    val recipientName: String,
    val phone: String,
    val city: String,
    val address: String,
    val notes: String = "",
    val paymentMethod: String, // "COD", "ZAIN_CASH", "QI_CARD"
    val status: String = "PENDING", // "PENDING", "ACCEPTED", "PREPARING", "SHIPPED", "IN_TRANSIT", "DELIVERED", "CANCELLED"
    val totalAmount: Double,
    val deliveryFee: Double = 5000.0,
    val discountAmount: Double = 0.0,
    val platformCommissionRate: Double = 1.0,
    val platformCommissionAmount: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val productId: Long,
    val merchantId: Long,
    val productName: String,
    val productPrice: Double,
    val quantity: Int,
    val selectedSize: String,
    val selectedColor: String,
    val merchantAmount: Double
)

@Entity(tableName = "wardrobe_items")
data class WardrobeItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val name: String,
    val category: String,
    val color: String,
    val style: String,
    val notes: String = "",
    val imageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "offers")
data class OfferEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val merchantId: Long,
    val title: String,
    val discountPercent: Int,
    val discountAmount: Double = 0.0,
    val targetProductId: Long? = null,
    val status: String = "ACTIVE",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "banners")
data class BannerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subtitle: String,
    val imageUrl: String = "",
    val linkType: String = "CATEGORY",
    val linkTargetId: Long? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val title: String,
    val message: String,
    val type: String = "ORDER",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "admin_logs")
data class AdminLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val adminId: Long,
    val action: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "platform_settings")
data class PlatformSettingEntity(
    @PrimaryKey val settingKey: String,
    val settingValue: String
)
