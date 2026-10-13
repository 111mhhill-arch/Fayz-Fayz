package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StylekGold
import com.example.ui.theme.StylekPrimaryBlue
import com.example.ui.theme.StylekSecondaryBlue
import com.example.ui.theme.StylekTextPrimary
import com.example.ui.theme.StylekTextSecondary

/**
 * The Signature Stylek Ribbon 'S' Monogram
 * Renders the glowing blue and gold fluid ribbon shape from the reference branding
 */
@Composable
fun StylekMonogramIcon(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            // Top ribbon curve of S (Electric Blue gradient to cyan)
            val topPath = Path().apply {
                moveTo(w * 0.78f, h * 0.22f)
                cubicTo(
                    w * 0.70f, h * 0.06f,
                    w * 0.30f, h * 0.06f,
                    w * 0.24f, h * 0.26f
                )
                cubicTo(
                    w * 0.18f, h * 0.44f,
                    w * 0.50f, h * 0.50f,
                    w * 0.65f, h * 0.58f
                )
            }
            drawPath(
                path = topPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF38BDF8), StylekPrimaryBlue, StylekSecondaryBlue),
                    start = Offset(0f, 0f),
                    end = Offset(w, h * 0.5f)
                ),
                style = Stroke(width = w * 0.14f, cap = StrokeCap.Round)
            )

            // Bottom ribbon curve of S (Royal Blue into glowing Gold accent)
            val bottomPath = Path().apply {
                moveTo(w * 0.55f, h * 0.52f)
                cubicTo(
                    w * 0.75f, h * 0.60f,
                    w * 0.82f, h * 0.74f,
                    w * 0.72f, h * 0.88f
                )
                cubicTo(
                    w * 0.60f, h * 0.98f,
                    w * 0.26f, h * 0.95f,
                    w * 0.22f, h * 0.78f
                )
            }
            drawPath(
                path = bottomPath,
                brush = Brush.linearGradient(
                    colors = listOf(StylekPrimaryBlue, Color(0xFF818CF8), StylekGold),
                    start = Offset(w * 0.5f, h * 0.5f),
                    end = Offset(w * 0.2f, h)
                ),
                style = Stroke(width = w * 0.14f, cap = StrokeCap.Round)
            )

            // Sparkle accent dot
            drawCircle(
                color = StylekGold,
                radius = w * 0.05f,
                center = Offset(w * 0.76f, h * 0.20f)
            )
        }
    }
}

/**
 * Full Stylek Logo with Monogram, English Name, Arabic Name, and optional Slogan
 */
@Composable
fun StylekFullLogo(
    modifier: Modifier = Modifier,
    iconSize: Dp = 54.dp,
    titleSize: TextUnit = 24.sp,
    showSlogan: Boolean = true,
    isVertical: Boolean = true
) {
    if (isVertical) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            StylekMonogramIcon(size = iconSize)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Stylek",
                fontSize = titleSize,
                fontWeight = FontWeight.ExtraBold,
                color = StylekTextPrimary,
                letterSpacing = 1.sp
            )
            Text(
                text = "ستايلك",
                fontSize = (titleSize.value * 0.8f).sp,
                fontWeight = FontWeight.Bold,
                color = StylekTextPrimary
            )
            if (showSlogan) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "مستشارك للأزياء",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StylekGold
                )
            }
        }
    } else {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StylekMonogramIcon(size = iconSize)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Stylek",
                        fontSize = titleSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = StylekTextPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ستايلك",
                        fontSize = (titleSize.value * 0.85f).sp,
                        fontWeight = FontWeight.Bold,
                        color = StylekTextPrimary
                    )
                }
                if (showSlogan) {
                    Text(
                        text = "مستشارك للأزياء",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StylekGold
                    )
                }
            }
        }
    }
}
