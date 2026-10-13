package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EmptyStateView
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppStrings
import com.example.ui.navigation.Screen
import com.example.ui.theme.StylekGold
import com.example.ui.theme.StylekMidnight
import com.example.ui.theme.StylekRoyalBlue
import com.example.ui.viewmodel.StylekViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchantDashboardScreen(
    viewModel: StylekViewModel,
    language: AppLanguage,
    initialTab: Int = 0,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    val merchant by viewModel.currentMerchant.collectAsState()
    val products by viewModel.merchantProducts.collectAsState()
    val orders by viewModel.merchantOrders.collectAsState()
    var selectedTab by remember(initialTab) { mutableIntStateOf(initialTab) }

    val currency = AppStrings.get("currency", language)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(merchant?.storeName ?: AppStrings.get("merchant_dashboard", language), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigate(Screen.MerchantAddProduct) }, modifier = Modifier.testTag("merchant_add_product_nav_btn")) {
                        Icon(Icons.Default.Add, contentDescription = "Add Product", tint = StylekMidnight)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Approval Status Notice Banner if PENDING
            if (merchant?.status == "PENDING") {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD97706))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = AppStrings.get("merchant_pending_msg", language),
                            fontSize = 12.sp,
                            color = Color(0xFF92400E),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Tabs: Overview, Products, Orders
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("نظرة عامة") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("المنتجات (${products.size})") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("الطلبات (${orders.size})") })
            }

            when (selectedTab) {
                0 -> {
                    // Overview Dashboard
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Quick Stats Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MerchantStatCard(
                                title = AppStrings.get("merchant_orders_count", language),
                                value = "${orders.size}",
                                icon = Icons.Default.ShoppingBag,
                                color = StylekRoyalBlue,
                                modifier = Modifier.weight(1f)
                            )
                            MerchantStatCard(
                                title = AppStrings.get("merchant_products_count", language),
                                value = "${products.size}",
                                icon = Icons.Default.Inventory,
                                color = StylekGold,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        val lowStockCount = products.count { it.stockQuantity <= 3 }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MerchantStatCard(
                                title = AppStrings.get("merchant_low_stock", language),
                                value = "$lowStockCount",
                                icon = Icons.Default.Warning,
                                color = Color(0xFFEF4444),
                                modifier = Modifier.weight(1f)
                            )
                            MerchantStatCard(
                                title = "حالة المتجر",
                                value = if (merchant?.status == "APPROVED") "معتمد" else "بانتظار الموافقة",
                                icon = Icons.Default.Store,
                                color = if (merchant?.status == "APPROVED") Color(0xFF10B981) else Color(0xFFD97706),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Add Product Quick Button
                        Button(
                            onClick = { onNavigate(Screen.MerchantAddProduct) },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StylekMidnight),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("merchant_add_product_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = StylekGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = AppStrings.get("add_product", language),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                1 -> {
                    // Products List
                    if (products.isEmpty()) {
                        EmptyStateView(
                            icon = Icons.Default.Inventory,
                            title = "لا توجد منتجات مضافة",
                            message = "أضف منتجاتك لتظهر للمتسوقين بعد موافقة الإدارة.",
                            actionButtonText = AppStrings.get("add_product", language),
                            onActionClick = { onNavigate(Screen.MerchantAddProduct) }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(products) { prod ->
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = prod.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        if (prod.status == "APPROVED") Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                                        shape = RoundedCornerShape(6.dp)
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = if (prod.status == "APPROVED") "معتمد" else "بانتظار المراجعة",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (prod.status == "APPROVED") Color(0xFF16A34A) else Color(0xFFD97706)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "السعر: ${(prod.discountPrice ?: prod.price).toInt()} $currency • المخزون: ${prod.stockQuantity}",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Orders Management
                    if (orders.isEmpty()) {
                        EmptyStateView(
                            icon = Icons.Default.ShoppingBag,
                            title = "لا توجد طلبات واردة",
                            message = "ستظهر طلبات الزبائن لمنتجاتك هنا فور تأكيدها."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(orders) { order ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = "#${order.orderNumber}", fontWeight = FontWeight.Bold)
                                            Text(text = "الحالة: ${order.status}", color = StylekRoyalBlue, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = "المستلم: ${order.recipientName} - ${order.phone}", fontSize = 12.sp)
                                        Text(text = "العنوان: ${order.city} - ${order.address}", fontSize = 12.sp, color = Color.Gray)
                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Status Change Actions
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            listOf(
                                                "ACCEPTED" to "قبول",
                                                "PREPARING" to "تجهيز",
                                                "SHIPPED" to "شحن",
                                                "DELIVERED" to "تسليم",
                                                "CANCELLED" to "إلغاء"
                                            ).forEach { (code, label) ->
                                                Surface(
                                                    onClick = { viewModel.updateMerchantOrderStatus(order.id, code) },
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = if (order.status == code) StylekMidnight else MaterialTheme.colorScheme.surfaceVariant
                                                ) {
                                                    Text(
                                                        text = label,
                                                        color = if (order.status == code) Color.White else MaterialTheme.colorScheme.onSurface,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
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
        }
    }
}

@Composable
fun MerchantStatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.12f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = title, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchantAddProductScreen(
    viewModel: StylekViewModel,
    language: AppLanguage,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("cat_men") }
    var gender by remember { mutableStateOf("male") }
    var style by remember { mutableStateOf("style_casual") }
    var priceStr by remember { mutableStateOf("") }
    var discountPriceStr by remember { mutableStateOf("") }
    var stockStr by remember { mutableStateOf("10") }
    var sizes by remember { mutableStateOf("S, M, L, XL") }
    var colors by remember { mutableStateOf("Black, Navy, Beige") }
    var city by remember { mutableStateOf("بغداد") }
    var sku by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(AppStrings.get("add_product", language), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PendingActions, contentDescription = null, tint = Color(0xFFD97706))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.get("product_pending_notice", language),
                        fontSize = 11.sp,
                        color = Color(0xFF92400E)
                    )
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(AppStrings.get("product_name", language)) },
                modifier = Modifier.fillMaxWidth().testTag("add_product_name_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(AppStrings.get("product_desc", language)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Price & Discount
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text(AppStrings.get("product_price", language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag("add_product_price_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = discountPriceStr,
                    onValueChange = { discountPriceStr = it },
                    label = { Text(AppStrings.get("product_discount_price", language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Stock & SKU
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = stockStr,
                    onValueChange = { stockStr = it },
                    label = { Text(AppStrings.get("product_stock", language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag("add_product_stock_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = sku,
                    onValueChange = { sku = it },
                    label = { Text(AppStrings.get("product_sku", language)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Sizes & Colors
            OutlinedTextField(
                value = sizes,
                onValueChange = { sizes = it },
                label = { Text(AppStrings.get("product_sizes", language)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = colors,
                onValueChange = { colors = it },
                label = { Text(AppStrings.get("product_colors", language)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text(AppStrings.get("city", language)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("رابط صورة المنتج (URL)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    val p = priceStr.toDoubleOrNull()
                    val s = stockStr.toIntOrNull() ?: 1
                    if (name.isBlank() || p == null) {
                        viewModel.showToast("يرجى إدخال اسم وسعر المنتج")
                        return@Button
                    }
                    val dp = discountPriceStr.toDoubleOrNull()
                    viewModel.addProduct(
                        name = name,
                        description = description,
                        category = category,
                        gender = gender,
                        style = style,
                        price = p,
                        discountPrice = dp,
                        stockQuantity = s,
                        sizes = sizes,
                        colors = colors,
                        city = city,
                        sku = sku,
                        imageUrl = imageUrl
                    ) {
                        onBack()
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StylekMidnight),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_add_product_btn")
            ) {
                Text(
                    text = "إرسال المنتج للاعتماد",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
