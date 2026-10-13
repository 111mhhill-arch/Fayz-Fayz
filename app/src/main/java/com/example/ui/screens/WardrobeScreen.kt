package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EmptyStateView
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
import com.example.ui.theme.StylekRuby
import com.example.ui.theme.StylekTextPrimary
import com.example.ui.theme.StylekTextSecondary
import com.example.ui.viewmodel.StylekViewModel

/**
 * Screen 8: خزانة ملابسي (Wardrobe Screen) - Matching Reference Screen 8
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeScreen(
    viewModel: StylekViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit
) {
    val wardrobeItems by viewModel.userWardrobe.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    val filterTabs = listOf("كل القطع", "ملابس", "أحذية", "إكسسوارات")

    var itemName by remember { mutableStateOf("") }
    var itemCategory by remember { mutableStateOf("قميص / ملابس") }
    var itemColor by remember { mutableStateOf("أسود") }
    var itemStyle by remember { mutableStateOf("كاجوال") }

    val filteredItems = when (selectedFilterIndex) {
        1 -> wardrobeItems.filter { it.category.contains("قميص") || it.category.contains("بنطلون") || it.category.contains("ملابس") || it.category.contains("جاكيت") }
        2 -> wardrobeItems.filter { it.category.contains("حذاء") || it.category.contains("أحذية") }
        3 -> wardrobeItems.filter { it.category.contains("إكسسوار") || it.category.contains("ساعة") || it.category.contains("شنطة") }
        else -> wardrobeItems
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "خزانة ملابسي",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = StylekTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigate(Screen.Home) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = StylekTextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Item", tint = StylekGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StylekBackground)
            )
        },
        bottomBar = {
            StylekBottomBar(
                currentScreen = Screen.Wardrobe,
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
            // Stylek AI Coordination Banner
            Card(
                onClick = { onNavigate(Screen.AiStylist) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StylekCard),
                border = BorderStroke(1.dp, StylekPrimaryBlue.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(StylekPrimaryBlue, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "تنسيق ذكي عبر Stylek AI",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "اطلب من الذكاء الاصطناعي تنسيق ملابسك مع منتجات المتاجر",
                            fontSize = 11.sp,
                            color = StylekTextSecondary
                        )
                    }
                }
            }

            // Filter Tabs matching Reference Screen 8: [كل القطع | ملابس | أحذية | إكسسوارات]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterTabs.forEachIndexed { index, label ->
                    val isSel = selectedFilterIndex == index
                    Surface(
                        onClick = { selectedFilterIndex = index },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSel) StylekPrimaryBlue else StylekCard,
                        border = BorderStroke(1.dp, if (isSel) StylekPrimaryBlue else StylekBorder),
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color.White else StylekTextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Grid Cards of user's wardrobe items + Add Piece Card
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // First Item: "+ إضافة قطعة" (Dashed card matching Reference Screen 8)
                item {
                    Card(
                        onClick = { showAddDialog = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = StylekCardElevated),
                        border = BorderStroke(1.dp, StylekBorder),
                        modifier = Modifier
                            .height(125.dp)
                            .testTag("wardrobe_add_card")
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add",
                                tint = StylekGold,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "+ إضافة قطعة",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                items(filteredItems) { item ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = StylekCard),
                        border = BorderStroke(1.dp, StylekBorder),
                        modifier = Modifier.height(125.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(StylekCardElevated),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when {
                                        item.category.contains("حذاء") -> Icons.Default.Checkroom
                                        item.category.contains("ساعة") -> Icons.Default.Watch
                                        item.category.contains("شنطة") -> Icons.Default.ShoppingBag
                                        else -> Icons.Default.Checkroom
                                    },
                                    contentDescription = null,
                                    tint = StylekPrimaryBlue,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = item.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = StylekTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = item.color,
                                    fontSize = 10.sp,
                                    color = StylekTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Add Piece Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                containerColor = StylekCard,
                title = { Text("إضافة قطعة إلى الخزانة", color = StylekTextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = itemName,
                            onValueChange = { itemName = it },
                            label = { Text("اسم القطعة (مثال: بنطلون جينز أزرق)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = StylekTextPrimary,
                                unfocusedTextColor = StylekTextPrimary,
                                focusedBorderColor = StylekPrimaryBlue,
                                unfocusedBorderColor = StylekBorder
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = itemCategory,
                            onValueChange = { itemCategory = it },
                            label = { Text("التصنيف (قميص، بنطلون، حذاء...)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = StylekTextPrimary,
                                unfocusedTextColor = StylekTextPrimary,
                                focusedBorderColor = StylekPrimaryBlue,
                                unfocusedBorderColor = StylekBorder
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = itemColor,
                            onValueChange = { itemColor = it },
                            label = { Text("اللون") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = StylekTextPrimary,
                                unfocusedTextColor = StylekTextPrimary,
                                focusedBorderColor = StylekPrimaryBlue,
                                unfocusedBorderColor = StylekBorder
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (itemName.isNotBlank()) {
                                viewModel.addWardrobeItem(itemName, itemCategory, itemColor, itemStyle, "")
                                itemName = ""
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StylekPrimaryBlue)
                    ) {
                        Text("إضافة القطعة", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("إلغاء", color = StylekTextSecondary)
                    }
                }
            )
        }
    }
}
