package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ProductCard
import com.example.ui.components.StylekBottomBar
import com.example.ui.localization.AppLanguage
import com.example.ui.navigation.Screen
import com.example.ui.theme.StylekBackground
import com.example.ui.theme.StylekTextPrimary
import com.example.ui.viewmodel.StylekViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: StylekViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    val favorites by viewModel.userFavorites.collectAsState()
    val allProducts by viewModel.approvedProducts.collectAsState()
    val favProducts = allProducts.filter { prod -> favorites.any { it.targetId == prod.id } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("المفضلة (${favProducts.size})", fontWeight = FontWeight.Bold, color = StylekTextPrimary) },
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
            if (favProducts.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Favorite,
                    title = "قائمة المفضلة فارغة",
                    message = "يمكنك حفظ المنتجات التي تعجبك في المفضلة بالضغط على أيقونة القلب في أي منتج للرجوع إليها لاحقاً.",
                    actionButtonText = "تصفح أحدث الأزياء",
                    onActionClick = { onNavigate(Screen.Home) }
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(favProducts) { product ->
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
                                viewModel.toggleFavorite(product.id)
                            }
                        )
                    }
                }
            }
        }
    }
}
