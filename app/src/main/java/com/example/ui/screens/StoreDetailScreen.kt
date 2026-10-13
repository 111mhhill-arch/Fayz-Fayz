package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.MerchantEntity
import com.example.data.local.entity.ProductEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ProductCard
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppStrings
import com.example.ui.navigation.Screen
import com.example.ui.theme.StylekGold
import com.example.ui.theme.StylekMidnight
import com.example.ui.viewmodel.StylekViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreDetailScreen(
    merchantId: Long,
    viewModel: StylekViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    var merchant by remember { mutableStateOf<MerchantEntity?>(null) }
    var isFollowing by remember { mutableStateOf(false) }
    val approvedProducts by viewModel.approvedProducts.collectAsState()
    val storeProducts = approvedProducts.filter { it.merchantId == merchantId }

    LaunchedEffect(merchantId) {
        merchant = viewModel.repository.getMerchantById(merchantId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(merchant?.storeName ?: "", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        if (merchant == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = StylekMidnight)
            }
        } else {
            val m = merchant!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Store Info Header Card
                Card(
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = StylekMidnight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Store, contentDescription = null, tint = StylekGold, modifier = Modifier.size(32.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = m.storeName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = StylekGold, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "${m.city} - ${m.address}", fontSize = 12.sp, color = Color.LightGray)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = StylekGold, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "%.1f".format(m.rating), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Follow Store Button
                        Button(
                            onClick = {
                                isFollowing = !isFollowing
                                viewModel.showToast(if (isFollowing) "أصبحت تتابع هذا المتجر" else "تم إلغاء المتابعة")
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFollowing) Color.DarkGray else StylekGold
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("store_follow_btn")
                        ) {
                            if (isFollowing) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = AppStrings.get("following_store", language), color = Color.White, fontWeight = FontWeight.Bold)
                            } else {
                                Text(text = AppStrings.get("follow_store", language), color = StylekMidnight, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "منتجات المتجر (${storeProducts.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                if (storeProducts.isEmpty()) {
                    EmptyStateView(
                        icon = Icons.Default.Store,
                        title = "لا توجد منتجات لهذا المتجر حالياً",
                        message = "لم يقم المتجر بنشر منتجات معتمدة حتى الآن."
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(storeProducts) { product ->
                            ProductCard(
                                product = product,
                                language = language,
                                onCardClick = { onNavigate(Screen.ProductDetail(product.id)) },
                                onAddToCartClick = {
                                    val s = product.sizes.split(",").firstOrNull()?.trim() ?: "M"
                                    val c = product.colors.split(",").firstOrNull()?.trim() ?: "Default"
                                    viewModel.addToCart(product.id, s, c)
                                },
                                onToggleFavorite = { viewModel.showToast("تم الحفظ في المفضلة") }
                            )
                        }
                    }
                }
            }
        }
    }
}
