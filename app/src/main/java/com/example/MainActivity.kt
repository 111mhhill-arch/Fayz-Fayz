package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.navigation.Screen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdminLoginScreen
import com.example.ui.screens.AiStylistScreen
import com.example.ui.screens.BudgetScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MerchantAddProductScreen
import com.example.ui.screens.MerchantDashboardScreen
import com.example.ui.screens.MerchantRegisterScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RegisterScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StoreDetailScreen
import com.example.ui.screens.StyleProfileScreen
import com.example.ui.screens.WardrobeScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.StylekViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: StylekViewModel = viewModel()
            val isDark by viewModel.isDarkMode.collectAsState()
            val language by viewModel.language.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val currentUser by viewModel.currentUser.collectAsState()

            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(Unit) {
                viewModel.userMessage.collect { msg ->
                    snackbarHostState.showSnackbar(msg)
                }
            }

            val layoutDirection = if (language == AppLanguage.ARABIC) {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }

            CompositionLocalProvider(
                LocalLayoutDirection provides layoutDirection,
                LocalAppLanguage provides language
            ) {
                MyApplicationTheme(darkTheme = isDark) {
                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            // Back button handler
                            BackHandler(enabled = currentScreen != Screen.Home && currentScreen != Screen.Welcome && currentScreen != Screen.Splash) {
                                viewModel.navigateBack()
                            }

                            when (val screen = currentScreen) {
                                is Screen.Splash -> {
                                    SplashScreen(
                                        language = language,
                                        isLoggedIn = currentUser != null,
                                        onNavigateNext = { viewModel.navigateTo(it) }
                                    )
                                }
                                is Screen.Welcome -> {
                                    WelcomeScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        onNavigate = { viewModel.navigateTo(it) }
                                    )
                                }
                                is Screen.Login -> {
                                    LoginScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        onNavigate = { viewModel.navigateTo(it) },
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }
                                is Screen.Register -> {
                                    RegisterScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        onNavigate = { viewModel.navigateTo(it) },
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }
                                is Screen.MerchantRegister -> {
                                    MerchantRegisterScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        onNavigate = { viewModel.navigateTo(it) },
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }
                                is Screen.AdminLogin -> {
                                    AdminLoginScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        onNavigate = { viewModel.navigateTo(it) },
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }
                                is Screen.Home -> {
                                    HomeScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        onNavigate = { viewModel.navigateTo(it) }
                                    )
                                }
                                is Screen.Search -> {
                                    SearchScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        onNavigate = { viewModel.navigateTo(it) }
                                    )
                                }
                                is Screen.ProductDetail -> {
                                    ProductDetailScreen(
                                        productId = screen.productId,
                                        viewModel = viewModel,
                                        language = language,
                                        onNavigate = { viewModel.navigateTo(it) },
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }
                                is Screen.Budget -> {
                                    BudgetScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        onNavigate = { viewModel.navigateTo(it) }
                                    )
                                }
                                is Screen.AiStylist -> {
                                    AiStylistScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        onNavigate = { viewModel.navigateTo(it) }
                                    )
                                }
                                is Screen.Wardrobe -> {
                                    WardrobeScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        onNavigate = { viewModel.navigateTo(it) }
                                    )
                                }
                                is Screen.Cart -> {
                                    CartScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        onNavigate = { viewModel.navigateTo(it) },
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }
                                is Screen.Checkout -> {
                                    CheckoutScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        onNavigate = { viewModel.navigateTo(it) },
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }
                                is Screen.Orders -> {
                                    OrdersScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        onNavigate = { viewModel.navigateTo(it) },
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }
                                is Screen.StoreDetail -> {
                                    StoreDetailScreen(
                                        merchantId = screen.merchantId,
                                        viewModel = viewModel,
                                        language = language,
                                        onNavigate = { viewModel.navigateTo(it) },
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }
                                is Screen.Profile -> {
                                    ProfileScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        onNavigate = { viewModel.navigateTo(it) }
                                    )
                                }
                                is Screen.StyleProfile -> {
                                    StyleProfileScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }
                                is Screen.Favorites -> {
                                    FavoritesScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        onNavigate = { viewModel.navigateTo(it) },
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }
                                is Screen.MerchantDashboard -> {
                                    MerchantDashboardScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        initialTab = 0,
                                        onNavigate = { viewModel.navigateTo(it) },
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }
                                is Screen.MerchantOrders -> {
                                    MerchantDashboardScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        initialTab = 2,
                                        onNavigate = { viewModel.navigateTo(it) },
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }
                                is Screen.MerchantAddProduct -> {
                                    MerchantAddProductScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }
                                is Screen.AdminDashboard -> {
                                    AdminDashboardScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        initialTab = 0,
                                        onNavigate = { viewModel.navigateTo(it) },
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }
                                is Screen.AdminUsers -> {
                                    AdminDashboardScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        initialTab = 4,
                                        onNavigate = { viewModel.navigateTo(it) },
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }
                                is Screen.AdminMerchants -> {
                                    AdminDashboardScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        initialTab = 1,
                                        onNavigate = { viewModel.navigateTo(it) },
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }
                                is Screen.AdminProducts -> {
                                    AdminDashboardScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        initialTab = 2,
                                        onNavigate = { viewModel.navigateTo(it) },
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }
                                is Screen.AdminOrders -> {
                                    AdminDashboardScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        initialTab = 3,
                                        onNavigate = { viewModel.navigateTo(it) },
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }
                                is Screen.AdminCommission, is Screen.AdminBanners, is Screen.AdminReports, is Screen.AdminLogs -> {
                                    AdminDashboardScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        initialTab = 0,
                                        onNavigate = { viewModel.navigateTo(it) },
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }
                                is Screen.Settings -> {
                                    SettingsScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }
                                else -> {
                                    HomeScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        onNavigate = { viewModel.navigateTo(it) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
