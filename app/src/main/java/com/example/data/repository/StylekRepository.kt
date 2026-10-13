package com.example.data.repository

import com.example.data.local.AppDatabase
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID

class StylekRepository(private val database: AppDatabase) {

    private val userDao = database.userDao()
    private val merchantDao = database.merchantDao()
    private val productDao = database.productDao()
    private val cartDao = database.cartDao()
    private val orderDao = database.orderDao()
    private val favoriteDao = database.favoriteDao()
    private val wardrobeDao = database.wardrobeDao()
    private val platformDao = database.platformDao()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _currentMerchant = MutableStateFlow<MerchantEntity?>(null)
    val currentMerchant: StateFlow<MerchantEntity?> = _currentMerchant.asStateFlow()

    fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun initializeDefaults() = withContext(Dispatchers.IO) {
        // Commission rate setting default
        if (platformDao.getSetting("commission_rate") == null) {
            platformDao.setSetting(PlatformSettingEntity("commission_rate", "1.0"))
        }

        // Internal admin system account
        val existingAdmin = userDao.getUserByEmail("admin@stylek.app")
        if (existingAdmin == null) {
            val adminUser = UserEntity(
                role = "ADMIN",
                name = "إدارة ستايلك - Stylek Admin",
                phone = "07700000000",
                email = "admin@stylek.app",
                passwordHash = hashPassword("admin123456"),
                city = "Baghdad",
                gender = "male",
                age = 35,
                status = "ACTIVE"
            )
            val adminId = userDao.insertUser(adminUser)
            platformDao.insertAdminLog(
                AdminLogEntity(
                    adminId = adminId,
                    action = "SYSTEM_INIT",
                    details = "Initialized secure internal platform administration"
                )
            )
        }
    }

    // --- Authentication ---
    suspend fun registerUser(
        name: String,
        phone: String,
        email: String,
        password: String,
        city: String,
        gender: String,
        age: Int
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val cleanPhone = phone.trim()
        val cleanEmail = email.trim()

        if (cleanPhone.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("رقم الهاتف مطلوب"))
        }
        if (userDao.getUserByPhone(cleanPhone) != null) {
            return@withContext Result.failure(IllegalArgumentException("رقم الهاتف مسجل مسبقاً"))
        }
        if (cleanEmail.isNotBlank() && userDao.getUserByEmail(cleanEmail) != null) {
            return@withContext Result.failure(IllegalArgumentException("البريد الإلكتروني مسجل مسبقاً"))
        }

