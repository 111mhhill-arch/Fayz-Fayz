package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
fun AdminDashboardScreen(
    viewModel: StylekViewModel,
    language: AppLanguage,
    initialTab: Int = 0,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    val users by viewModel.allUsers.collectAsState()
    val merchants by viewModel.allMerchants.collectAsState()
    val pendingMerchants by viewModel.pendingMerchants.collectAsState()
    val pendingProducts by viewModel.pendingProducts.collectAsState()
    val orders by viewModel.allOrders.collectAsState()
    val commissionRateStr by viewModel.commissionRate.collectAsState()
    val adminLogs by viewModel.allAdminLogs.collectAsState()

    var selectedTab by remember(initialTab) { mutableIntStateOf(initialTab) }
    val currency = AppStrings.get("currency", language)

    // Calculate Platform GMV & Revenue
    val totalGMV = orders.sumOf { it.totalAmount }
    val totalCommissionRevenue = orders.sumOf { it.platformCommissionAmount }

    // Commission Edit Dialog
    var showCommissionDialog by remember { mutableStateOf(false) }
    var editRateInput by remember { mutableStateOf(commissionRateStr) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(AppStrings.get("admin_dashboard", language), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        editRateInput = commissionRateStr
                        showCommissionDialog = true
                    }) {
                        Icon(Icons.Default.Percent, contentDescription = "Commission Rate", tint = StylekGold)
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
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("المؤشرات") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("التجار (${pendingMerchants.size})") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("المنتجات (${pendingProducts.size})") })
                Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("الطلبات") })
                Tab(selected = selectedTab == 4, onClick = { selectedTab = 4 }, text = { Text("المستخدمون (${users.size})") })
            }

            when (selectedTab) {
                0 -> {
                    // Metrics & Analytics
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Revenue Hero Card
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = StylekMidnight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = AppStrings.get("admin_platform_revenue", language),
                                        color = StylekGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Surface(
                                        onClick = {
                                            editRateInput = commissionRateStr
                                            showCommissionDialog = true
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color.White.copy(alpha = 0.15f),
                                        modifier = Modifier.testTag("admin_edit_commission_rate_btn")
                                    ) {
                                        Text(
                                            text = "نسبة العمولة: $commissionRateStr%",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${totalCommissionRevenue.toInt()} $currency",
                                    color = Color.White,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "إجمالي قيمة التداولات (GMV): ${totalGMV.toInt()} $currency",
                                    color = Color.LightGray,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Stats Grid
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            AdminMiniCard(
                                title = AppStrings.get("admin_total_users", language),
                                value = "${users.size}",
                                icon = Icons.Default.People,
                                color = StylekRoyalBlue,
                                modifier = Modifier.weight(1f)
                            )
                            AdminMiniCard(
                                title = AppStrings.get("admin_total_merchants", language),
                                value = "${merchants.size}",
                                icon = Icons.Default.Store,
                                color = StylekGold,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            AdminMiniCard(
                                title = AppStrings.get("admin_pending_merchants", language),
                                value = "${pendingMerchants.size}",
                                icon = Icons.Default.Store,
                                color = Color(0xFFD97706),
                                modifier = Modifier.weight(1f)
                            )
                            AdminMiniCard(
                                title = AppStrings.get("admin_pending_products", language),
                                value = "${pendingProducts.size}",
                                icon = Icons.Default.Inventory,
                                color = Color(0xFF7C3AED),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Admin Logs Preview
                        Text(
                            text = AppStrings.get("admin_logs", language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        if (adminLogs.isEmpty()) {
                            Text(text = "لا توجد سجلات حالياً", color = Color.Gray, fontSize = 12.sp)
                        } else {
                            adminLogs.take(5).forEach { log ->
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(text = log.action, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = StylekMidnight)
                                        Text(text = log.details, fontSize = 11.sp, color = Color.DarkGray)
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Merchants Management
                    if (merchants.isEmpty()) {
                        EmptyStateView(
                            icon = Icons.Default.Store,
                            title = "لا يوجد تجار مسجلين",
                            message = "عند تسجيل التجار الجدد ستظهر طلبات اعتمادهم هنا."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(merchants) { merchant ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("admin_merchant_row_${merchant.id}")
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = merchant.storeName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        when (merchant.status) {
                                                            "APPROVED" -> Color(0xFFDCFCE7)
                                                            "REJECTED" -> Color(0xFFFEE2E2)
                                                            else -> Color(0xFFFEF3C7)
                                                        },
                                                        shape = RoundedCornerShape(6.dp)
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = merchant.status,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = when (merchant.status) {
                                                        "APPROVED" -> Color(0xFF16A34A)
                                                        "REJECTED" -> Color(0xFFDC2626)
                                                        else -> Color(0xFFD97706)
                                                    }
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "صاحب المتجر: ${merchant.ownerName} | هاتف: ${merchant.phone}", fontSize = 12.sp, color = Color.Gray)
                                        Text(text = "المدينة: ${merchant.city} | التخصص: ${merchant.storeType}", fontSize = 12.sp, color = Color.Gray)

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Actions
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            if (merchant.status != "APPROVED") {
                                                Button(
                                                    onClick = { viewModel.adminApproveMerchant(merchant.id) },
                                                    shape = RoundedCornerShape(8.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                                    modifier = Modifier.weight(1f).height(38.dp)
                                                ) {
                                                    Text(AppStrings.get("admin_approve", language), color = Color.White, fontSize = 12.sp)
                                                }
                                            }
                                            if (merchant.status != "REJECTED") {
                                                Button(
                                                    onClick = { viewModel.adminRejectMerchant(merchant.id) },
                                                    shape = RoundedCornerShape(8.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                                    modifier = Modifier.weight(1f).height(38.dp)
                                                ) {
                                                    Text(AppStrings.get("admin_reject", language), color = Color.White, fontSize = 12.sp)
                                                }
                                            }
                                            if (merchant.status == "APPROVED") {
                                                Button(
                                                    onClick = { viewModel.adminSuspendMerchant(merchant.id) },
                                                    shape = RoundedCornerShape(8.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                                    modifier = Modifier.weight(1f).height(38.dp)
                                                ) {
                                                    Text(AppStrings.get("admin_suspend", language), color = Color.White, fontSize = 12.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Products Moderation & Approvals
                    if (pendingProducts.isEmpty()) {
                        EmptyStateView(
                            icon = Icons.Default.Inventory,
                            title = "لا توجد منتجات بانتظار الموافقة",
                            message = "جميع المنتجات مفحوصة ومعتمدة حالياً."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(pendingProducts) { product ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("admin_product_row_${product.id}")
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text(text = "المتجر: ${product.storeName} | السعر: ${(product.discountPrice ?: product.price).toInt()} $currency", fontSize = 12.sp, color = StylekRoyalBlue)
                                        Text(text = "الفئة: ${product.category} | المقاسات: ${product.sizes}", fontSize = 11.sp, color = Color.Gray)

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = { viewModel.adminApproveProduct(product.id) },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                                modifier = Modifier.weight(1f).height(38.dp)
                                            ) {
                                                Text(AppStrings.get("admin_approve", language), color = Color.White, fontSize = 12.sp)
                                            }

                                            Button(
                                                onClick = { viewModel.adminRejectProduct(product.id) },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                                modifier = Modifier.weight(1f).height(38.dp)
                                            ) {
                                                Text(AppStrings.get("admin_reject", language), color = Color.White, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // Orders & Commissions View
                    if (orders.isEmpty()) {
                        EmptyStateView(
                            icon = Icons.Default.ShoppingBag,
                            title = "لا توجد طلبات في المنصة حتى الآن",
                            message = "ستظهر جميع الطلبات المكتملة وحسابات العمولات هنا."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(orders) { order ->
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = "طلب #${order.orderNumber}", fontWeight = FontWeight.Bold)
                                            Text(text = order.status, fontWeight = FontWeight.Bold, color = StylekRoyalBlue)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "المشتري: ${order.recipientName} - ${order.city}", fontSize = 12.sp, color = Color.Gray)
                                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(text = "قيمة الطلب:", fontSize = 12.sp)
                                            Text(text = "${order.totalAmount.toInt()} $currency", fontWeight = FontWeight.Bold)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(text = "عمولة المنصة (${order.platformCommissionRate}%):", fontSize = 12.sp, color = StylekGold)
                                            Text(text = "${order.platformCommissionAmount.toInt()} $currency", fontWeight = FontWeight.Bold, color = StylekGold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                4 -> {
                    // Users Management
                    if (users.isEmpty()) {
                        EmptyStateView(
                            icon = Icons.Default.People,
                            title = "لا يوجد مستخدمون مسجلون",
                            message = "ستظهر قائمة مستخدمي المنصة هنا."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(users) { u ->
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("admin_user_card_${u.id}")
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .background(
                                                            when (u.role) {
                                                                "ADMIN" -> StylekGold.copy(alpha = 0.2f)
                                                                "MERCHANT" -> StylekRoyalBlue.copy(alpha = 0.15f)
                                                                else -> Color.Gray.copy(alpha = 0.15f)
                                                            },
                                                            shape = CircleShape
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = when (u.role) {
                                                            "ADMIN" -> Icons.Default.AdminPanelSettings
                                                            "MERCHANT" -> Icons.Default.Store
                                                            else -> Icons.Default.People
                                                        },
                                                        contentDescription = null,
                                                        tint = when (u.role) {
                                                            "ADMIN" -> StylekGold
                                                            "MERCHANT" -> StylekRoyalBlue
                                                            else -> Color.DarkGray
                                                        },
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(text = u.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                    Text(text = "${u.phone} • ${u.email}", fontSize = 11.sp, color = Color.Gray)
                                                }
                                            }

                                            // Status pill
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        if (u.status == "ACTIVE") Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                                                        shape = RoundedCornerShape(6.dp)
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = if (u.status == "ACTIVE") "نشط" else "معطل",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (u.status == "ACTIVE") Color(0xFF16A34A) else Color(0xFFDC2626)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "الدور: ${when(u.role) { "ADMIN" -> "مدير نظام" ; "MERCHANT" -> "تاجر" ; else -> "متسوق" }} | المدينة: ${u.city}",
                                                fontSize = 11.sp,
                                                color = Color.DarkGray
                                            )
                                            if (u.role != "ADMIN") {
                                                Button(
                                                    onClick = { viewModel.adminToggleUserStatus(u.id, u.status) },
                                                    shape = RoundedCornerShape(8.dp),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = if (u.status == "ACTIVE") Color(0xFFDC2626) else Color(0xFF16A34A)
                                                    ),
                                                    modifier = Modifier.height(34.dp)
                                                ) {
                                                    Text(
                                                        text = if (u.status == "ACTIVE") "تجميد الحساب" else "تفعيل الحساب",
                                                        fontSize = 11.sp,
                                                        color = Color.White
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

        // Commission Rate Setting Dialog
        if (showCommissionDialog) {
            AlertDialog(
                onDismissRequest = { showCommissionDialog = false },
                title = { Text(AppStrings.get("admin_commission_rate", language), fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text(
                            text = "حدد نسبة العمولة المقتطعة لصالح المنصة من مبيعات التجار (مثال: 1% أو 2.5%):",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = editRateInput,
                            onValueChange = { editRateInput = it },
                            label = { Text("نسبة العمولة %") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("commission_rate_input")
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val r = editRateInput.toDoubleOrNull()
                            if (r != null && r >= 0.0) {
                                viewModel.adminUpdateCommission(r)
                                showCommissionDialog = false
                            } else {
                                viewModel.showToast("يرجى إدخال نسبة صحيحة")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StylekMidnight)
                    ) {
                        Text(AppStrings.get("admin_commission_update", language), color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCommissionDialog = false }) {
                        Text(AppStrings.get("cancel", language))
                    }
                }
            )
        }
    }
}

@Composable
fun AdminMiniCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(30.dp).background(color.copy(alpha = 0.12f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, fontSize = 11.sp, color = Color.Gray, maxLines = 1)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
