package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.EmptyStateView
import com.example.ui.components.StylekBottomBar
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
 * Screen 12: سلة التسوق (Cart Screen) - Matching Reference Screen 12
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: StylekViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val approvedProducts by viewModel.approvedProducts.collectAsState()
    var couponCode by remember { mutableStateOf("") }
    var couponDiscount by remember { mutableDoubleStateOf(0.0) }

    val currency = AppStrings.get("currency", language)

    val subtotal = cartItems.sumOf { item ->
        val prod = approvedProducts.firstOrNull { it.id == item.productId }
        val price = prod?.let { it.discountPrice ?: it.price } ?: 0.0
        price * item.quantity
    }
    val deliveryFee = if (cartItems.isNotEmpty()) 5000.0 else 0.0
    val total = (subtotal - couponDiscount).coerceAtLeast(0.0) + deliveryFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("سلة التسوق", fontWeight = FontWeight.Bold, color = StylekTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = StylekTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StylekBackground)
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    color = StylekBackgroundSecondary,
                    border = BorderStroke(1.dp, StylekBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "المبلغ الإجمالي", color = StylekTextSecondary, fontSize = 13.sp)
                            Text(
                                text = "${total.toInt()} $currency",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, color = StylekGold)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        // Primary Blue CTA Button: إتمام الشراء (Reference Screen 12)
                        Button(
                            onClick = { onNavigate(Screen.Checkout) },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StylekPrimaryBlue),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("cart_proceed_checkout_btn")
                        ) {
                            Text(
                                text = "إتمام الشراء",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                StylekBottomBar(currentScreen = Screen.Cart, language = language, onNavigate = onNavigate)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(StylekBackground)
                .padding(padding)
        ) {
            if (cartItems.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.ShoppingCart,
                    title = "سلة التسوق فارغة",
                    message = "لم تقم بإضافة أي منتجات إلى السلة حتى الآن. استكشف أحدث عروض الأزياء والمتاجر.",
                    actionButtonText = "تصفح المنتجات الآن",
                    onActionClick = { onNavigate(Screen.Home) }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems) { item ->
                        val product = approvedProducts.firstOrNull { it.id == item.productId }
                        val price = product?.let { it.discountPrice ?: it.price } ?: 0.0

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = StylekCard),
                            border = BorderStroke(1.dp, StylekBorder),
                            modifier = Modifier.fillMaxWidth().testTag("cart_item_${item.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Product Image Thumbnail
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(StylekCardElevated)
                                ) {
                                    if (product != null && product.imageUrl.isNotBlank()) {
                                        AsyncImage(
                                            model = product.imageUrl,
                                            contentDescription = product.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = StylekTextSecondary)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = product?.name ?: "منتج أزياء",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = StylekTextPrimary,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "المقاس: ${item.selectedSize} • اللون: ${item.selectedColor}",
                                        fontSize = 11.sp,
                                        color = StylekTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${price.toInt()} $currency",
                                        fontWeight = FontWeight.Bold,
                                        color = StylekGold,
                                        fontSize = 14.sp
                                    )
                                }

                                // Quantity Stepper (- 1 +)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(StylekCardElevated)
                                        .border(1.dp, StylekBorder, RoundedCornerShape(8.dp))
                                ) {
                                    IconButton(
                                        onClick = { viewModel.updateCartQuantity(item.id, item.quantity - 1) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (item.quantity == 1) Icons.Default.Delete else Icons.Default.Remove,
                                            contentDescription = "Minus",
                                            tint = if (item.quantity == 1) StylekRuby else StylekTextPrimary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                    Text(
                                        text = "${item.quantity}",
                                        fontWeight = FontWeight.Bold,
                                        color = StylekTextPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp),
                                        fontSize = 13.sp
                                    )
                                    IconButton(
                                        onClick = { viewModel.updateCartQuantity(item.id, item.quantity + 1) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Plus",
                                            tint = StylekTextPrimary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Coupon Input Box
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = StylekCard),
                            border = BorderStroke(1.dp, StylekBorder),
                            modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = couponCode,
                                    onValueChange = { couponCode = it },
                                    placeholder = { Text("كود الخصم (مثال: STYLEK10)", fontSize = 12.sp, color = StylekTextSecondary) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = StylekCardElevated,
                                        unfocusedContainerColor = StylekCardElevated,
                                        focusedBorderColor = StylekPrimaryBlue,
                                        unfocusedBorderColor = StylekBorder,
                                        focusedTextColor = StylekTextPrimary,
                                        unfocusedTextColor = StylekTextPrimary
                                    ),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (couponCode.trim().uppercase() == "STYLEK10") {
                                            couponDiscount = subtotal * 0.10
                                            viewModel.showToast("تم تطبيق خصم 10% بنجاح!")
                                        } else {
                                            viewModel.showToast("كود الخصم غير صالح")
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = StylekPrimaryBlue)
                                ) {
                                    Text("تطبيق", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Cost Summary
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = StylekCard),
                            border = BorderStroke(1.dp, StylekBorder),
                            modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "المجموع الفرعي", color = StylekTextSecondary, fontSize = 13.sp)
                                    Text(text = "${subtotal.toInt()} $currency", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = StylekTextPrimary)
                                }
                                if (couponDiscount > 0) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = "خصم الكوبون", color = Color(0xFF10B981), fontSize = 13.sp)
                                        Text(text = "-${couponDiscount.toInt()} $currency", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "رسوم التوصيل", color = StylekTextSecondary, fontSize = 13.sp)
                                    Text(text = "${deliveryFee.toInt()} $currency", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = StylekTextPrimary)
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = StylekBorder)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "الإجمالي", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = StylekTextPrimary)
                                    Text(text = "${total.toInt()} $currency", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = StylekGold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Screen 13: الدفع والتوصيل (Checkout Screen) - Matching Reference Screen 13
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: StylekViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()

    var name by remember { mutableStateOf(user?.name ?: "") }
    var phone by remember { mutableStateOf(user?.phone ?: "") }
    var city by remember { mutableStateOf(user?.city ?: "بغداد") }
    var address by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedPayment by remember { mutableStateOf("COD") }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = StylekCard,
        unfocusedContainerColor = StylekCard,
        focusedBorderColor = StylekPrimaryBlue,
        unfocusedBorderColor = StylekBorder,
        focusedTextColor = StylekTextPrimary,
        unfocusedTextColor = StylekTextPrimary,
        focusedLabelColor = StylekPrimaryBlue,
        unfocusedLabelColor = StylekTextSecondary,
        cursorColor = StylekPrimaryBlue
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إتمام الطلب والدفع", fontWeight = FontWeight.Bold, color = StylekTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = StylekTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StylekBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(StylekBackground)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Steps Indicator matching Reference Screen 13: [1 المنتجات → 2 العنوان → 3 التوصيل → 4 الدفع → 5 مراجعة]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    "1" to "المنتجات",
                    "2" to "العنوان",
                    "3" to "التوصيل",
                    "4" to "الدفع",
                    "5" to "مراجعة"
                ).forEachIndexed { idx, (stepNum, stepLabel) ->
                    val isActive = idx <= 3
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (isActive) StylekPrimaryBlue else StylekCardElevated)
                                .border(1.dp, if (isActive) StylekPrimaryBlue else StylekBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stepNum, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = stepLabel, fontSize = 9.sp, color = if (isActive) StylekTextPrimary else StylekTextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Delivery Information Card
            Text(text = "معلومات التوصيل والشحن", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = StylekTextPrimary)
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("اسم المستلم") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().testTag("checkout_name_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("رقم هاتف المستلم") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().testTag("checkout_phone_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("المدينة / المحافظة") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().testTag("checkout_city_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("العنوان بالتفصيل (الحي، أقرب نقطة دالة)") },
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().testTag("checkout_address_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("ملاحظات التوصيل (اختياري)") },
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null) },
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Payment Methods Selection matching Reference Screen 13
            Text(text = "طريقة الدفع", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = StylekTextPrimary)
            Spacer(modifier = Modifier.height(10.dp))

            listOf(
                Triple("COD", "الدفع عند الاستلام (أدفع عند استلام الطلب)", Icons.Default.LocalAtm),
                Triple("ZAIN_CASH", "Zain Cash (محفظة زين كاش)", Icons.Default.Payment),
                Triple("QI_CARD", "Qi Card (بطاقة الماستر كارد / كي كارد)", Icons.Default.CreditCard),
                Triple("CREDIT_CARD", "بطاقة ائتمانية (Visa / Mastercard)", Icons.Default.CreditCard)
            ).forEach { (id, title, icon) ->
                val isSelected = selectedPayment == id
                Card(
                    onClick = { selectedPayment = id },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) StylekCardElevated else StylekCard
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) StylekPrimaryBlue else StylekBorder
                    ),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("payment_opt_$id")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedPayment = id },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = StylekPrimaryBlue,
                                unselectedColor = StylekTextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(icon, contentDescription = null, tint = if (isSelected) StylekPrimaryBlue else StylekTextSecondary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = title,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = StylekTextPrimary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Blue Action Button: متابعة / تأكيد الطلب
            Button(
                onClick = {
                    if (name.isBlank() || phone.isBlank() || address.isBlank()) {
                        viewModel.showToast("يرجى إكمال بيانات التوصيل")
                        return@Button
                    }
                    viewModel.placeOrder(
                        recipientName = name,
                        phone = phone,
                        city = city,
                        address = address,
                        notes = notes,
                        paymentMethod = selectedPayment
                    ) { order ->
                        viewModel.showToast("تم إنشاء الطلب بنجاح! رقم الطلب: ${order.orderNumber}")
                        onNavigate(Screen.Orders)
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StylekPrimaryBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("checkout_confirm_order_btn")
            ) {
                Text(
                    text = "متابعة وتأكيد الطلب",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
