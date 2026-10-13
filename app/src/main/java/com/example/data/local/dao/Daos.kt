package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.local.entity.AdminLogEntity
import com.example.data.local.entity.BannerEntity
import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.FavoriteEntity
import com.example.data.local.entity.MerchantEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.OfferEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.OrderItemEntity
import com.example.data.local.entity.PlatformSettingEntity
import com.example.data.local.entity.ProductEntity
import com.example.data.local.entity.StyleProfileEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WardrobeItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("UPDATE users SET status = :status WHERE id = :userId")
    suspend fun updateUserStatus(userId: Long, status: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStyleProfile(profile: StyleProfileEntity)

    @Query("SELECT * FROM style_profiles WHERE userId = :userId LIMIT 1")
    fun getStyleProfileFlow(userId: Long): Flow<StyleProfileEntity?>

    @Query("SELECT * FROM style_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getStyleProfile(userId: Long): StyleProfileEntity?
}

@Dao
interface MerchantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMerchant(merchant: MerchantEntity): Long

    @Query("SELECT * FROM merchants WHERE id = :id LIMIT 1")
    suspend fun getMerchantById(id: Long): MerchantEntity?

    @Query("SELECT * FROM merchants WHERE userId = :userId LIMIT 1")
    suspend fun getMerchantByUserId(userId: Long): MerchantEntity?

    @Query("SELECT * FROM merchants WHERE userId = :userId LIMIT 1")
    fun getMerchantByUserIdFlow(userId: Long): Flow<MerchantEntity?>

    @Query("SELECT * FROM merchants ORDER BY createdAt DESC")
    fun getAllMerchants(): Flow<List<MerchantEntity>>

    @Query("SELECT * FROM merchants WHERE status = 'APPROVED' ORDER BY rating DESC")
    fun getApprovedMerchants(): Flow<List<MerchantEntity>>

    @Query("UPDATE merchants SET status = :status WHERE id = :merchantId")
    suspend fun updateMerchantStatus(merchantId: Long, status: String)
}

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProduct(id: Long)

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): ProductEntity?

    @Query("SELECT * FROM products WHERE status = 'APPROVED' ORDER BY createdAt DESC")
    fun getAllApprovedProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE status = 'APPROVED' ORDER BY salesCount DESC")
    fun getBestSellers(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE status = 'APPROVED' AND discountPrice IS NOT NULL ORDER BY createdAt DESC")
    fun getDiscountedProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE status = 'PENDING' ORDER BY createdAt DESC")
    fun getPendingProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE merchantId = :merchantId ORDER BY createdAt DESC")
    fun getProductsByMerchant(merchantId: Long): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE status = 'APPROVED' AND category = :category ORDER BY createdAt DESC")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Query("UPDATE products SET status = :status WHERE id = :productId")
    suspend fun updateProductStatus(productId: Long, status: String)

    @Query("UPDATE products SET stockQuantity = :stock WHERE id = :productId")
    suspend fun updateStock(productId: Long, stock: Int)

    @Query("""
        SELECT * FROM products 
        WHERE status = 'APPROVED' 
        AND (name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR storeName LIKE '%' || :query || '%')
    """)
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE status = 'APPROVED'")
    suspend fun getAllApprovedProductsSnapshot(): List<ProductEntity>
}

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity): Long

    @Query("SELECT * FROM cart_items WHERE userId = :userId ORDER BY addedAt DESC")
    fun getCartItems(userId: Long): Flow<List<CartItemEntity>>

    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :cartItemId")
    suspend fun updateQuantity(cartItemId: Long, quantity: Int)

    @Query("DELETE FROM cart_items WHERE id = :cartItemId")
    suspend fun deleteCartItem(cartItemId: Long)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: Long)
}

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: Long): OrderEntity?

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItems(orderId: Long): List<OrderItemEntity>

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserOrders(userId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("""
        SELECT DISTINCT o.* FROM orders o
        INNER JOIN order_items oi ON o.id = oi.orderId
        WHERE oi.merchantId = :merchantId
        ORDER BY o.createdAt DESC
    """)
    fun getMerchantOrders(merchantId: Long): Flow<List<OrderEntity>>

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, status: String)
}

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(fav: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND targetType = :targetType AND targetId = :targetId")
    suspend fun removeFavorite(userId: Long, targetType: String, targetId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND targetType = :targetType AND targetId = :targetId)")
    fun isFavorite(userId: Long, targetType: String, targetId: Long): Flow<Boolean>

    @Query("SELECT * FROM favorites WHERE userId = :userId AND targetType = :targetType ORDER BY createdAt DESC")
    fun getUserFavorites(userId: Long, targetType: String): Flow<List<FavoriteEntity>>
}

@Dao
interface WardrobeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: WardrobeItemEntity): Long

    @Query("DELETE FROM wardrobe_items WHERE id = :id")
    suspend fun deleteItem(id: Long)

    @Query("SELECT * FROM wardrobe_items WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserWardrobe(userId: Long): Flow<List<WardrobeItemEntity>>

    @Query("SELECT * FROM wardrobe_items WHERE userId = :userId")
    suspend fun getUserWardrobeSnapshot(userId: Long): List<WardrobeItemEntity>
}

@Dao
interface PlatformDao {
    @Query("SELECT * FROM banners WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveBanners(): Flow<List<BannerEntity>>

    @Query("SELECT * FROM banners ORDER BY createdAt DESC")
    fun getAllBanners(): Flow<List<BannerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanner(banner: BannerEntity): Long

    @Query("DELETE FROM banners WHERE id = :id")
    suspend fun deleteBanner(id: Long)

    @Query("SELECT * FROM offers WHERE status = 'ACTIVE' ORDER BY createdAt DESC")
    fun getActiveOffers(): Flow<List<OfferEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffer(offer: OfferEntity): Long

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserNotifications(userId: Long): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: Long)

    @Query("SELECT * FROM admin_logs ORDER BY timestamp DESC")
    fun getAllAdminLogs(): Flow<List<AdminLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdminLog(log: AdminLogEntity)

    @Query("SELECT settingValue FROM platform_settings WHERE settingKey = :key LIMIT 1")
    suspend fun getSetting(key: String): String?

    @Query("SELECT settingValue FROM platform_settings WHERE settingKey = :key LIMIT 1")
    fun getSettingFlow(key: String): Flow<String?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: PlatformSettingEntity)
}
