package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.components.StylekFullLogo
import com.example.ui.components.StylekMonogramIcon
import com.example.ui.localization.AppLanguage
import com.example.ui.viewmodel.StylekViewModel
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
import kotlinx.coroutines.delay

/**
 * Screen 1: شاشة البداية (Splash Screen) - Matching Reference Screen 1
 */
@Composable
fun SplashScreen(
    language: AppLanguage,
    isLoggedIn: Boolean,
    onNavigateNext: (Screen) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
        delay(2200)
        if (isLoggedIn) {
            onNavigateNext(Screen.Home)
        } else {
            onNavigateNext(Screen.Welcome)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        StylekBackground,
                        Color(0xFF091527),
                        Color(0xFF040A14)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(900)) + scaleIn(tween(900), initialScale = 0.85f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 40.dp)
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Brand Emblem & Title
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    StylekMonogramIcon(size = 72.dp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Stylek",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "ستايلك",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "مستشارك للأزياء",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StylekGold
                    )
                    Text(
                        text = "واختياراتك الأذكى لكل مناسبة",
                        fontSize = 12.sp,
                        color = StylekTextSecondary
                    )
                }

                // Fashion Model Frame with glowing border
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = StylekCard),
                    border = BorderStroke(1.5.dp, StylekPrimaryBlue.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .size(width = 240.dp, height = 280.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = R.drawable.stylek_home_banner_1791206791539,
                            contentDescription = "Fashion Showcase",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            StylekBackground.copy(alpha = 0.6f)
                                        )
                                    )
                                )
                        )
                    }
                }

                // Bottom loading bar
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "جاري التحميل...",
                        fontSize = 12.sp,
                        color = StylekTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        modifier = Modifier
                            .width(140.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = StylekPrimaryBlue,
                        trackColor = StylekCardElevated
                    )
                }
            }
        }
    }
}

/**
 * Screen 2: شاشة الترحيب (Welcome Screen) - Matching Reference Screen 2
 */
@Composable
fun WelcomeScreen(
    viewModel: StylekViewModel? = null,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StylekBackground)
    ) {
        // Fashion Background Image
        AsyncImage(
            model = R.drawable.stylek_welcome_hero_1791206778378,
            contentDescription = "Welcome Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark Gradient Overlay for optimal contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            StylekBackground.copy(alpha = 0.6f),
                            StylekBackground.copy(alpha = 0.95f),
                            StylekBackground
                        ),
                        startY = 100f
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Stylek Signature Logo
            StylekMonogramIcon(size = 56.dp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Stylek",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "ستايلك",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ستايلك على كيفك\nوالسعر بضغطة زر",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    lineHeight = 30.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Primary Blue CTA Button: ابدأ الآن
            Button(
                onClick = { onNavigate(Screen.Register) },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StylekPrimaryBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("welcome_start_now_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ابدأ الآن",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Outlined Button: تسجيل الدخول
            Button(
                onClick = { onNavigate(Screen.Login) },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StylekCard),
                border = BorderStroke(1.dp, StylekBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("welcome_login_btn")
            ) {
                Text(
                    text = "تسجيل الدخول",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Guest Entry Button: تصفح كزائر
            TextButton(
                onClick = {
                    if (viewModel != null) {
                        viewModel.loginAsGuest {
                            onNavigate(Screen.Home)
                        }
                    } else {
                        onNavigate(Screen.Home)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("welcome_guest_btn")
            ) {
                Text(
                    text = "تصفح التطبيق كزائر",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StylekTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer link
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "لديك حساب؟ ",
                    color = StylekTextSecondary,
                    fontSize = 13.sp
                )
                Text(
                    text = "تسجيل الدخول",
                    color = StylekPrimaryBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onNavigate(Screen.Login) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
