package com.example.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checkroom
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import coil.compose.AsyncImage
import com.example.ui.components.StylekBottomBar
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppStrings
import com.example.ui.navigation.Screen
import com.example.ui.theme.StylekBackground
import com.example.ui.theme.StylekBorder
import com.example.ui.theme.StylekCard
import com.example.ui.theme.StylekCardElevated
import com.example.ui.theme.StylekGold
import com.example.ui.theme.StylekPrimaryBlue
import com.example.ui.theme.StylekTextPrimary
import com.example.ui.theme.StylekTextSecondary
import com.example.ui.viewmodel.StylekViewModel

/**
 * Screen 6: تحديد الميزانية (Budget Screen) - Matching Reference Screen 6
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    viewModel: StylekViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit
) {
    val budgetAmount by viewModel.budgetAmountInput.collectAsState()
    val occasion by viewModel.budgetOccasion.collectAsState()
    val gender by viewModel.budgetGender.collectAsState()
    val style by viewModel.budgetStyle.collectAsState()
    val fullOutfit by viewModel.budgetFullOutfit.collectAsState()
    val outfitResult by viewModel.budgetOutfitResult.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()

    val currency = AppStrings.get("currency", language)

    var selectedSize by remember { mutableStateOf("L") }
    var selectedColorIndex by remember { mutableStateOf(0) }

    val colorSwatches = listOf(
        Color(0xFFE2C9A5), // Beige
        Color(0xFF8B5A2B), // Brown
        Color(0xFF2563EB), // Blue
        Color(0xFFF97316), // Orange
        Color(0xFF1E293B), // Navy
        Color(0xFFFFFFFF), // White
        Color(0xFF000000)  // Black
    )

    val currentAmountFloat = (budgetAmount.toFloatOrNull() ?: 50000f).coerceIn(10000f, 500000f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "تحديد ميزانيتي",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = StylekTextPrimary
                        )
                        Text(
                            text = "اختر ميزانيتك والإطلالة المناسبة لك",
                            style = MaterialTheme.typography.labelSmall,
                            color = StylekTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigate(Screen.Home) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = StylekTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StylekBackground)
            )
        },
        bottomBar = {
            StylekBottomBar(
                currentScreen = Screen.Budget,
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Main Budget Display & Interactive Slider Card (Reference Screen 6)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = StylekCard),
                border = BorderStroke(1.dp, StylekBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "الميزانية المحددة",
                        fontSize = 13.sp,
                        color = StylekTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${budgetAmount.toIntOrNull() ?: 50000} $currency",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = StylekGold,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sleek Slider
                    Slider(
                        value = currentAmountFloat,
                        onValueChange = { newValue ->
                            val rounded = (Math.round(newValue / 5000.0) * 5000).toInt()
                            viewModel.budgetAmountInput.value = rounded.toString()
                        },
                        valueRange = 15000f..400000f,
                        colors = SliderDefaults.colors(
                            thumbColor = StylekPrimaryBlue,
                            activeTrackColor = StylekPrimaryBlue,
                            inactiveTrackColor = StylekBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Presets
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("50000", "100000", "150000", "200000", "300000").forEach { amount ->
                            val isSel = budgetAmount == amount
                            Surface(
                                onClick = { viewModel.budgetAmountInput.value = amount },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) StylekPrimaryBlue else StylekCardElevated,
                                border = BorderStroke(1.dp, if (isSel) StylekPrimaryBlue else StylekBorder),
                                modifier = Modifier.testTag("budget_preset_$amount")
                            ) {
                                Text(
                                    text = "${amount.toInt() / 1000}K",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else StylekTextSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Selectors matching Reference Screen 6
            // 1. Gender Selector
            Text(text = "الجنس", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = StylekTextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                listOf("male" to "رجال", "female" to "نساء").forEach { (g, label) ->
                    val isSel = gender == g
                    Surface(
                        onClick = { viewModel.budgetGender.value = g },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSel) StylekPrimaryBlue else StylekCard,
                        border = BorderStroke(1.dp, if (isSel) StylekPrimaryBlue else StylekBorder),
                        modifier = Modifier.weight(1f).height(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = label,
                                color = if (isSel) Color.White else StylekTextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Size Selector (S, M, L, XL, XXL)
            Text(text = "المقاس", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = StylekTextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("S", "M", "L", "XL", "XXL").forEach { s ->
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

            // 3. Color Swatches (Reference Screen 6)
            Text(text = "اللون المفضل", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = StylekTextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                colorSwatches.forEachIndexed { idx, col ->
                    val isSel = selectedColorIndex == idx
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(col)
                            .border(if (isSel) 2.5.dp else 1.dp, if (isSel) StylekGold else StylekBorder, CircleShape)
                            .clickable { selectedColorIndex = idx },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSel) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = if (col == Color.White) Color.Black else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Occasion Selector
            Text(text = "المناسبة", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = StylekTextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "occasion_daily" to "يومي",
                    "occasion_work" to "عمل",
                    "occasion_party" to "حفلة",
                    "occasion_wedding" to "زفاف",
                    "occasion_sport" to "رياضي"
                ).forEach { (key, label) ->
                    val isSel = occasion == key
                    Surface(
                        onClick = { viewModel.budgetOccasion.value = key },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSel) StylekPrimaryBlue else StylekCard,
                        border = BorderStroke(1.dp, if (isSel) StylekPrimaryBlue else StylekBorder)
                    ) {
                        Text(
                            text = label,
                            color = if (isSel) Color.White else StylekTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Full Outfit Toggle
            Card(
                shape = RoundedCornerShape(14.dp),
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
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Checkroom, contentDescription = null, tint = StylekGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "أريد إطلالة كاملة (Full Outfit)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = StylekTextPrimary
                            )
                        }
                        Text(
                            text = "تنسيق (قميص + بنطلون + حذاء + إكسسوار) ليكون المجموع ضمن الميزانية",
                            fontSize = 11.sp,
                            color = StylekTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                    Switch(
                        checked = fullOutfit,
                        onCheckedChange = { viewModel.budgetFullOutfit.value = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = StylekGold,
                            checkedTrackColor = StylekPrimaryBlue,
                            uncheckedTrackColor = StylekBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Big Primary Blue CTA Button matching Reference Screen 6: "عرض المنتجات المناسبة"
            Button(
                onClick = { viewModel.calculateBudgetOutfit() },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StylekPrimaryBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("budget_find_outfit_btn")
            ) {
                Text(
                    text = "عرض المنتجات المناسبة",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Results Display
            if (outfitResult != null) {
                val res = outfitResult!!
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = StylekCard),
                    border = BorderStroke(1.dp, StylekBorder),
                    modifier = Modifier.fillMaxWidth().testTag("budget_result_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✨ الإطلالة المتناسقة المقترحة",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = StylekTextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF10B981).copy(alpha = 0.20f), shape = RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "ضمن الميزانية",
                                    color = Color(0xFF10B981),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Pieces list
                        listOfNotNull(
                            res.top?.let { "القطعة العلوية / القميص" to it },
                            res.bottom?.let { "القطعة السفلية / البنطلون" to it },
                            res.shoes?.let { "الحذاء" to it },
                            res.accessory?.let { "الساعة / الإكسسوار" to it }
                        ).forEach { (label, product) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigate(Screen.ProductDetail(product.id)) }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = label, fontSize = 11.sp, color = StylekTextSecondary, fontWeight = FontWeight.SemiBold)
                                    Text(text = product.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StylekTextPrimary, maxLines = 1)
                                }
                                Text(
                                    text = "${product.price.toInt()} $currency",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = StylekGold
                                )
                            }
                            HorizontalDivider(color = StylekBorder)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Summary
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "إجمالي سعر الإطلالة:", fontSize = 13.sp, color = StylekTextSecondary)
                            Text(text = "${res.totalCost.toInt()} $currency", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = StylekTextPrimary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "المبلغ المتبقي من ميزانيتك:", fontSize = 13.sp, color = StylekTextSecondary)
                            Text(text = "${res.remainingBudget.toInt()} $currency", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF10B981))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Add All to Cart Button (Electric Blue)
                        Button(
                            onClick = {
                                viewModel.addOutfitToCart(res)
                                onNavigate(Screen.Cart)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StylekPrimaryBlue),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "إضافة الإطلالة بالكامل إلى السلة",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
