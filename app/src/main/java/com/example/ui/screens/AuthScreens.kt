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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StylekMonogramIcon
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
import com.example.ui.theme.StylekTextPrimary
import com.example.ui.theme.StylekTextSecondary
import com.example.ui.viewmodel.StylekViewModel

/**
 * Screen 3: تسجيل الدخول (Login Screen) - Matching Reference Screen 3
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: StylekViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    var selectedRoleIndex by remember { mutableIntStateOf(0) }
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = StylekCard,
        unfocusedContainerColor = StylekCard,
        focusedBorderColor = StylekPrimaryBlue,
        unfocusedBorderColor = StylekBorder,
        focusedTextColor = StylekTextPrimary,
        unfocusedTextColor = StylekTextPrimary,
        focusedLabelColor = StylekPrimaryBlue,
        unfocusedLabelColor = StylekTextSecondary,
        focusedLeadingIconColor = StylekPrimaryBlue,
        unfocusedLeadingIconColor = StylekTextSecondary,
        cursorColor = StylekPrimaryBlue
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = StylekTextPrimary
                        )
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
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Stylek Signature Logo
            StylekMonogramIcon(size = 56.dp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Stylek",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "ستايلك",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "مرحباً بعودتك",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = StylekTextPrimary,
                fontSize = 22.sp
            )
            Text(
                text = "سجل دخولك للمتابعة",
                style = MaterialTheme.typography.bodyMedium,
                color = StylekTextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Segmented Role Switcher: [مستخدم | تاجر]
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = StylekCard,
                border = BorderStroke(1.dp, StylekBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    // User tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedRoleIndex == 0) StylekPrimaryBlue else Color.Transparent)
                            .clickable { selectedRoleIndex = 0 },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "مستخدم",
                            color = if (selectedRoleIndex == 0) Color.White else StylekTextSecondary,
                            fontWeight = if (selectedRoleIndex == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }

                    // Merchant tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedRoleIndex == 1) StylekPrimaryBlue else Color.Transparent)
                            .clickable { selectedRoleIndex = 1 },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "تاجر",
                            color = if (selectedRoleIndex == 1) Color.White else StylekTextSecondary,
                            fontWeight = if (selectedRoleIndex == 1) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Phone or Email Input
            OutlinedTextField(
                value = identifier,
                onValueChange = { identifier = it },
                label = { Text(if (selectedRoleIndex == 0) "رقم الهاتف أو البريد الإلكتروني" else "هاتف أو بريد المتجر") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_identifier_input"),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Password Input
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("كلمة المرور") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = StylekTextSecondary
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password_input"),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Remember me & Forgot Password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = StylekPrimaryBlue,
                            uncheckedColor = StylekTextSecondary,
                            checkmarkColor = Color.White
                        )
                    )
                    Text(
                        text = "تذكرني",
                        fontSize = 12.sp,
                        color = StylekTextSecondary
                    )
                }

                Text(
                    text = "نسيت كلمة المرور؟",
                    fontSize = 12.sp,
                    color = StylekPrimaryBlue,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        viewModel.showToast("يرجى التواصل مع الدعم الفني لاستعادة الحساب")
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Blue Login Button
            Button(
                onClick = {
                    if (identifier.isBlank() || password.isBlank()) {
                        viewModel.showToast("يرجى ملء جميع الحقول")
                        return@Button
                    }
                    isLoading = true
                    val role = if (selectedRoleIndex == 0) "USER" else "MERCHANT"
                    viewModel.login(identifier, password, role) {
                        isLoading = false
                        if (role == "MERCHANT") {
                            onNavigate(Screen.MerchantDashboard)
                        } else {
                            onNavigate(Screen.Home)
                        }
                    }
                },
                enabled = !isLoading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StylekPrimaryBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("login_submit_btn")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Text(
                        text = "تسجيل الدخول",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Or separator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(StylekBorder)
                )
                Text(
                    text = "أو",
                    color = StylekTextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(StylekBorder)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Social Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                // Google Sign In Button
                Surface(
                    onClick = {
                        isLoading = true
                        viewModel.loginWithGoogle(
                            accountEmail = "user@stylek.iq",
                            accountName = "مستخدم Google"
                        ) {
                            isLoading = false
                            onNavigate(Screen.Home)
                        }
                    },
                    shape = CircleShape,
                    color = StylekCard,
                    border = BorderStroke(1.dp, StylekBorder),
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("google_login_btn")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "G", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }

                Surface(
                    onClick = { viewModel.showToast("تسجيل الدخول عبر Apple متوفر على أجهزة iOS") },
                    shape = CircleShape,
                    color = StylekCard,
                    border = BorderStroke(1.dp, StylekBorder),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Guest Login / الدخول كضيف
            OutlinedButton(
                onClick = {
                    viewModel.loginAsGuest {
                        onNavigate(Screen.Home)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, StylekBorder),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = StylekTextPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("guest_login_btn")
            ) {
                Text(
                    text = "المتابعة كضيف بدون حساب",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = StylekTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(20.dp))

            // Create account link
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "ليس لديك حساب؟ ",
                    color = StylekTextSecondary,
                    fontSize = 13.sp
                )
                Text(
                    text = "إنشاء حساب",
                    color = StylekPrimaryBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onNavigate(Screen.Register) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Discreet internal Admin access link
            Text(
                text = "دخول الإدارة",
                color = StylekBorder,
                fontSize = 11.sp,
                modifier = Modifier
                    .clickable { onNavigate(Screen.AdminLogin) }
                    .testTag("admin_hidden_entry_btn")
            )
        }
    }
}

/**
 * Screen 4: إنشاء حساب (Register Screen) - Matching Reference Screen 4
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: StylekViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    var selectedRoleIndex by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Baghdad") }
    var passwordVisible by remember { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = StylekCard,
        unfocusedContainerColor = StylekCard,
        focusedBorderColor = StylekPrimaryBlue,
        unfocusedBorderColor = StylekBorder,
        focusedTextColor = StylekTextPrimary,
        unfocusedTextColor = StylekTextPrimary,
        focusedLabelColor = StylekPrimaryBlue,
        unfocusedLabelColor = StylekTextSecondary,
        focusedLeadingIconColor = StylekPrimaryBlue,
        unfocusedLeadingIconColor = StylekTextSecondary,
        cursorColor = StylekPrimaryBlue
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = StylekTextPrimary
                        )
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
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StylekMonogramIcon(size = 50.dp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Stylek",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "ستايلك",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "إنشاء حساب جديد",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = StylekTextPrimary,
                fontSize = 22.sp
            )
            Text(
                text = "اختر نوع الحساب",
                style = MaterialTheme.typography.bodyMedium,
                color = StylekTextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Two Role Selection Cards matching Reference Screen 4
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: مستخدم
                Card(
                    onClick = { selectedRoleIndex = 0 },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedRoleIndex == 0) StylekCardElevated else StylekCard
                    ),
                    border = BorderStroke(
                        1.5.dp,
                        if (selectedRoleIndex == 0) StylekPrimaryBlue else StylekBorder
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (selectedRoleIndex == 0) StylekPrimaryBlue else StylekBorder),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "مستخدم",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "تسوق واستمتع بالحل المعيشي",
                            fontSize = 10.sp,
                            color = StylekTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Card 2: تاجر
                Card(
                    onClick = { selectedRoleIndex = 1 },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedRoleIndex == 1) StylekCardElevated else StylekCard
                    ),
                    border = BorderStroke(
                        1.5.dp,
                        if (selectedRoleIndex == 1) StylekPrimaryBlue else StylekBorder
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (selectedRoleIndex == 1) StylekPrimaryBlue else StylekBorder),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Store,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "تاجر",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "اعرض منتجاتك واكبر عملك",
                            fontSize = 10.sp,
                            color = StylekTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (selectedRoleIndex == 1) {
                // If merchant chosen, redirect to full Merchant Registration form
                Surface(
                    onClick = { onNavigate(Screen.MerchantRegister) },
                    shape = RoundedCornerShape(14.dp),
                    color = StylekPrimaryBlue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "إكمال بيانات حساب التاجر المتجر",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                // User Registration Fields
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("الاسم الكامل") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth().testTag("reg_name_input"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("رقم الهاتف") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth().testTag("reg_phone_input"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("البريد الإلكتروني") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth().testTag("reg_email_input"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("كلمة المرور") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = StylekTextSecondary
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth().testTag("reg_password_input"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("المدينة") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth().testTag("reg_city_input"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (name.isBlank() || phone.isBlank() || password.isBlank()) {
                            viewModel.showToast("يرجى ملء جميع الحقول المطلوبة")
                            return@Button
                        }
                        viewModel.registerUser(
                            name = name,
                            phone = phone,
                            email = email,
                            password = password,
                            city = city,
                            gender = "male",
                            age = 25
                        ) {
                            onNavigate(Screen.Home)
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StylekPrimaryBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("reg_submit_btn")
                ) {
                    Text(
                        text = "متابعة",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer link
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "لديك حساب بالفعل؟ ",
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

/**
 * Merchant Registration Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchantRegisterScreen(
    viewModel: StylekViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    var storeName by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Baghdad") }
    var address by remember { mutableStateOf("") }
    var storeType by remember { mutableStateOf("أزياء رجالية ونسائية") }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = StylekCard,
        unfocusedContainerColor = StylekCard,
        focusedBorderColor = StylekPrimaryBlue,
        unfocusedBorderColor = StylekBorder,
        focusedTextColor = StylekTextPrimary,
        unfocusedTextColor = StylekTextPrimary,
        focusedLabelColor = StylekPrimaryBlue,
        unfocusedLabelColor = StylekTextSecondary,
        cursorColor = StylekPrimaryBlue
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تسجيل متجر جديد", fontWeight = FontWeight.Bold, color = StylekTextPrimary) },
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
                .padding(24.dp)
        ) {
            Text(
                text = "انضم إلى شبكة متاجر ستايلك",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = StylekTextPrimary
            )
            Text(
                text = "اعرض منتجاتك لآلاف العملاء في جميع أنحاء العراق",
                style = MaterialTheme.typography.bodySmall,
                color = StylekTextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = storeName,
                onValueChange = { storeName = it },
                label = { Text("اسم المتجر التجاري") },
                leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) },
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().testTag("merchant_reg_store_name"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = ownerName,
                onValueChange = { ownerName = it },
                label = { Text("اسم المالك / المدير المسؤول") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().testTag("merchant_reg_owner_name"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("رقم هاتف المتجر") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().testTag("merchant_reg_phone"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("البريد الإلكتروني للعمل") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().testTag("merchant_reg_email"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("كلمة مرور الحساب") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().testTag("merchant_reg_password"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("المدينة / المحافظة") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().testTag("merchant_reg_city"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("عنوان المتجر أو المستودع بالتفصيل") },
                leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().testTag("merchant_reg_address"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = storeType,
                onValueChange = { storeType = it },
                label = { Text("تخصص المتجر") },
                leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().testTag("merchant_reg_type"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (storeName.isBlank() || ownerName.isBlank() || phone.isBlank() || password.isBlank()) {
                        viewModel.showToast("يرجى إكمال جميع بيانات المتجر")
                        return@Button
                    }
                    viewModel.registerMerchant(
                        ownerName, storeName, phone, email, password, city, address, storeType
                    ) {
                        onNavigate(Screen.MerchantDashboard)
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StylekPrimaryBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("merchant_reg_submit_btn")
            ) {
                Text(
                    text = "تقديم طلب تسجيل التاجر",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Admin Login Screen (Internal Access)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginScreen(
    viewModel: StylekViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    var adminEmail by remember { mutableStateOf("admin@stylek.app") }
    var adminPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = StylekCard,
        unfocusedContainerColor = StylekCard,
        focusedBorderColor = StylekPrimaryBlue,
        unfocusedBorderColor = StylekBorder,
        focusedTextColor = StylekTextPrimary,
        unfocusedTextColor = StylekTextPrimary,
        focusedLabelColor = StylekPrimaryBlue,
        unfocusedLabelColor = StylekTextSecondary,
        cursorColor = StylekPrimaryBlue
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تسجيل الدخول الإداري", fontWeight = FontWeight.Bold, color = StylekTextPrimary) },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(StylekCardElevated)
                    .border(1.dp, StylekBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = StylekGold,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "بوابة إدارة منصة ستايلك",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = StylekTextPrimary
            )
            Text(
                text = "صلاحيات الوصول مقصورة على مدراء النظام المعتمدين فقط",
                style = MaterialTheme.typography.bodySmall,
                color = StylekTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = adminEmail,
                onValueChange = { adminEmail = it },
                label = { Text("البريد الإلكتروني للإدارة") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().testTag("admin_email_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = adminPassword,
                onValueChange = { adminPassword = it },
                label = { Text("كلمة المرور الإدارية") },
                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().testTag("admin_password_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (adminEmail.isBlank() || adminPassword.isBlank()) {
                        viewModel.showToast("يرجى إدخال بيانات الدخول الإدارية")
                        return@Button
                    }
                    viewModel.login(adminEmail, adminPassword, "ADMIN") {
                        onNavigate(Screen.AdminDashboard)
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StylekPrimaryBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("admin_login_submit_btn")
            ) {
                Text(
                    text = "دخول لوحة الإدارة",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
