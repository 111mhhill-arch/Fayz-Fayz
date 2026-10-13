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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppStrings
import com.example.ui.theme.StylekGold
import com.example.ui.theme.StylekMidnight
import com.example.ui.viewmodel.StylekViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: StylekViewModel,
    language: AppLanguage,
    onBack: () -> Unit
) {
    val isDark by viewModel.isDarkMode.collectAsState()
    var notificationsEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(AppStrings.get("settings_title", language), fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Language Selection Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = StylekMidnight)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = AppStrings.get("language", language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AppLanguage.values().forEach { lang ->
                            val isSelected = language == lang
                            Surface(
                                onClick = {
                                    viewModel.setLanguage(lang)
                                    viewModel.showToast(if (lang == AppLanguage.ARABIC) "تم تغيير اللغة إلى العربية" else "Dil Türkçe olarak değiştirildi")
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) StylekMidnight else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("lang_btn_${lang.code}")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = lang.displayName,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Dark Mode Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DarkMode, contentDescription = null, tint = StylekMidnight)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = AppStrings.get("dark_mode", language),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                    Switch(
                        checked = isDark,
                        onCheckedChange = { viewModel.toggleDarkMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = StylekGold,
                            checkedTrackColor = StylekMidnight
                        ),
                        modifier = Modifier.testTag("dark_mode_switch")
                    )
                }
            }

            // Notifications Toggle Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = StylekMidnight)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = AppStrings.get("notifications", language),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = StylekGold,
                            checkedTrackColor = StylekMidnight
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // About Stylek
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = StylekMidnight)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "عن تطبيق ستايلك", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ستايلك Stylek منصة تسوق ومستشار أزياء ذكي مبني بتقنيات الذكاء الاصطناعي لمساعدتك في تنسيق مظهرك الأنيق حسب ميزانيتك ومواصفاتك الشخصية.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "الإصدار 1.0.0 (Production Ready)",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
