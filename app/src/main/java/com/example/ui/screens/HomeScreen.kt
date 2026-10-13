package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ProductCard
import com.example.ui.components.StylekBottomBar
import com.example.ui.components.StylekHomeHeader
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppStrings
import com.example.ui.navigation.Screen
import com.example.ui.theme.StylekBackground
import com.example.ui.theme.StylekBorder
import com.example.ui.theme.StylekCard
import com.example.ui.theme.StylekCardElevated
import com.example.ui.theme.StylekGold
import com.example.ui.theme.StylekPrimaryBlue
import com.example.ui.theme.StylekSecondaryBlue
import com.example.ui.theme.StylekTextPrimary
import com.example.ui.theme.StylekTextSecondary
import com.example.ui.viewmodel.StylekViewModel

@Composable
fun HomeScreen(
    viewModel: StylekViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit
) {
    val approvedProducts by viewModel.approvedProducts.collectAsState()
    val bestSellers by viewModel.bestSellers.collectAsState()
    val discountedProducts by viewModel.discountedProducts.collectAsState()
    val approvedMerchants by viewModel.approvedMerchants.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()

    val categories = listOf(
        Triple("cat_men", AppStrings.get("cat_men", language), Icons.Default.Person),
        Triple("cat_women", AppStrings.get("cat_women", language), Icons.Default.Face),
        Triple("cat_kids", AppStrings.get("cat_kids", language), Icons.Default.ChildCare),
        Triple("cat_shoes", AppStrings.get("cat_shoes", language), Icons.Default.Checkroom),
        Triple("cat_bags", AppStrings.get("cat_bags", language), Icons.Default.ShoppingBag),
        Triple("cat_watches", AppStrings.get("cat_watches", language), Icons.Default.Watch),
        Triple("cat_perfumes", AppStrings.get("cat_perfumes", language), Icons.Default.Diamond),
        Triple("cat_beauty", AppStrings.get("cat_beauty", language), Icons.Default.Face),
        Triple("cat_accessories", AppStrings.get("cat_accessories", language), Icons.Default.Diamond)
    )

    Scaffold(
        topBar = {
            StylekHomeHeader(
                language = language,
                cartCount = cartItems.sumOf { it.quantity },
                hasUnreadNotifications = false,
                onNotificationsClick = { onNavigate(Screen.Orders) },
                onCartClick = { onNavigate(Screen.Cart) },
                onProfileClick = { onNavigate(Screen.Profile) }
            )
        },
        bottomBar = {
            StylekBottomBar(
                currentScreen = Screen.Home,
                language = language,
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(StylekBackground)
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 750.dp)
                    .align(Alignment.Center),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Search Bar (Dark Card with border & filter icon)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Surface(
                            onClick = { onNavigate(Screen.Search) },
                            shape = RoundedCornerShape(14.dp),
                            color = StylekCard,
                            border = BorderStroke(1.dp, StylekBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("home_search_bar_clickable")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = StylekTextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (language == AppLanguage.ARABIC) "ابحث عن منتج، متجر، أو تصنيف..." else "Ürün, mağaza veya kategori ara...",
                                        color = StylekTextSecondary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Filters",
                                    tint = StylekPrimaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Reference Hero Banner: خصومات تصل إلى 70%
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .height(185.dp),
                        colors = CardDefaults.cardColors(containerColor = StylekCard),
                        border = BorderStroke(1.dp, StylekBorder)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = R.drawable.stylek_home_banner_1791206791539,
                                contentDescription = "Stylek Hero Banner",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                StylekBackground.copy(alpha = 0.96f),
                                                StylekBackground.copy(alpha = 0.80f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "خصومات تصل إلى",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "70%",
                                    color = StylekGold,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (-1).sp
                                )
                                Text(
                                    text = "تسوّق بأناقة مع ستايلك",
                                    color = StylekTextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    onClick = { onNavigate(Screen.Budget) },
                                    shape = RoundedCornerShape(20.dp),
                                    color = StylekGold,
                                    modifier = Modifier.testTag("banner_cta_discover_btn")
                                ) {
                                    Text(
                                        text = "تسوق الآن",
                                        color = StylekBackground,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Two Hero Feature Cards side-by-side: حدد ميزانيتك & Stylek AI
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Card 1: حدد ميزانيتك (Reference Design)
                        Card(
                            onClick = { onNavigate(Screen.Budget) },
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = StylekCard),
                            border = BorderStroke(1.dp, StylekBorder),
                            modifier = Modifier
                                .weight(1f)
                                .height(125.dp)
                                .testTag("home_premium_budget_card")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(StylekCardElevated, shape = CircleShape)
                                            .border(1.dp, StylekBorder, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.AccountBalanceWallet,
                                            contentDescription = null,
                                            tint = StylekGold,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = StylekTextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "تحديد ميزانيتي",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = StylekTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "اكتشف أفضل خيارات ميزانيتك",
                                        fontSize = 11.sp,
                                        color = StylekTextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        // Card 2: Stylek AI (Reference Design)
                        Card(
                            onClick = { onNavigate(Screen.AiStylist) },
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = StylekCardElevated),
                            border = BorderStroke(1.dp, StylekPrimaryBlue.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(125.dp)
                                .testTag("home_premium_ai_card")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(StylekPrimaryBlue, shape = CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = StylekPrimaryBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Stylek AI",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "مستشارك الذكي للأزياء",
                                        fontSize = 11.sp,
                                        color = StylekGold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                // Reference Categories Grid / Circular Scroll ("التصنيفات")
                item {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Text(
                            text = "التصنيفات",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = StylekTextPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(categories) { (key, label, icon) ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.filterCategory.value = key
                                            onNavigate(Screen.Search)
                                        }
                                        .testTag("category_chip_$key")
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(CircleShape)
                                            .background(StylekCard)
                                            .border(1.dp, StylekBorder, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = label,
                                            tint = StylekTextPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = StylekTextSecondary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                // Featured Stores Section
                item {
                    Column(modifier = Modifier.padding(top = 22.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "المتاجر المميزة",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = StylekTextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        if (approvedMerchants.isEmpty()) {
                            EmptyStateView(
                                icon = Icons.Default.Storefront,
                                title = "لا توجد متاجر متاحة حاليًا",
                                message = "ابدأ بإضافة متاجر إلى ستايلك لتظهر في هذا القسم."
                            )
                        } else {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(approvedMerchants) { merchant ->
                                    Card(
                                        onClick = { onNavigate(Screen.StoreDetail(merchant.id)) },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = StylekCard),
                                        border = BorderStroke(1.dp, StylekBorder),
                                        modifier = Modifier
                                            .width(145.dp)
                                            .testTag("store_card_${merchant.id}")
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(14.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .clip(CircleShape)
                                                    .background(StylekCardElevated)
                                                    .border(1.dp, StylekBorder, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.Storefront,
                                                    contentDescription = null,
                                                    tint = StylekPrimaryBlue,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = merchant.storeName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = StylekTextPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = merchant.city,
                                                fontSize = 11.sp,
                                                color = StylekTextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Latest Products Section
                item {
                    Column(modifier = Modifier.padding(top = 22.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "أحدث المنتجات",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = StylekTextPrimary
                            )
                            TextButton(onClick = { onNavigate(Screen.Search) }) {
                                Text(
                                    text = "عرض الكل",
                                    color = StylekPrimaryBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))

                        if (approvedProducts.isEmpty()) {
                            EmptyStateView(
                                icon = Icons.Default.ShoppingBag,
                                title = "لا توجد منتجات متاحة حاليًا",
                                message = "سيتم إدراج المنتجات فور اعتمادها من قبل إدارة المنصة."
                            )
                        } else {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                items(approvedProducts.take(10)) { product ->
                                    Box(modifier = Modifier.width(180.dp)) {
                                        ProductCard(
                                            product = product,
                                            language = language,
                                            onCardClick = { onNavigate(Screen.ProductDetail(product.id)) },
                                            onAddToCartClick = {
                                                val s = product.sizes.split(",").firstOrNull()?.trim() ?: "M"
                                                val c = product.colors.split(",").firstOrNull()?.trim() ?: "Default"
                                                viewModel.addToCart(product.id, s, c)
                                            },
                                            onToggleFavorite = {
                                                viewModel.toggleFavorite(product.id)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Special Offers Section (if any)
                if (discountedProducts.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(top = 22.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.LocalOffer,
                                    contentDescription = null,
                                    tint = StylekGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "عروض خاصة",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    color = StylekTextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                items(discountedProducts) { product ->
                                    Box(modifier = Modifier.width(180.dp)) {
                                        ProductCard(
                                            product = product,
                                            language = language,
                                            onCardClick = { onNavigate(Screen.ProductDetail(product.id)) },
                                            onAddToCartClick = {
                                                val s = product.sizes.split(",").firstOrNull()?.trim() ?: "M"
                                                val c = product.colors.split(",").firstOrNull()?.trim() ?: "Default"
                                                viewModel.addToCart(product.id, s, c)
                                            },
                                            onToggleFavorite = {
                                                viewModel.showToast("تم حفظ العرض في المفضلة")
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Best Sellers Section (if any)
                if (bestSellers.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(top = 22.dp)) {
                            Text(
                                text = "الأكثر مبيعاً",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = StylekTextPrimary,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                items(bestSellers.take(6)) { product ->
                                    Box(modifier = Modifier.width(180.dp)) {
                                        ProductCard(
                                            product = product,
                                            language = language,
                                            onCardClick = { onNavigate(Screen.ProductDetail(product.id)) },
                                            onAddToCartClick = {
                                                val s = product.sizes.split(",").firstOrNull()?.trim() ?: "M"
                                                val c = product.colors.split(",").firstOrNull()?.trim() ?: "Default"
                                                viewModel.addToCart(product.id, s, c)
                                            },
                                            onToggleFavorite = {
                                                viewModel.showToast("تم الحفظ في المفضلة")
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
