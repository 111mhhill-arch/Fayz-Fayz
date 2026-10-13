package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.OrderEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.StylekBottomBar
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppStrings
import com.example.ui.navigation.Screen
import com.example.ui.theme.StylekBackground
import com.example.ui.theme.StylekBorder
import com.example.ui.theme.StylekCard
import com.example.ui.theme.StylekGold
import com.example.ui.theme.StylekPrimaryBlue
import com.example.ui.theme.StylekTextPrimary
import com.example.ui.theme.StylekTextSecondary
import com.example.ui.viewmodel.StylekViewModel

/**
 * Screen 14: طلباتي والتتبع (Orders & Tracking) - Matching Reference Screen 14
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: StylekViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    val orders by viewModel.userOrders.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val currency = AppStrings.get("currency", language)

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("الكل", "قيد المعالجة", "قيد التوصيل", "تم التسليم", "ملغي")

    val filteredOrders = when (selectedTabIndex) {
        1 -> orders.filter { it.status == "PENDING" || it.status == "PREPARING" || it.status == "ACCEPTED" }
        2 -> orders.filter { it.status == "SHIPPED" || it.status == "IN_TRANSIT" }
        3 -> orders.filter { it.status == "DELIVERED" }
        4 -> orders.filter { it.status == "CANCELLED" }
        else -> orders
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("طلباتي", fontWeight = FontWeight.Bold, color = StylekTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = StylekTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StylekBackground)
            )
        },
        bottomBar = {
            StylekBottomBar(
                currentScreen = Screen.Profile,
                language = language,
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(StylekBackground)
                .padding(padding)
        ) {
            // Tabs matching Reference Screen 14: [الكل | قيد المعالجة | قيد التوصيل | تم التسليم | ملغي]
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
                tabs.forEachIndexed { index, tabTitle ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = tabTitle,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) StylekPrimaryBlue else StylekTextSecondary
                            )
                        }
                    )
                }
            }

            if (user == null) {
                EmptyStateView(
                    icon = Icons.Default.ShoppingBag,
                    title = "يرجى تسجيل الدخول",
                    message = "سجل الدخول لمتابعة حالة شحناتك وطلباتك السابقة.",
                    actionButtonText = AppStrings.get("login", language),
                    onActionClick = { onNavigate(Screen.Login) }
                )
            } else if (filteredOrders.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.ShoppingBag,
                    title = "لا توجد طلبات في هذا القسم",
                    message = "يمكنك تصفح متجر الأزياء وإتمام أول طلب الآن.",
                    actionButtonText = "تصفح المنتجات الآن",
                    onActionClick = { onNavigate(Screen.Home) }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredOrders) { order ->
                        OrderCardItem(order = order, currency = currency)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCardItem(order: OrderEntity, currency: String) {
    val statusColor = when (order.status) {
        "DELIVERED" -> Color(0xFF10B981)
        "CANCELLED" -> Color(0xFFEF4444)
        "SHIPPED", "IN_TRANSIT" -> StylekPrimaryBlue
        else -> StylekGold
    }

    val statusText = when (order.status) {
        "PENDING" -> "قيد المعالجة"
        "ACCEPTED" -> "تم قبول الطلب"
        "PREPARING" -> "قيد التجهيز"
        "SHIPPED" -> "تم الشحن"
        "IN_TRANSIT" -> "قيد التوصيل"
        "DELIVERED" -> "تم التسليم"
        "CANCELLED" -> "ملغي"
        else -> order.status
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = StylekCard),
        border = BorderStroke(1.dp, StylekBorder),
        modifier = Modifier.fillMaxWidth().testTag("order_item_${order.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.orderNumber,
                        fontWeight = FontWeight.Bold,
                        color = StylekTextPrimary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "التاريخ: ${java.text.SimpleDateFormat("yyyy/MM/dd", java.util.Locale.getDefault()).format(java.util.Date(order.createdAt))}",
                        fontSize = 11.sp,
                        color = StylekTextSecondary
                    )
                }

                // Status Pill matching Reference Screen 14
                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.18f), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = StylekBorder)
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "المستلم:", fontSize = 12.sp, color = StylekTextSecondary)
                Text(text = "${order.recipientName} (${order.city})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = StylekTextPrimary)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "طريقة الدفع:", fontSize = 12.sp, color = StylekTextSecondary)
                Text(text = order.paymentMethod, fontSize = 12.sp, color = StylekTextPrimary)
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = StylekBorder)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "إجمالي الطلب:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = StylekTextSecondary)
                Text(
                    text = "${order.totalAmount.toInt()} $currency",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = StylekGold
                )
            }
        }
    }
}
