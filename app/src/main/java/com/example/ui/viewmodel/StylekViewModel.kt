package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.AdminLogEntity
import com.example.data.local.entity.BannerEntity
import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.FavoriteEntity
import com.example.data.local.entity.MerchantEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.OrderItemEntity
import com.example.data.local.entity.ProductEntity
import com.example.data.local.entity.StyleProfileEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WardrobeItemEntity
import com.example.data.remote.GeminiService
import com.example.data.repository.StylekRepository
import com.example.ui.localization.AppLanguage
import com.example.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class BudgetOutfitResult(
    val top: ProductEntity?,
    val bottom: ProductEntity?,
    val shoes: ProductEntity?,
    val accessory: ProductEntity?,
    val totalCost: Double,
    val remainingBudget: Double
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class StylekViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = StylekRepository(db)
    val firebaseSyncService = com.example.data.remote.FirebaseSyncService(application)
    private val geminiService = GeminiService()

    // Navigation Stack
    private val navigationStack = mutableListOf<Screen>(Screen.Splash)
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Localization & Theme
    private val _language = MutableStateFlow(AppLanguage.ARABIC)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // User & Merchant Session
    val currentUser: StateFlow<UserEntity?> = repository.currentUser
    val currentMerchant: StateFlow<MerchantEntity?> = repository.currentMerchant

    // Notifications & Messages
    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    fun showToast(msg: String) {
        viewModelScope.launch { _userMessage.emit(msg) }
    }

    // App Data Flows
    val approvedProducts: StateFlow<List<ProductEntity>> = repository.allApprovedProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bestSellers: StateFlow<List<ProductEntity>> = repository.bestSellers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val discountedProducts: StateFlow<List<ProductEntity>> = repository.discountedProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val approvedMerchants: StateFlow<List<MerchantEntity>> = repository.approvedMerchants
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeBanners: StateFlow<List<BannerEntity>> = repository.activeBanners
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart
    val cartItems: StateFlow<List<CartItemEntity>> = currentUser.flatMapLatest { user ->
        if (user != null) repository.getCartItems(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User Orders
    val userOrders: StateFlow<List<OrderEntity>> = currentUser.flatMapLatest { user ->
        if (user != null) repository.getUserOrders(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User Wardrobe
    val userWardrobe: StateFlow<List<WardrobeItemEntity>> = currentUser.flatMapLatest { user ->
        if (user != null) repository.getUserWardrobe(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User Favorites
    val userFavorites: StateFlow<List<FavoriteEntity>> = currentUser.flatMapLatest { user ->
        if (user != null) repository.getUserFavorites(user.id, "PRODUCT") else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Style Profile
    val userStyleProfile: StateFlow<StyleProfileEntity?> = currentUser.flatMapLatest { user ->
        if (user != null) repository.getStyleProfileFlow(user.id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Admin & Merchant flows
    val pendingMerchants: StateFlow<List<MerchantEntity>> = repository.allMerchants
        .combine(currentUser) { list, user ->
            if (user?.role == "ADMIN") list.filter { it.status == "PENDING" } else emptyList()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMerchants: StateFlow<List<MerchantEntity>> = repository.allMerchants
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingProducts: StateFlow<List<ProductEntity>> = repository.pendingProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAdminLogs: StateFlow<List<AdminLogEntity>> = repository.allAdminLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val commissionRate: StateFlow<String> = repository.getCommissionRateFlow()
        .combine(flowOf("1.0")) { rate, def -> rate ?: def }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "1.0")

    // Merchant specific orders & products
    val merchantProducts: StateFlow<List<ProductEntity>> = currentMerchant.flatMapLatest { merchant ->
        if (merchant != null) repository.getProductsByMerchant(merchant.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val merchantOrders: StateFlow<List<OrderEntity>> = currentMerchant.flatMapLatest { merchant ->
        if (merchant != null) repository.getMerchantOrders(merchant.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search & Filter State
    val searchQuery = MutableStateFlow("")
    val filterCategory = MutableStateFlow<String?>(null)
    val filterGender = MutableStateFlow<String?>(null)
    val filterStyle = MutableStateFlow<String?>(null)
    val filterCity = MutableStateFlow<String?>(null)
    val filterMinPrice = MutableStateFlow<Double?>(null)
    val filterMaxPrice = MutableStateFlow<Double?>(null)
    val sortBy = MutableStateFlow("RELEVANCE") // RELEVANCE, PRICE_LOW, PRICE_HIGH, RATING, NEWEST, SALES

    // Filtered Products for Search
    val searchResults: StateFlow<List<ProductEntity>> = combine(
        approvedProducts,
        searchQuery,
        filterCategory,
        filterGender,
        filterStyle,
        filterMinPrice,
        filterMaxPrice,
        sortBy
    ) { args ->
        @Suppress("UNCHECKED_CAST")
        val list = args[0] as List<ProductEntity>
        val query = args[1] as String
        val cat = args[2] as String?
        val gender = args[3] as String?
        val style = args[4] as String?
        val minP = args[5] as Double?
        val maxP = args[6] as Double?
        val sort = args[7] as String

        var filtered = list.filter { p ->
            val matchesQuery = query.isBlank() ||
                    p.name.contains(query, ignoreCase = true) ||
                    p.description.contains(query, ignoreCase = true) ||
                    p.storeName.contains(query, ignoreCase = true)
            val matchesCat = cat == null || p.category == cat
            val matchesGender = gender == null || p.gender == gender || p.gender == "unisex"
            val matchesStyle = style == null || p.style == style
            val price = p.discountPrice ?: p.price
            val matchesMin = minP == null || price >= minP
            val matchesMax = maxP == null || price <= maxP

            matchesQuery && matchesCat && matchesGender && matchesStyle && matchesMin && matchesMax
        }

        when (sort) {
            "PRICE_LOW" -> filtered.sortedBy { it.discountPrice ?: it.price }
            "PRICE_HIGH" -> filtered.sortedByDescending { it.discountPrice ?: it.price }
            "RATING" -> filtered.sortedByDescending { it.rating }
            "NEWEST" -> filtered.sortedByDescending { it.createdAt }
            "SALES" -> filtered.sortedByDescending { it.salesCount }
            else -> filtered
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Budget Stylist State
    val budgetAmountInput = MutableStateFlow("150000")
    val budgetOccasion = MutableStateFlow("occasion_party")
    val budgetGender = MutableStateFlow("male")
    val budgetStyle = MutableStateFlow("style_luxury")
    val budgetFullOutfit = MutableStateFlow(true)
    private val _budgetOutfitResult = MutableStateFlow<BudgetOutfitResult?>(null)
    val budgetOutfitResult: StateFlow<BudgetOutfitResult?> = _budgetOutfitResult.asStateFlow()

    // AI Stylist Chat
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                isUser = false,
                text = "أهلاً بك في ستايلك AI 👔✨ مستشارك الخاص للأزياء والموضة. حدد ميزانيتك ومناسبتك أو اسألني عن أي تنسيق يناسب لون بشرتك وشكل جسمك، وسأقترح عليك ملابس حقيقية متوفرة في المتاجر أو تنسيقها مع خزانتك!"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()
    val isAiThinking = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            repository.initializeDefaults()
        }
    }

    // --- Navigation Functions ---
    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            navigationStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (navigationStack.size > 1) {
            navigationStack.removeAt(navigationStack.size - 1)
            _currentScreen.value = navigationStack.last()
            return true
        }
        return false
    }

    fun resetToHome() {
        navigationStack.clear()
        navigationStack.add(Screen.Home)
        _currentScreen.value = Screen.Home
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // --- Authentication ---
    fun registerUser(
        name: String,
        phone: String,
        email: String,
        password: String,
        city: String,
        gender: String,
        age: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.registerUser(name, phone, email, password, city, gender, age)
            res.onSuccess { savedUser ->
                showToast("تم إنشاء الحساب بنجاح! مرحباً بك.")
                viewModelScope.launch {
                    firebaseSyncService.syncUserToFirestore(savedUser, password)
                }
                resetToHome()
                onSuccess()
            }.onFailure {
                showToast(it.message ?: "حدث خطأ أثناء التسجيل")
            }
        }
    }

    fun registerMerchant(
        ownerName: String,
        storeName: String,
        phone: String,
        email: String,
        password: String,
        city: String,
        address: String,
        storeType: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.registerMerchant(
                ownerName, storeName, phone, email, password, city, address, storeType
            )
            res.onSuccess { savedMerchant ->
                showToast("تم تسجيل طلب المتجر بنجاح وهو قيد المراجعة.")
                viewModelScope.launch {
                    firebaseSyncService.syncMerchantToFirestore(savedMerchant)
                }
                navigateTo(Screen.MerchantDashboard)
                onSuccess()
            }.onFailure {
                showToast(it.message ?: "حدث خطأ أثناء تسجيل التاجر")
            }
        }
    }

    fun login(identifier: String, pass: String, roleTarget: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val res = repository.login(identifier, pass, roleTarget)
            res.onSuccess { user ->
                showToast("أهلاً بك مجدداً، ${user.name}!")
                when (user.role) {
                    "ADMIN" -> navigateTo(Screen.AdminDashboard)
                    "MERCHANT" -> navigateTo(Screen.MerchantDashboard)
                    else -> resetToHome()
                }
                onSuccess()
            }.onFailure {
                showToast(it.message ?: "فشل تسجيل الدخول")
            }
        }
    }

    fun loginWithGoogle(accountEmail: String = "google.user@stylek.iq", accountName: String = "مستخدم Google", onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val res = repository.loginWithGoogle(accountEmail, accountName)
            res.onSuccess { user ->
                showToast("تم تسجيل الدخول بحساب Google بنجاح! مرحباً ${user.name}")
                resetToHome()
                onSuccess()
            }.onFailure {
                showToast(it.message ?: "فشل تسجيل الدخول عبر Google")
            }
        }
    }

    fun loginAsGuest(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val res = repository.loginAsGuest()
            res.onSuccess {
                showToast("مرحباً بك كزائر في ستايلك! يمكنك التصفح واكتشاف المنتجات.")
                resetToHome()
                onSuccess()
            }.onFailure {
                showToast(it.message ?: "فشل الدخول كزائر")
            }
        }
    }

    fun logout() {
        repository.logout()
        navigationStack.clear()
        navigationStack.add(Screen.Welcome)
        _currentScreen.value = Screen.Welcome
        showToast("تم تسجيل الخروج بنجاح")
    }

    // --- Style Profile ---
    fun updateStyleProfile(profile: StyleProfileEntity) {
        viewModelScope.launch {
            repository.saveStyleProfile(profile)
            showToast("تم حفظ مواصفات الستايل بنجاح")
        }
    }

    // --- Cart Actions ---
    fun addToCart(productId: Long, size: String, color: String, quantity: Int = 1) {
        val user = currentUser.value
        if (user == null) {
            showToast("يرجى تسجيل الدخول أولاً لإضافة منتجات للسلة")
            navigateTo(Screen.Login)
            return
        }
        viewModelScope.launch {
            repository.addToCart(user.id, productId, quantity, size, color)
            showToast("تمت الإضافة إلى سلة التسوق 🛒")
        }
    }

    fun updateCartQuantity(cartItemId: Long, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(cartItemId, quantity)
        }
    }

    fun deleteCartItem(cartItemId: Long) {
        viewModelScope.launch {
            repository.deleteCartItem(cartItemId)
            showToast("تم حذف المنتج من السلة")
        }
    }

    // --- Checkout & Orders ---
    fun placeOrder(
        recipientName: String,
        phone: String,
        city: String,
        address: String,
        notes: String,
        paymentMethod: String,
        onSuccess: (OrderEntity) -> Unit
    ) {
        val user = currentUser.value ?: return
        val items = cartItems.value
        if (items.isEmpty()) {
            showToast("السلة فارغة")
            return
        }
        viewModelScope.launch {
            val res = repository.placeOrder(
                user.id, recipientName, phone, city, address, notes, paymentMethod, items
            )
            res.onSuccess { order ->
                showToast("تم تأكيد الطلب بنجاح! رقم الطلب: ${order.orderNumber}")
                viewModelScope.launch {
                    val mIds = items.map { it.productId.toString() }
                    firebaseSyncService.syncOrderToFirestore(order, mIds)
                }
                navigateTo(Screen.Orders)
                onSuccess(order)
            }.onFailure {
                showToast(it.message ?: "فشل إتمام الطلب")
            }
        }
    }

    // --- Budget Stylist Calculation ---
    fun calculateBudgetOutfit() {
        val budget = budgetAmountInput.value.toDoubleOrNull() ?: 150000.0
        val products = approvedProducts.value
        val gender = budgetGender.value
        val style = budgetStyle.value

        val genderFiltered = products.filter {
            it.gender == gender || it.gender == "unisex"
        }

        // Categories:
        // Tops: cat_men / cat_women
        // Bottoms: products with 'بنطلون' or 'جينز' or 'pants' or category cat_men/cat_women
        // Shoes: cat_shoes
        // Accessory: cat_watches, cat_accessories, cat_perfumes, cat_bags

        val tops = genderFiltered.filter { it.category == "cat_men" || it.category == "cat_women" }
        val shoes = genderFiltered.filter { it.category == "cat_shoes" }
        val accessories = genderFiltered.filter {
            it.category == "cat_accessories" || it.category == "cat_watches" || it.category == "cat_perfumes" || it.category == "cat_bags"
        }

        // Find top, shoes, accessory within budget
        var bestOutfit: BudgetOutfitResult? = null
        var maxSpent = 0.0

        for (top in tops) {
            val topPrice = top.discountPrice ?: top.price
            if (topPrice > budget) continue

            val remainingAfterTop = budget - topPrice
            val eligibleShoes = shoes.filter { (it.discountPrice ?: it.price) <= remainingAfterTop }

            if (eligibleShoes.isEmpty()) {
                val spent = topPrice
                if (spent > maxSpent && spent <= budget) {
                    maxSpent = spent
                    bestOutfit = BudgetOutfitResult(top, null, null, null, spent, budget - spent)
                }
                continue
            }

            for (shoe in eligibleShoes) {
                val shoePrice = shoe.discountPrice ?: shoe.price
                val remainingAfterShoe = remainingAfterTop - shoePrice
                val eligibleAcc = accessories.filter { (it.discountPrice ?: it.price) <= remainingAfterShoe }

                val acc = eligibleAcc.firstOrNull()
                val accPrice = acc?.let { it.discountPrice ?: it.price } ?: 0.0
                val total = topPrice + shoePrice + accPrice

                if (total > maxSpent && total <= budget) {
                    maxSpent = total
                    bestOutfit = BudgetOutfitResult(top, null, shoe, acc, total, budget - total)
                }
            }
        }

        if (bestOutfit == null && products.isNotEmpty()) {
            val single = products.firstOrNull { (it.discountPrice ?: it.price) <= budget }
            if (single != null) {
                val p = single.discountPrice ?: single.price
                bestOutfit = BudgetOutfitResult(single, null, null, null, p, budget - p)
            }
        }

        _budgetOutfitResult.value = bestOutfit
    }

    fun addAllBudgetOutfitToCart() {
        val user = currentUser.value
        if (user == null) {
            showToast("يرجى تسجيل الدخول أولاً لإضافة المنتجات للسلة")
            navigateTo(Screen.Login)
            return
        }
        val outfit = _budgetOutfitResult.value ?: return
        viewModelScope.launch {
            outfit.top?.let {
                repository.addToCart(user.id, it.id, 1, it.sizes.split(",").firstOrNull()?.trim() ?: "M", it.colors.split(",").firstOrNull()?.trim() ?: "Default")
            }
            outfit.shoes?.let {
                repository.addToCart(user.id, it.id, 1, it.sizes.split(",").firstOrNull()?.trim() ?: "42", it.colors.split(",").firstOrNull()?.trim() ?: "Default")
            }
            outfit.accessory?.let {
                repository.addToCart(user.id, it.id, 1, "Standard", it.colors.split(",").firstOrNull()?.trim() ?: "Default")
            }
            showToast("تمت إضافة الإطلالة الكاملة إلى السلة بنجاح!")
            navigateTo(Screen.Cart)
        }
    }

    fun addOutfitToCart(outfit: BudgetOutfitResult) {
        _budgetOutfitResult.value = outfit
        addAllBudgetOutfitToCart()
    }

    suspend fun getProductById(id: Long): ProductEntity? {
        return repository.getProductById(id)
    }

    fun saveStyleProfile(
        heightCm: Int,
        weightKg: Int,
        size: String,
        skinTone: String,
        styleType: String,
        favoriteColors: String
    ) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val profile = StyleProfileEntity(
                userId = user.id,
                heightCm = heightCm,
                weightKg = weightKg,
                size = size,
                skinTone = skinTone,
                styleType = styleType,
                favoriteColors = favoriteColors
            )
            repository.saveStyleProfile(profile)
            firebaseSyncService.syncUserToFirestore(user, profile = profile)
        }
    }

    fun toggleFavorite(productId: Long) {
        val user = currentUser.value
        if (user == null) {
            showToast("يرجى تسجيل الدخول لحفظ المنتجات في المفضلة")
            return
        }
        viewModelScope.launch {
            val isFav = userFavorites.value.any { it.targetId == productId }
            repository.toggleFavorite(user.id, "PRODUCT", productId, isFav)
            showToast(if (isFav) "تمت الإزالة من المفضلة" else "تمت الإضافة إلى المفضلة ❤️")
        }
    }

    // --- Wardrobe ---
    fun addWardrobeItem(name: String, category: String, color: String, style: String, notes: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.addWardrobeItem(user.id, name, category, color, style, notes)
            showToast("تمت إضافة القطعة إلى خزانتك 👔")
        }
    }

    fun deleteWardrobeItem(id: Long) {
        viewModelScope.launch {
            repository.deleteWardrobeItem(id)
            showToast("تم حذف القطعة من الخزانة")
        }
    }

    fun askAiAboutWardrobeItem(item: WardrobeItemEntity) {
        navigateTo(Screen.AiStylist)
        sendAiMessage("عندي في خزانتي: ${item.name} بلون ${item.color} وستايل ${item.style}. اقترح لي ملابس مناسبة من المتاجر لتنسيق إطلالة فخمة مع هذه القطعة.")
    }

    // --- AI Stylist Chat ---
    fun sendAiMessage(promptText: String) {
        val clean = promptText.trim()
        if (clean.isBlank()) return

        val userMsg = ChatMessage(isUser = true, text = clean)
        _chatMessages.value = _chatMessages.value + userMsg
        isAiThinking.value = true

        viewModelScope.launch {
            val user = currentUser.value
            val profile = if (user != null) repository.getStyleProfile(user.id) else null
            val products = repository.getAllApprovedProductsSnapshot()
            val wardrobe = if (user != null) repository.getUserWardrobeSnapshot(user.id) else emptyList()

            val answer = geminiService.askFashionConsultant(clean, user, profile, products, wardrobe)
            val aiMsg = ChatMessage(isUser = false, text = answer)
            _chatMessages.value = _chatMessages.value + aiMsg
            isAiThinking.value = false
        }
    }

    // --- Merchant Actions ---
    fun addProduct(
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
        imageUrl: String,
        onSuccess: () -> Unit
    ) {
        val merchant = currentMerchant.value
        if (merchant == null) {
            showToast("لا تملك متجراً مسجلاً")
            return
        }
        viewModelScope.launch {
            val res = repository.addProduct(
                merchantId = merchant.id,
                storeName = merchant.storeName,
                name = name,
                description = description,
                category = category,
                gender = gender,
                style = style,
                price = price,
                discountPrice = discountPrice,
                stockQuantity = stockQuantity,
                sizes = sizes,
                colors = colors,
                city = city,
                sku = sku,
                imageUrl = imageUrl
            )
            res.onSuccess { prodId ->
                showToast("تمت إضافة المنتج بنجاح! هو الآن بانتظار موافقة الإدارة.")
                viewModelScope.launch {
                    repository.getProductById(prodId)?.let { prod ->
                        firebaseSyncService.syncProductToFirestore(prod)
                    }
                }
                onSuccess()
            }.onFailure {
                showToast(it.message ?: "فشل إضافة المنتج")
            }
        }
    }

    fun updateMerchantOrderStatus(orderId: Long, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
            firebaseSyncService.syncOrderStatus(orderId, status)
            showToast("تم تحديث حالة الطلب إلى: $status")
        }
    }

    // --- Admin Actions ---
    fun adminApproveMerchant(merchantId: Long) {
        val admin = currentUser.value ?: return
        viewModelScope.launch {
            repository.updateMerchantStatus(merchantId, "APPROVED", admin.id)
            firebaseSyncService.syncMerchantStatus(merchantId, "APPROVED")
            showToast("تم اعتماد المتجر والتاجر بنجاح")
        }
    }

    fun adminRejectMerchant(merchantId: Long) {
        val admin = currentUser.value ?: return
        viewModelScope.launch {
            repository.updateMerchantStatus(merchantId, "REJECTED", admin.id)
            firebaseSyncService.syncMerchantStatus(merchantId, "REJECTED")
            showToast("تم رفض طلب التاجر")
        }
    }

    fun adminSuspendMerchant(merchantId: Long) {
        val admin = currentUser.value ?: return
        viewModelScope.launch {
            repository.updateMerchantStatus(merchantId, "SUSPENDED", admin.id)
            firebaseSyncService.syncMerchantStatus(merchantId, "SUSPENDED")
            showToast("تم تعليق حساب التاجر")
        }
    }

    fun adminApproveProduct(productId: Long) {
        val admin = currentUser.value ?: return
        viewModelScope.launch {
            repository.updateProductStatus(productId, "APPROVED", admin.id)
            firebaseSyncService.syncProductStatus(productId, "APPROVED")
            showToast("تمت الموافقة على المنتج ونشره للمتسوقين")
        }
    }

    fun adminRejectProduct(productId: Long) {
        val admin = currentUser.value ?: return
        viewModelScope.launch {
            repository.updateProductStatus(productId, "REJECTED", admin.id)
            firebaseSyncService.syncProductStatus(productId, "REJECTED")
            showToast("تم رفض المنتج")
        }
    }

    fun adminToggleUserStatus(userId: Long, currentStatus: String) {
        val admin = currentUser.value ?: return
        val newStatus = if (currentStatus == "ACTIVE") "DISABLED" else "ACTIVE"
        viewModelScope.launch {
            repository.updateUserStatus(userId, newStatus, admin.id)
            showToast("تم تغيير حالة المستخدم إلى $newStatus")
        }
    }

    fun adminUpdateCommission(rate: Double) {
        val admin = currentUser.value ?: return
        viewModelScope.launch {
            repository.updateCommissionRate(rate, admin.id)
            showToast("تم تحديث نسبة العمولة إلى $rate%")
        }
    }

    fun adminAddBanner(title: String, subtitle: String, imageUrl: String) {
        viewModelScope.launch {
            repository.addBanner(title, subtitle, imageUrl)
            showToast("تمت إضافة الإعلان بنجاح")
        }
    }

    fun adminDeleteBanner(id: Long) {
        viewModelScope.launch {
            repository.deleteBanner(id)
            showToast("تم حذف الإعلان")
        }
    }
}
