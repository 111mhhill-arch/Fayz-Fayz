package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.StylekRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: StylekRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = StylekRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Stylek", appName)
    }

    @Test
    fun `database starts clean with zero mock products`() = runBlocking {
        val products = repository.getAllApprovedProductsSnapshot()
        assertTrue("Database must have 0 products initially", products.isEmpty())
    }

    @Test
    fun `user registration and login journey`() = runBlocking {
        val regResult = repository.registerUser(
            name = "أحمد العراقي",
            phone = "07712345678",
            email = "ahmed@example.com",
            password = "password123",
            city = "Baghdad",
            gender = "male",
            age = 26
        )
        assertTrue(regResult.isSuccess)
        val user = regResult.getOrNull()
        assertNotNull(user)
        assertEquals("USER", user?.role)

        val loginResult = repository.login("07712345678", "password123", "USER")
        assertTrue(loginResult.isSuccess)
        assertEquals(user?.id, loginResult.getOrNull()?.id)
    }

    @Test
    fun `merchant registration product approval and checkout journey`() = runBlocking {
        repository.initializeDefaults()

        // 1. Merchant registration
        val merchantReg = repository.registerMerchant(
            ownerName = "علي التميمي",
            storeName = "متجر الفخامة",
            phone = "07800000000",
            email = "store@example.com",
            password = "store123456",
            city = "Baghdad",
            address = "شارع فلسطين",
            storeType = "أزياء رجالية"
        )
        assertTrue(merchantReg.isSuccess)
        val merchant = merchantReg.getOrNull()!!
        assertEquals("PENDING", merchant.status)

        // 2. Admin approves merchant
        repository.updateMerchantStatus(merchant.id, "APPROVED", 1L)
        val updatedMerchant = repository.getMerchantById(merchant.id)
        assertEquals("APPROVED", updatedMerchant?.status)

        // 3. Merchant adds a product (starts PENDING)
        val addProdResult = repository.addProduct(
            merchantId = merchant.id,
            storeName = merchant.storeName,
            name = "قميص كتان بيج فاخر",
            description = "قميص صيفي من الكتان الطبيعي",
            category = "cat_men",
            gender = "male",
            style = "style_casual",
            price = 45000.0,
            discountPrice = 40000.0,
            stockQuantity = 5,
            sizes = "M, L, XL",
            colors = "Beige, White",
            city = "Baghdad",
            sku = "SHIRT-01",
            imageUrl = ""
        )
        assertTrue(addProdResult.isSuccess)
        val prodId = addProdResult.getOrNull()!!

        // 4. Admin approves product
        repository.updateProductStatus(prodId, "APPROVED", 1L)
        val approvedProd = repository.getProductById(prodId)
        assertEquals("APPROVED", approvedProd?.status)

        // 5. User registers & places order
        val userResult = repository.registerUser(
            name = "سارة",
            phone = "07799999999",
            email = "sara@example.com",
            password = "password123",
            city = "Baghdad",
            gender = "female",
            age = 24
        )
        val user = userResult.getOrNull()!!

        repository.addToCart(user.id, prodId, 2, "M", "Beige")
        val cartItems = database.cartDao().getCartItems(user.id)
        val cartSnapshot = listOf(
            com.example.data.local.entity.CartItemEntity(
                userId = user.id,
                productId = prodId,
                quantity = 2,
                selectedSize = "M",
                selectedColor = "Beige"
            )
        )

        val orderResult = repository.placeOrder(
            userId = user.id,
            recipientName = "سارة",
            phone = "07799999999",
            city = "Baghdad",
            address = "المنصور",
            notes = "يرجى الاتصال قبل الوصول",
            paymentMethod = "COD",
            cartItems = cartSnapshot
        )
        assertTrue(orderResult.isSuccess)
        val order = orderResult.getOrNull()!!

        // Stock decreased from 5 to 3
        val postStock = repository.getProductById(prodId)?.stockQuantity
        assertEquals(3, postStock)

        // Subtotal = 40000 * 2 = 80000. Total = 80000 + 5000 delivery = 85000
        assertEquals(85000.0, order.totalAmount, 0.01)
        // Commission (1%) = 80000 * 0.01 = 800
        assertEquals(800.0, order.platformCommissionAmount, 0.01)
    }
}
