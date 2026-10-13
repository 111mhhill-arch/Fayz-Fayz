package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ProductCard
import com.example.ui.components.StylekBottomBar
import com.example.ui.components.StylekTopBar
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
 * Screen 9: البحث والفلاتر (Search & Filters) - Matching Reference Screen 9
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: StylekViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit
) {
    val results by viewModel.searchResults.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val category by viewModel.filterCategory.collectAsState()
    val gender by viewModel.filterGender.collectAsState()
    val style by viewModel.filterStyle.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            StylekTopBar(
                title = "البحث والتصنيفات",
                language = language,
                cartCount = cartItems.sumOf { it.quantity },
                onCartClick = { onNavigate(Screen.Cart) },
                onAiClick = { onNavigate(Screen.AiStylist) }
            )
        },
        bottomBar = {
            StylekBottomBar(
                currentScreen = Screen.Search,
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
            // Search Input Row matching Reference Screen 9
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = { Text("ابحث عن منتج، متجر، أو تصنيف...", fontSize = 13.sp, color = StylekTextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = StylekTextSecondary) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = StylekTextSecondary)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = StylekCard,
                        unfocusedContainerColor = StylekCard,
                        focusedBorderColor = StylekPrimaryBlue,
                        unfocusedBorderColor = StylekBorder,
                        focusedTextColor = StylekTextPrimary,
                        unfocusedTextColor = StylekTextPrimary,
                        cursorColor = StylekPrimaryBlue
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_text_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    onClick = { showFilterSheet = true },
                    shape = RoundedCornerShape(12.dp),
                    color = StylekPrimaryBlue,
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("search_filters_toggle_btn")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filters",
                            tint = Color.White
                        )
                    }
                }
            }

            // Quick Sort Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val sortOptions = listOf(
                    "RELEVANCE" to AppStrings.get("sort_relevance", language),
                    "PRICE_LOW" to AppStrings.get("sort_price_low", language),
                    "PRICE_HIGH" to AppStrings.get("sort_price_high", language),
                    "RATING" to AppStrings.get("sort_rating", language),
                    "NEWEST" to AppStrings.get("sort_newest", language)
                )

                sortOptions.forEach { (key, label) ->
                    val isSelected = sortBy == key
                    Surface(
                        onClick = { viewModel.sortBy.value = key },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) StylekPrimaryBlue else StylekCard,
                        border = BorderStroke(1.dp, if (isSelected) StylekPrimaryBlue else StylekBorder),
                        modifier = Modifier.testTag("sort_chip_$key")
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else StylekTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Products Grid
            if (results.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Search,
                    title = AppStrings.get("empty_search", language),
                    message = "لم نجد أي منتجات تطابق بحثك حاليًا. جرّب كلمات بحث أو فلاتر أخرى."
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(results) { product ->
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

            // Filters Bottom Sheet matching Reference Screen 9
            if (showFilterSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showFilterSheet = false },
                    sheetState = rememberModalBottomSheetState(),
                    containerColor = StylekCard
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "تصفية المنتجات",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = StylekTextPrimary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Gender Filter
                        Text(text = "الجنس", fontWeight = FontWeight.SemiBold, color = StylekTextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("male" to "رجال", "female" to "نساء", "kids" to "أطفال").forEach { (g, label) ->
                                val isSel = gender == g
                                Surface(
                                    onClick = { viewModel.filterGender.value = if (isSel) null else g },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSel) StylekPrimaryBlue else StylekCardElevated,
                                    border = BorderStroke(1.dp, if (isSel) StylekPrimaryBlue else StylekBorder)
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSel) Color.White else StylekTextSecondary,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Style Filter
                        Text(text = "الستايل", fontWeight = FontWeight.SemiBold, color = StylekTextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("style_casual" to "كاجوال", "style_formal" to "رسمي", "style_sport" to "رياضي", "style_luxury" to "فاخر").forEach { (st, label) ->
                                val isSel = style == st
                                Surface(
                                    onClick = { viewModel.filterStyle.value = if (isSel) null else st },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSel) StylekPrimaryBlue else StylekCardElevated,
                                    border = BorderStroke(1.dp, if (isSel) StylekPrimaryBlue else StylekBorder)
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSel) Color.White else StylekTextSecondary,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.filterCategory.value = null
                                    viewModel.filterGender.value = null
                                    viewModel.filterStyle.value = null
                                    viewModel.filterMinPrice.value = null
                                    viewModel.filterMaxPrice.value = null
                                    showFilterSheet = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = StylekCardElevated),
                                border = BorderStroke(1.dp, StylekBorder),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "إعادة تعيين", color = StylekTextSecondary)
                            }

                            Button(
                                onClick = { showFilterSheet = false },
                                colors = ButtonDefaults.buttonColors(containerColor = StylekPrimaryBlue),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "تطبيق الفلاتر", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
