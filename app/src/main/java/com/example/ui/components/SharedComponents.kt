package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.ProductEntity
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

/**
 * Top Header matching Reference Screen 5
 * Logo on right (RTL start), Notification, Cart and Profile on left (RTL end)
 */
@Composable
fun StylekHomeHeader(
    language: AppLanguage,
    cartCount: Int = 0,
    hasUnreadNotifications: Boolean = false,
    onNotificationsClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Surface(
        color = StylekBackgroundSecondary,
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stylek Signature Logo
            StylekFullLogo(
                isVertical = false,
                iconSize = 34.dp,
                titleSize = 18.sp,
                showSlogan = true
            )

            // Actions: Notifications, Cart, Profile
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Notifications icon
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(StylekCardElevated)
                        .border(1.dp, StylekBorder, CircleShape)
                        .testTag("top_bar_notifications_btn")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(19.dp)
                        )
                        if (hasUnreadNotifications) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .align(Alignment.TopEnd)
                                    .background(StylekGold, shape = CircleShape)
                            )
                        }
                    }
                }

                // Cart Icon with badge
                IconButton(
                    onClick = onCartClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(StylekCardElevated)
                        .border(1.dp, StylekBorder, CircleShape)
                        .testTag("top_bar_cart_btn")
                ) {
                    if (cartCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = StylekGold,
                                    contentColor = StylekBackground
                                ) {
                                    Text(
                                        text = "$cartCount",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Cart",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Profile Avatar Icon
                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(StylekCardElevated)
                        .border(1.dp, StylekBorder, CircleShape)
                        .testTag("top_bar_profile_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = StylekGold,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StylekTopBar(
    title: String,
    language: AppLanguage,
    showBack: Boolean = false,
    cartCount: Int = 0,
    onBackClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onAiClick: () -> Unit = {},
    actions: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = StylekTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            if (showBack) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("top_bar_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = StylekTextPrimary
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = onCartClick,
                modifier = Modifier.testTag("top_bar_cart_btn")
            ) {
                if (cartCount > 0) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = StylekGold,
                                contentColor = StylekBackground
                            ) {
                                Text("$cartCount", fontWeight = FontWeight.Bold)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = StylekTextPrimary
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Cart",
                        tint = StylekTextPrimary
                    )
                }
            }
            actions()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = StylekBackgroundSecondary
        )
    )
}

/**
 * Bottom Navigation matching Reference Design
 * Tabs: الرئيسية, التصنيفات, الميزانية, السلة, حسابي
 */
@Composable
fun StylekBottomBar(
    currentScreen: Screen,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit
) {
    Surface(
        color = StylekBackgroundSecondary,
        shadowElevation = 10.dp,
        border = BorderStroke(1.dp, StylekBorder)
    ) {
        NavigationBar(
            containerColor = StylekBackgroundSecondary,
            contentColor = StylekTextPrimary,
            tonalElevation = 0.dp
        ) {
            val navItems = listOf(
                Triple(Screen.Home, AppStrings.get("nav_home", language), Icons.Default.Home),
                Triple(Screen.Search, "التصنيفات", Icons.Default.GridView),
                Triple(Screen.Budget, AppStrings.get("nav_budget", language), Icons.Default.AccountBalanceWallet),
                Triple(Screen.Cart, AppStrings.get("nav_cart", language), Icons.Default.ShoppingCart),
                Triple(Screen.Profile, "حسابي", Icons.Default.Person)
            )

            navItems.forEach { (screen, label, icon) ->
                val isSelected = currentScreen::class == screen::class
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onNavigate(screen) },
                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = StylekPrimaryBlue,
                        selectedTextColor = StylekPrimaryBlue,
                        indicatorColor = StylekPrimaryBlue.copy(alpha = 0.18f),
                        unselectedIconColor = StylekTextSecondary,
                        unselectedTextColor = StylekTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_item_${label}")
                )
            }
        }
    }
}

@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    message: String,
    actionButtonText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = StylekCard),
        border = BorderStroke(1.dp, StylekBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .background(StylekCardElevated, shape = CircleShape)
                    .border(1.dp, StylekBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = StylekPrimaryBlue
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = StylekTextPrimary,
                textAlign = TextAlign.Center,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = StylekTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                fontSize = 12.sp
            )
            if (actionButtonText != null && onActionClick != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    onClick = onActionClick,
                    shape = RoundedCornerShape(10.dp),
                    color = StylekPrimaryBlue,
                    modifier = Modifier.testTag("empty_state_action_btn")
                ) {
                    Text(
                        text = actionButtonText,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

/**
 * Product Card matching Reference Screen 5 & 10
 */
@Composable
fun ProductCard(
    product: ProductEntity,
    language: AppLanguage,
    isFavorite: Boolean = false,
    onCardClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val currency = AppStrings.get("currency", language)
    val hasDiscount = product.discountPrice != null && product.discountPrice < product.price
    val finalPrice = product.discountPrice ?: product.price
    val discountPercent = if (hasDiscount) {
        (((product.price - product.discountPrice!!) / product.price) * 100).toInt()
    } else 0

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = StylekCard),
        border = BorderStroke(1.dp, StylekBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .testTag("product_card_${product.id}")
    ) {
        Column {
            // Product Image Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .background(Color(0xFF0D1829))
            ) {
                if (product.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(StylekCardElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = StylekTextSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }

                // Discount Badge (-30%)
                if (hasDiscount) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(StylekGold)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "-$discountPercent%",
                            color = StylekBackground,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Floating glass favorite button
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(StylekBackground.copy(alpha = 0.65f))
                        .border(0.5.dp, StylekBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) StylekRuby else StylekTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Out of stock overlay
                if (product.stockQuantity <= 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.75f))
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = AppStrings.get("out_of_stock", language),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Info Section
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Store Name & Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.storeName,
                        style = MaterialTheme.typography.bodySmall,
                        color = StylekTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = StylekGold,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "%.1f".format(product.rating),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StylekTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Product Name
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = StylekTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price Row & Quick Add
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${finalPrice.toInt()} $currency",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = StylekTextPrimary,
                                fontSize = 14.sp
                            )
                        )
                        if (hasDiscount) {
                            Text(
                                text = "${product.price.toInt()} $currency",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    textDecoration = TextDecoration.LineThrough,
                                    color = StylekTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Quick Add to Cart button (Royal Blue)
                    Surface(
                        onClick = onAddToCartClick,
                        enabled = product.stockQuantity > 0,
                        shape = RoundedCornerShape(10.dp),
                        color = if (product.stockQuantity > 0) StylekPrimaryBlue else StylekBorder,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Add to Cart",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