        val user = UserEntity(
            role = "USER",
            name = name.trim(),
            phone = cleanPhone,
            email = cleanEmail,
            passwordHash = hashPassword(password),
            city = city.trim(),
            gender = gender,
            age = age,
            status = "ACTIVE"
        )
        val id = userDao.insertUser(user)
        val saved = user.copy(id = id)
        _currentUser.value = saved
        Result.success(saved)
    }

    suspend fun registerMerchant(
        ownerName: String,
        storeName: String,
        phone: String,
        email: String,
        password: String,
        city: String,
        address: String,
        storeType: String
    ): Result<MerchantEntity> = withContext(Dispatchers.IO) {
        val cleanPhone = phone.trim()
        val cleanEmail = email.trim()

        if (cleanPhone.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("رقم الهاتف مطلوب"))
        }
        if (userDao.getUserByPhone(cleanPhone) != null) {
            return@withContext Result.failure(IllegalArgumentException("رقم الهاتف مسجل مسبقاً"))
        }

        val user = UserEntity(
            role = "MERCHANT",
            name = ownerName.trim(),
            phone = cleanPhone,
            email = cleanEmail,
            passwordHash = hashPassword(password),
            city = city.trim(),
            gender = "unisex",
            age = 30,
            status = "ACTIVE"
        )
        val userId = userDao.insertUser(user)
        val savedUser = user.copy(id = idOr(userId))

        val merchant = MerchantEntity(
            userId = savedUser.id,
            storeName = storeName.trim(),
            ownerName = ownerName.trim(),
            phone = cleanPhone,
            email = cleanEmail,
            city = city.trim(),
            address = address.trim(),
            storeType = storeType.trim(),
            status = "PENDING"
        )
        val merchantId = merchantDao.insertMerchant(merchant)
        val savedMerchant = merchant.copy(id = merchantId)

        _currentUser.value = savedUser
        _currentMerchant.value = savedMerchant
        Result.success(savedMerchant)
    }

    private fun idOr(id: Long): Long = id

    suspend fun login(identifier: String, password: String, roleTarget: String): Result<UserEntity> =
        withContext(Dispatchers.IO) {
            val clean = identifier.trim()
            val user = userDao.getUserByPhone(clean) ?: userDao.getUserByEmail(clean)
            if (user == null) {
                return@withContext Result.failure(IllegalArgumentException("المستخدم غير موجود"))
            }

            if (user.passwordHash != hashPassword(password)) {
                return@withContext Result.failure(IllegalArgumentException("كلمة المرور غير صحيحة"))
            }

            if (user.status == "DISABLED") {
                return@withContext Result.failure(IllegalStateException("الحساب معطل من قبل الإدارة"))
            }

            if (roleTarget == "MERCHANT" && user.role != "MERCHANT") {
                return@withContext Result.failure(IllegalArgumentException("هذا الحساب ليس حساب تاجر"))
            }
            if (roleTarget == "ADMIN" && user.role != "ADMIN") {
                return@withContext Result.failure(IllegalArgumentException("غير مصرح بالدخول للإدارة"))
            }

            _currentUser.value = user
            if (user.role == "MERCHANT") {
                _currentMerchant.value = merchantDao.getMerchantByUserId(user.id)
            }
            Result.success(user)
        }

    suspend fun loginWithGoogle(accountEmail: String = "user@stylek.com", accountName: String = "مستخدم Google"): Result<UserEntity> =
        withContext(Dispatchers.IO) {
            val emailClean = accountEmail.trim().lowercase()
            var user = userDao.getUserByEmail(emailClean)
            if (user == null) {
                // Auto create user via Google Account
                val newUser = UserEntity(
                    role = "USER",
                    name = accountName,
                    phone = "0770" + (1000000..9999999).random(),
                    email = emailClean,
                    passwordHash = hashPassword("GOOGLE_AUTH_SSO"),
                    city = "بغداد",
                    gender = "male",
                    age = 25,
                    status = "ACTIVE"
                )
                val id = userDao.insertUser(newUser)
                user = newUser.copy(id = id)
            }
            _currentUser.value = user
            Result.success(user)
        }

    suspend fun loginAsGuest(): Result<UserEntity> =
        withContext(Dispatchers.IO) {
            // Find existing guest or create a seamless guest session user
            val guestEmail = "guest@stylek.iq"
            var guest = userDao.getUserByEmail(guestEmail)
            if (guest == null) {
                val newGuest = UserEntity(
                    role = "USER",
                    name = "زائر ستايلك",
                    phone = "07700000000",
                    email = guestEmail,
                    passwordHash = hashPassword("GUEST_ACCESS"),
                    city = "العراق",
                    gender = "unisex",
                    age = 25,
                    status = "ACTIVE"
                )
                val id = userDao.insertUser(newGuest)
                guest = newGuest.copy(id = id)
            }
            _currentUser.value = guest
            Result.success(guest)
        }

    fun logout() {
        _currentUser.value = null
        _currentMerchant.value = null
    }

    // --- Style Profile ---
    fun getStyleProfileFlow(userId: Long): Flow<StyleProfileEntity?> = userDao.getStyleProfileFlow(userId)
    suspend fun getStyleProfile(userId: Long): StyleProfileEntity? = withContext(Dispatchers.IO) {
        userDao.getStyleProfile(userId)
    }
    suspend fun saveStyleProfile(profile: StyleProfileEntity) = withContext(Dispatchers.IO) {
        userDao.saveStyleProfile(profile)
    }

    // --- Products ---
    val allApprovedProducts: Flow<List<ProductEntity>> = productDao.getAllApprovedProducts()
    val bestSellers: Flow<List<ProductEntity>> = productDao.getBestSellers()
    val discountedProducts: Flow<List<ProductEntity>> = productDao.getDiscountedProducts()
    val pendingProducts: Flow<List<ProductEntity>> = productDao.getPendingProducts()

    fun getProductsByMerchant(merchantId: Long): Flow<List<ProductEntity>> =
        productDao.getProductsByMerchant(merchantId)

    fun getProductsByCategory(category: String): Flow<List<ProductEntity>> =
        productDao.getProductsByCategory(category)

    fun searchProducts(query: String): Flow<List<ProductEntity>> = productDao.searchProducts(query)

    suspend fun getProductById(id: Long): ProductEntity? = withContext(Dispatchers.IO) {
        productDao.getProductById(id)
    }

    suspend fun getAllApprovedProductsSnapshot(): List<ProductEntity> = withContext(Dispatchers.IO) {
        productDao.getAllApprovedProductsSnapshot()
    }

    suspend fun addProduct(
        merchantId: Long,
        storeName: String,
        name: String,
        description: String,
        category: String,
        gender: String,
        style: String,
        price: Double,
        discountPrice: Double?,
        stockQuantity: Int,
        sizes: String,
        colors: String,
        city: String,
        sku: String,
        imageUrl: String
    ): Result<Long> = withContext(Dispatchers.IO) {
        val product = ProductEntity(
            merchantId = merchantId,
            storeName = storeName,
            name = name.trim(),
            description = description.trim(),
            category = category,
            gender = gender,
            style = style,
            price = price,
            discountPrice = discountPrice,
            stockQuantity = stockQuantity,
            sizes = sizes.trim(),
            colors = colors.trim(),
            city = city.trim(),
            sku = sku.trim(),
            imageUrl = imageUrl.trim(),
            status = "PENDING"
        )
        val id = productDao.insertProduct(product)
        Result.success(id)
    }

    suspend fun updateProductStatus(productId: Long, status: String, adminId: Long) =
        withContext(Dispatchers.IO) {
            productDao.updateProductStatus(productId, status)
            platformDao.insertAdminLog(
                AdminLogEntity(
                    adminId = adminId,
                    action = "PRODUCT_STATUS_UPDATE",
                    details = "Product ID: $productId updated to $status"
                )
            )
        }

    suspend fun deleteProduct(productId: Long) = withContext(Dispatchers.IO) {
        productDao.deleteProduct(productId)
    }

    // --- Merchants ---
    val allMerchants: Flow<List<MerchantEntity>> = merchantDao.getAllMerchants()
    val approvedMerchants: Flow<List<MerchantEntity>> = merchantDao.getApprovedMerchants()

    fun getMerchantByUserIdFlow(userId: Long): Flow<MerchantEntity?> =
        merchantDao.getMerchantByUserIdFlow(userId)

    suspend fun getMerchantById(id: Long): MerchantEntity? = withContext(Dispatchers.IO) {
        merchantDao.getMerchantById(id)
    }

    suspend fun updateMerchantStatus(merchantId: Long, status: String, adminId: Long) =
        withContext(Dispatchers.IO) {
            merchantDao.updateMerchantStatus(merchantId, status)
            platformDao.insertAdminLog(
                AdminLogEntity(
                    adminId = adminId,
                    action = "MERCHANT_STATUS_UPDATE",
                    details = "Merchant ID: $merchantId updated to $status"
                )
            )
        }

    // --- Cart ---
    fun getCartItems(userId: Long): Flow<List<CartItemEntity>> = cartDao.getCartItems(userId)

    suspend fun addToCart(
        userId: Long,
        productId: Long,
        quantity: Int,
        size: String,
        color: String
    ) = withContext(Dispatchers.IO) {
        cartDao.insertCartItem(
            CartItemEntity(
                userId = userId,
                productId = productId,
                quantity = quantity,
                selectedSize = size,
                selectedColor = color
            )
        )
    }

    suspend fun updateCartQuantity(cartItemId: Long, quantity: Int) = withContext(Dispatchers.IO) {
        if (quantity <= 0) {
            cartDao.deleteCartItem(cartItemId)
        } else {
            cartDao.updateQuantity(cartItemId, quantity)
        }
    }

    suspend fun deleteCartItem(cartItemId: Long) = withContext(Dispatchers.IO) {
        cartDao.deleteCartItem(cartItemId)
    }

    suspend fun clearCart(userId: Long) = withContext(Dispatchers.IO) {
        cartDao.clearCart(userId)
    }

    // --- Orders & Checkout ---
    fun getUserOrders(userId: Long): Flow<List<OrderEntity>> = orderDao.getUserOrders(userId)
    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()
    fun getMerchantOrders(merchantId: Long): Flow<List<OrderEntity>> =
        orderDao.getMerchantOrders(merchantId)

    suspend fun getOrderById(orderId: Long): OrderEntity? = withContext(Dispatchers.IO) {
        orderDao.getOrderById(orderId)
    }

    suspend fun getOrderItems(orderId: Long): List<OrderItemEntity> = withContext(Dispatchers.IO) {
        orderDao.getOrderItems(orderId)
    }

    suspend fun updateOrderStatus(orderId: Long, status: String) = withContext(Dispatchers.IO) {
        orderDao.updateOrderStatus(orderId, status)
        val order = orderDao.getOrderById(orderId)
        if (order != null) {
            platformDao.insertNotification(
                NotificationEntity(
                    userId = order.userId,
                    title = "تحديث حالة الطلب #${order.orderNumber}",
                    message = "أصبحت حالة طلبك الآن: $status",
                    type = "ORDER"
                )
            )
        }
    }

    suspend fun placeOrder(
        userId: Long,
        recipientName: String,
        phone: String,
        city: String,
        address: String,
        notes: String,
        paymentMethod: String,
        cartItems: List<CartItemEntity>,
        discountAmount: Double = 0.0
    ): Result<OrderEntity> = withContext(Dispatchers.IO) {
        if (cartItems.isEmpty()) {
            return@withContext Result.failure(IllegalStateException("السلة فارغة"))
        }

        // Validate stock
        var subtotal = 0.0
        val preparedItems = mutableListOf<Triple<ProductEntity, CartItemEntity, Double>>()

        for (item in cartItems) {
            val product = productDao.getProductById(item.productId)
                ?: return@withContext Result.failure(IllegalStateException("المنتج غير موجود"))
            if (product.stockQuantity < item.quantity) {
                return@withContext Result.failure(
                    IllegalStateException("الكمية المطلوبة من ${product.name} غير متوفرة بالمخزون")
                )
            }
            val price = product.discountPrice ?: product.price
            subtotal += price * item.quantity
            preparedItems.add(Triple(product, item, price))
        }

        val commissionRateStr = platformDao.getSetting("commission_rate") ?: "1.0"
        val commissionRate = commissionRateStr.toDoubleOrNull() ?: 1.0
        val commissionTotal = subtotal * (commissionRate / 100.0)
        val deliveryFee = 5000.0
        val finalTotal = (subtotal - discountAmount).coerceAtLeast(0.0) + deliveryFee

        val orderNumber = "STK-" + UUID.randomUUID().toString().take(6).uppercase()

        val order = OrderEntity(
            orderNumber = orderNumber,
            userId = userId,
            recipientName = recipientName.trim(),
            phone = phone.trim(),
            city = city.trim(),
            address = address.trim(),
            notes = notes.trim(),
            paymentMethod = paymentMethod,
            status = "PENDING",
            totalAmount = finalTotal,
            deliveryFee = deliveryFee,
            discountAmount = discountAmount,
            platformCommissionRate = commissionRate,
            platformCommissionAmount = commissionTotal
        )

        val orderId = orderDao.insertOrder(order)
        val orderItems = preparedItems.map { (product, cartItem, price) ->
            val itemCommission = (price * cartItem.quantity) * (commissionRate / 100.0)
            val merchantAmount = (price * cartItem.quantity) - itemCommission

            // Decrement stock
            productDao.updateStock(product.id, product.stockQuantity - cartItem.quantity)

            OrderItemEntity(
                orderId = orderId,
                productId = product.id,
                merchantId = product.merchantId,
                productName = product.name,
                productPrice = price,
                quantity = cartItem.quantity,
                selectedSize = cartItem.selectedSize,
                selectedColor = cartItem.selectedColor,
                merchantAmount = merchantAmount
            )
        }

        orderDao.insertOrderItems(orderItems)
        cartDao.clearCart(userId)

        platformDao.insertNotification(
            NotificationEntity(
                userId = userId,
                title = "تم تأكيد طلبك بنجاح",
                message = "رقم الطلب $orderNumber بقيمة $finalTotal د.ع",
                type = "ORDER"
            )
        )

        val savedOrder = order.copy(id = orderId)
        Result.success(savedOrder)
    }

    // --- Wardrobe ---
    fun getUserWardrobe(userId: Long): Flow<List<WardrobeItemEntity>> =
        wardrobeDao.getUserWardrobe(userId)

    suspend fun getUserWardrobeSnapshot(userId: Long): List<WardrobeItemEntity> =
        withContext(Dispatchers.IO) {
            wardrobeDao.getUserWardrobeSnapshot(userId)
        }

    suspend fun addWardrobeItem(
        userId: Long,
        name: String,
        category: String,
        color: String,
        style: String,
        notes: String
    ) = withContext(Dispatchers.IO) {
        wardrobeDao.insertItem(
            WardrobeItemEntity(
                userId = userId,
                name = name.trim(),
                category = category,
                color = color.trim(),
                style = style,
                notes = notes.trim()
            )
        )
    }

    suspend fun deleteWardrobeItem(id: Long) = withContext(Dispatchers.IO) {
        wardrobeDao.deleteItem(id)
    }

    // --- Favorites ---
    fun isFavorite(userId: Long, targetType: String, targetId: Long): Flow<Boolean> =
        favoriteDao.isFavorite(userId, targetType, targetId)

    fun getUserFavorites(userId: Long, targetType: String): Flow<List<FavoriteEntity>> =
        favoriteDao.getUserFavorites(userId, targetType)

    suspend fun toggleFavorite(userId: Long, targetType: String, targetId: Long, isFav: Boolean) =
        withContext(Dispatchers.IO) {
            if (isFav) {
                favoriteDao.removeFavorite(userId, targetType, targetId)
            } else {
                favoriteDao.addFavorite(
                    FavoriteEntity(
                        userId = userId,
                        targetType = targetType,
                        targetId = targetId
                    )
                )
            }
        }

    // --- Platform & Admin ---
    val activeBanners: Flow<List<BannerEntity>> = platformDao.getActiveBanners()
    val allBanners: Flow<List<BannerEntity>> = platformDao.getAllBanners()
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()
    val allAdminLogs: Flow<List<AdminLogEntity>> = platformDao.getAllAdminLogs()
    fun getCommissionRateFlow(): Flow<String?> = platformDao.getSettingFlow("commission_rate")

    suspend fun updateCommissionRate(rate: Double, adminId: Long) = withContext(Dispatchers.IO) {
        platformDao.setSetting(PlatformSettingEntity("commission_rate", rate.toString()))
        platformDao.insertAdminLog(
            AdminLogEntity(
                adminId = adminId,
                action = "COMMISSION_RATE_UPDATE",
                details = "Commission rate set to $rate%"
            )
        )
    }

    suspend fun addBanner(title: String, subtitle: String, imageUrl: String) =
        withContext(Dispatchers.IO) {
            platformDao.insertBanner(
                BannerEntity(
                    title = title.trim(),
                    subtitle = subtitle.trim(),
                    imageUrl = imageUrl.trim()
                )
            )
        }

    suspend fun deleteBanner(id: Long) = withContext(Dispatchers.IO) {
        platformDao.deleteBanner(id)
    }

    suspend fun updateUserStatus(userId: Long, status: String, adminId: Long) =
        withContext(Dispatchers.IO) {
            userDao.updateUserStatus(userId, status)
            platformDao.insertAdminLog(
                AdminLogEntity(
                    adminId = adminId,
                    action = "USER_STATUS_UPDATE",
                    details = "User ID: $userId status updated to $status"
                )
            )
        }

    fun getUserNotifications(userId: Long): Flow<List<NotificationEntity>> =
        platformDao.getUserNotifications(userId)

    suspend fun markNotificationRead(id: Long) = withContext(Dispatchers.IO) {
        platformDao.markNotificationRead(id)
    }
}
