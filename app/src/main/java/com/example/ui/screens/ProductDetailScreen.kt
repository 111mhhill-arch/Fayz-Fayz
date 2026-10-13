package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.ProductEntity
import com.example.ui.components.ProductCard
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppStrings
import com.example.ui.navigation.Screen
import com.example.ui.theme.StylekBackground
import com.example.ui.theme.StylekBackgroundSecondary
import com.example.ui.theme.StylekBorder
import com.example.ui.theme.StylekCard
import com.example.ui.theme.StylekCardElevated
import com.example.ui.theme.StylekGold
import com.example.ui.theme.StylekPrimaryBlue
import com.example.ui.theme.StylekRuby
import com.example.ui.theme.StylekTextPrimary
import com.example.ui.theme.StylekTextSecondary
import com.example.ui.viewmodel.StylekViewModel

/**
 * Screen 10: تفاصيل المنتج (Product Details) - Matching Reference Screen 10
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Long,
    viewModel: StylekViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var product by remember { mutableStateOf<ProductEntity?>(null) }
    var selectedSize by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("") }
    var quantity by remember { mutableIntStateOf(1) }
    val favorites by viewModel.userFavorites.collectAsState()
    val isFavorite = favorites.any { it.targetId == productId }
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(productId) {
        val p = viewModel.getProductById(productId)
        product = p
        if (p != null) {
            val sizes = p.sizes.split(",").map { it.trim() }.filter { it.isNotBlank() }
            if (sizes.isNotEmpty()) selectedSize = sizes.first()
            val colors = p.colors.split(",").map { it.trim() }.filter { it.isNotBlank() }
            if (colors.isNotEmpty()) selectedColor = colors.first()
        }
    }

    val colorSwatches = listOf(
        Color(0xFFE2C9A5), Color(0xFF8B5A2B), Color(0xFF2563EB), Color(0xFF000000), Color(0xFFFFFFFF)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = product?.name ?: "", maxLines = 1, color = StylekTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = StylekTextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "شاهد منتج ${product?.name} على تطبيق ستايلك Stylek!")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "مشاركة المنتج"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = StylekTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StylekBackground)
            )
        },
        bottomBar = {
            if (product != null) {
                Surface(
                    color = StylekBackgroundSecondary,
                    border = BorderStroke(1.dp, StylekBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Favorite Heart Button
                        Surface(
                            onClick = {
                                viewModel.toggleFavorite(productId)
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = StylekCard,
                            border = BorderStroke(1.dp, StylekBorder),
                            modifier = Modifier.size(50.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (isFavorite) StylekRuby else StylekTextSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        // Primary Blue Add to Cart Button (Matching Reference Screen 10)
                        Button(
                            onClick = {
                                viewModel.addToCart(productId, selectedSize, selectedColor, quantity)
                                onNavigate(Screen.Cart)
                            },
                            enabled = product!!.stockQuantity > 0,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StylekPrimaryBlue),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("product_detail_add_cart_btn")
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (product!!.stockQuantity > 0) "أضف إلى السلة" else "نفدت الكمية",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (product == null) {
            Box(modifier = Modifier.fillMaxSize().background(StylekBackground).padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = StylekPrimaryBlue)
            }
        } else {
            val p = product!!
            val hasDiscount = p.discountPrice != null && p.discountPrice < p.price
            val finalPrice = p.discountPrice ?: p.price
            val currency = AppStrings.get("currency", language)
            val discountPercent = if (hasDiscount) {
                (((p.price - p.discountPrice!!) / p.price) * 100).toInt()
            } else 0

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(StylekBackground)
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Product Main Image Box matching Reference Screen 10
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color(0xFF0D1829))
                ) {
                    if (p.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = p.imageUrl,
                            contentDescription = p.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = StylekTextSecondary, modifier = Modifier.size(60.dp))
                        }
                    }

                    if (hasDiscount) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(StylekGold)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "-$discountPercent%",
                                color = StylekBackground,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Info Section
                Column(modifier = Modifier.padding(16.dp)) {
                    // Title & Rating
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = p.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = StylekTextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = StylekGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "%.1f".format(p.rating), fontWeight = FontWeight.Bold, color = StylekTextPrimary, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Price Display
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${finalPrice.toInt()} $currency",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = StylekGold
                        )
                        if (hasDiscount) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "${p.price.toInt()} $currency",
                                fontSize = 14.sp,
                                textDecoration = TextDecoration.LineThrough,
                                color = StylekTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = StylekBorder)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Colors Selector
                    Text(text = "الألوان المتاحة", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = StylekTextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        colorSwatches.forEach { col ->
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(col)
                                    .border(1.dp, StylekBorder, CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sizes Selector (S, M, L, XL, XXL)
                    Text(text = "المقاسات", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = StylekTextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val availableSizes = if (p.sizes.isNotBlank()) {
                            p.sizes.split(",").map { it.trim() }
                        } else listOf("S", "M", "L", "XL", "XXL")

                        availableSizes.forEach { s ->
                            val isSel = selectedSize == s
                            Surface(
                                onClick = { selectedSize = s },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSel) StylekPrimaryBlue else StylekCard,
                                border = BorderStroke(1.dp, if (isSel) StylekPrimaryBlue else StylekBorder),
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = s,
                                        color = if (isSel) Color.White else StylekTextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quantity Stepper (- 1 +)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "الكمية", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = StylekTextPrimary)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(StylekCard)
                                .border(1.dp, StylekBorder, RoundedCornerShape(10.dp))
                        ) {
                            IconButton(onClick = { if (quantity > 1) quantity-- }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = StylekTextPrimary, modifier = Modifier.size(16.dp))
                            }
                            Text(
                                text = "$quantity",
                                fontWeight = FontWeight.Bold,
                                color = StylekTextPrimary,
                                modifier = Modifier.padding(horizontal = 14.dp),
                                fontSize = 14.sp
                            )
                            IconButton(onClick = { if (quantity < p.stockQuantity) quantity++ }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Add, contentDescription = "Increase", tint = StylekTextPrimary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Store Preview Card matching Reference Screen 10
                    Card(
                        onClick = { onNavigate(Screen.StoreDetail(p.merchantId)) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = StylekCard),
                        border = BorderStroke(1.dp, StylekBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(StylekCardElevated),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Storefront, contentDescription = null, tint = StylekPrimaryBlue, modifier = Modifier.size(24.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = p.storeName, fontWeight = FontWeight.Bold, color = StylekTextPrimary, fontSize = 14.sp)
                                    Text(text = p.city, fontSize = 11.sp, color = StylekTextSecondary)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = StylekPrimaryBlue,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Text(
                                    text = "زيارة المتجر",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Tabs: [الوصف | التفاصيل | المراجعات]
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = StylekCard,
                        contentColor = StylekTextPrimary,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = StylekPrimaryBlue
                            )
                        }
                    ) {
                        listOf("الوصف", "التفاصيل", "المراجعات").forEachIndexed { idx, title ->
                            Tab(
                                selected = selectedTabIndex == idx,
                                onClick = { selectedTabIndex = idx },
                                text = { Text(title, fontWeight = if (selectedTabIndex == idx) FontWeight.Bold else FontWeight.Normal, color = if (selectedTabIndex == idx) StylekPrimaryBlue else StylekTextSecondary) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    when (selectedTabIndex) {
                        0 -> Text(text = p.description.ifBlank { "منتج أصيل وعالي الجودة مقدم من متجر ${p.storeName}." }, color = StylekTextSecondary, fontSize = 13.sp, lineHeight = 20.sp)
                        1 -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = "• خامة أصلية 100% عالية الجودة ومريحة.", color = StylekTextSecondary, fontSize = 12.sp)
                            Text(text = "• بلد المنشأ والتصميم: أزياء تركية وعالمية راقية.", color = StylekTextSecondary, fontSize = 12.sp)
                            Text(text = "• الشحن: توصيل سريع لجميع المحافظات خلال 24-48 ساعة.", color = StylekTextSecondary, fontSize = 12.sp)
                        }
                        2 -> Text(text = "لا توجد مراجعات حتى الآن لهذا المنتج.", color = StylekTextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
