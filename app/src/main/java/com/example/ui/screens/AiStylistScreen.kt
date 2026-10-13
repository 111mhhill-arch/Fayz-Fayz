package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.ui.components.StylekBottomBar
import com.example.ui.components.StylekMonogramIcon
import com.example.ui.localization.AppLanguage
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
 * Screen 7: Stylek AI (AI Fashion Assistant) - Matching Reference Screen 7
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiStylistScreen(
    viewModel: StylekViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit
) {
    val messages by viewModel.chatMessages.collectAsState()
    val isThinking by viewModel.isAiThinking.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val quickQuestions = listOf(
        "ما هي المناسبة؟",
        "ما هي ميزانيتك؟",
        "ما هو مقاسك؟",
        "ما هي الألوان المفضلة؟",
        "ما هو الستايل؟"
    )

    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StylekMonogramIcon(size = 32.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Stylek AI",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = StylekTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = StylekGold,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = "مستشارك في الأزياء",
                                style = MaterialTheme.typography.labelSmall,
                                color = StylekGold
                            )
                        }
                    }
                },
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
                currentScreen = Screen.Home,
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
            // Chat history area
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))

                    // Initial Greeting Bubble matching Reference Screen 7
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = StylekCard),
                        border = BorderStroke(1.dp, StylekBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(StylekPrimaryBlue, shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Stylek AI",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "أهلاً بك! أنا مستشارك في الأزياء. أخبرني عن رغبتك وسأقترح لك أفضل الخيارات من متاجرنا.",
                                fontSize = 14.sp,
                                color = StylekTextPrimary,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick Prompt Buttons matching Reference Screen 7
                    Text(
                        text = "أسئلة مقترحة للبدء السريع:",
                        fontSize = 12.sp,
                        color = StylekTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        quickQuestions.forEach { prompt ->
                            Surface(
                                onClick = { viewModel.sendAiMessage(prompt) },
                                shape = RoundedCornerShape(12.dp),
                                color = StylekCard,
                                border = BorderStroke(1.dp, StylekBorder),
                                modifier = Modifier.fillMaxWidth().height(46.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = prompt,
                                        color = StylekTextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = StylekPrimaryBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Chat Messages
                items(messages) { msg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (msg.isUser) 16.dp else 4.dp,
                                bottomEnd = if (msg.isUser) 4.dp else 16.dp
                            ),
                            color = if (msg.isUser) StylekPrimaryBlue else StylekCard,
                            border = if (!msg.isUser) BorderStroke(1.dp, StylekBorder) else null,
                            modifier = Modifier.widthIn(max = 300.dp)
                        ) {
                            Text(
                                text = msg.text,
                                modifier = Modifier.padding(14.dp),
                                color = Color.White,
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }

                if (isThinking) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = StylekGold
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "مستشارك يفكر في أفضل تنسيق...",
                                color = StylekTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Bottom Input Bar matching Reference Screen 7
            Surface(
                color = StylekBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("اكتب رسالتك هنا...", fontSize = 13.sp, color = StylekTextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = StylekCard,
                            unfocusedContainerColor = StylekCard,
                            focusedBorderColor = StylekPrimaryBlue,
                            unfocusedBorderColor = StylekBorder,
                            focusedTextColor = StylekTextPrimary,
                            unfocusedTextColor = StylekTextPrimary,
                            cursorColor = StylekPrimaryBlue
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_chat_input"),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Surface(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val text = inputText
                                inputText = ""
                                viewModel.sendAiMessage(text)
                            }
                        },
                        shape = CircleShape,
                        color = StylekPrimaryBlue,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("ai_send_msg_btn")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
