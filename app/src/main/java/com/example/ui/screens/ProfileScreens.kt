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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.StyleProfileEntity
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
import com.example.ui.theme.StylekRuby
import com.example.ui.theme.StylekTextPrimary
import com.example.ui.theme.StylekTextSecondary
import com.example.ui.viewmodel.StylekViewModel

/**
 * Screen 15: الملف الشخصي (Profile Screen) - Matching Reference Screen 15
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: StylekViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val merchant by viewModel.currentMerchant.collectAsState()
    val styleProfile by viewModel.userStyleProfile.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الملف الشخصي", fontWeight = FontWeight.Bold, color = StylekTextPrimary) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // User Avatar & Name Header matching Reference Screen 15
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(StylekCardElevated)
                    .border(2.dp, StylekPrimaryBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user?.name?.firstOrNull()?.toString() ?: "S",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = StylekGold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = user?.name ?: "متسوق ستايلك",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = StylekTextPrimary
            )
            Text(
                text = "${user?.phone ?: "07700000000"} • ${user?.city ?: "العراق"}",
                fontSize = 12.sp,
                color = StylekTextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Body Profile Specs Card matching Reference Screen 15
            Card(
                onClick = { onNavigate(Screen.StyleProfile) },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = StylekCard),
                border = BorderStroke(1.dp, StylekBorder),
                modifier = Modifier.fillMaxWidth().testTag("profile_style_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = StylekGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "معلوماتي الشخصية والقياسات",
                                fontWeight = FontWeight.Bold,
                                color = StylekTextPrimary,
                                fontSize = 13.sp
                            )
                        }
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = StylekTextSecondary, modifier = Modifier.size(16.dp))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Specs Grid matching Reference Screen 15
                    val specs = listOf(
                        "العمر" to "${user?.age ?: 25}",
                        "الجنس" to if (user?.gender == "female") "أنثى" else "ذكر",
                        "الطول" to "${styleProfile?.heightCm ?: 180} سم",
                        "الوزن" to "${styleProfile?.weightKg ?: 75} كغم",
                        "المقاس" to (styleProfile?.size ?: "L"),
                        "شكل الجسم" to "رياضي",
                        "لون البشرة" to (styleProfile?.skinTone ?: "فاتح"),
                        "الستايل المفضل" to (styleProfile?.styleType ?: "Casual")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        specs.chunked(2).forEach { rowSpecs ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowSpecs.forEach { (label, value) ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = StylekCardElevated,
                                        modifier = Modifier.weight(1f).height(38.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = label, fontSize = 11.sp, color = StylekTextSecondary)
                                            Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StylekTextPrimary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Menu List matching Reference Screen 15
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = StylekCard),
                border = BorderStroke(1.dp, StylekBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ProfileMenuRow(
                        title = "خزانة ملابسي",
                        icon = Icons.Default.Checkroom,
                        onClick = { onNavigate(Screen.Wardrobe) }
                    )
                    HorizontalDivider(color = StylekBorder)
                    ProfileMenuRow(
                        title = "المفضلة",
                        icon = Icons.Default.Favorite,
                        onClick = { onNavigate(Screen.Favorites) }
                    )
                    HorizontalDivider(color = StylekBorder)
                    ProfileMenuRow(
                        title = "طلباتي",
                        icon = Icons.Default.ShoppingBag,
                        onClick = { onNavigate(Screen.Orders) }
                    )
                    HorizontalDivider(color = StylekBorder)
                    ProfileMenuRow(
                        title = "الإعدادات",
                        icon = Icons.Default.Settings,
                        onClick = { onNavigate(Screen.Settings) }
                    )

                    // Role based shortcuts
                    if (user?.role == "MERCHANT") {
                        HorizontalDivider(color = StylekBorder)
                        ProfileMenuRow(
                            title = "لوحة تحكم التاجر",
                            icon = Icons.Default.Store,
                            onClick = { onNavigate(Screen.MerchantDashboard) }
                        )
                    } else {
                        HorizontalDivider(color = StylekBorder)
                        ProfileMenuRow(
                            title = "انضم كتاجر / فتح متجر",
                            icon = Icons.Default.Store,
                            onClick = { onNavigate(Screen.MerchantRegister) }
                        )
                    }

                    HorizontalDivider(color = StylekBorder)
                    if (user?.role == "ADMIN") {
                        ProfileMenuRow(
                            title = "لوحة الإدارة العامة",
                            icon = Icons.Default.AdminPanelSettings,
                            onClick = { onNavigate(Screen.AdminDashboard) }
                        )
                    } else {
                        ProfileMenuRow(
                            title = "بوابة دخول الإدارة",
                            icon = Icons.Default.AdminPanelSettings,
                            onClick = { onNavigate(Screen.AdminLogin) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Logout or Login button
            if (user != null) {
                Button(
                    onClick = { viewModel.logout() },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StylekCardElevated),
                    border = BorderStroke(1.dp, StylekRuby.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("profile_logout_btn")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = StylekRuby)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تسجيل الخروج",
                        color = StylekRuby,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            } else {
                Button(
                    onClick = { onNavigate(Screen.Login) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StylekPrimaryBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("profile_login_btn")
                ) {
                    Text(
                        text = "تسجيل الدخول / إنشاء حساب",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProfileMenuRow(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = StylekPrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = StylekTextPrimary,
                fontSize = 13.sp
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = StylekTextSecondary,
            modifier = Modifier.size(16.dp)
        )
    }
}

/**
 * Style Profile Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyleProfileScreen(
    viewModel: StylekViewModel,
    language: AppLanguage,
    onBack: () -> Unit
) {
    val existingProfile by viewModel.userStyleProfile.collectAsState()

    var heightCm by remember { mutableStateOf(existingProfile?.heightCm?.toString() ?: "180") }
    var weightKg by remember { mutableStateOf(existingProfile?.weightKg?.toString() ?: "75") }
    var size by remember { mutableStateOf(existingProfile?.size ?: "L") }
    var skinTone by remember { mutableStateOf(existingProfile?.skinTone ?: "فاتح") }
    var styleType by remember { mutableStateOf(existingProfile?.styleType ?: "Casual") }
    var favoriteColors by remember { mutableStateOf(existingProfile?.favoriteColors ?: "أزرق، أسود") }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = StylekCard,
        unfocusedContainerColor = StylekCard,
        focusedBorderColor = StylekPrimaryBlue,
        unfocusedBorderColor = StylekBorder,
        focusedTextColor = StylekTextPrimary,
        unfocusedTextColor = StylekTextPrimary,
        cursorColor = StylekPrimaryBlue
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("معلوماتي الشخصية والقياسات", fontWeight = FontWeight.Bold, color = StylekTextPrimary) },
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
                .padding(20.dp)
        ) {
            Text(
                text = "مواصفاتك لمستشار الأزياء الذكي",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = StylekTextPrimary
            )
            Text(
                text = "يستخدم Stylek AI هذه البيانات لتقديم أفضل التنسيقات المتطابقة تماماً مع جسمك ومظهرك.",
                style = MaterialTheme.typography.bodySmall,
                color = StylekTextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = heightCm,
                onValueChange = { heightCm = it },
                label = { Text("الطول (سم)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().testTag("style_height_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = weightKg,
                onValueChange = { weightKg = it },
                label = { Text("الوزن (كغم)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().testTag("style_weight_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = size,
                onValueChange = { size = it },
                label = { Text("المقاس المفضل (S, M, L, XL, XXL)") },
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().testTag("style_size_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = skinTone,
                onValueChange = { skinTone = it },
                label = { Text("درجة لون البشرة") },
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().testTag("style_skintone_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = styleType,
                onValueChange = { styleType = it },
                label = { Text("الستايل المفضل (كاجوال، رسمي، رياضي...)") },
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().testTag("style_type_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = favoriteColors,
                onValueChange = { favoriteColors = it },
                label = { Text("الألوان المفضلة") },
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().testTag("style_colors_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.saveStyleProfile(
                        heightCm = heightCm.toIntOrNull() ?: 180,
                        weightKg = weightKg.toIntOrNull() ?: 75,
                        size = size,
                        skinTone = skinTone,
                        styleType = styleType,
                        favoriteColors = favoriteColors
                    )
                    viewModel.showToast("تم حفظ وتحديث مواصفاتك بنجاح")
                    onBack()
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StylekPrimaryBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("style_profile_save_btn")
            ) {
                Text(
                    text = "حفظ البيانات",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

