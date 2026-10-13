package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.local.entity.MerchantEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.ProductEntity
import com.example.data.local.entity.StyleProfileEntity
import com.example.data.local.entity.UserEntity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

/**
 * Service managing synchronization between local Room cache and Firebase Cloud Firestore.
 * Always accesses the custom provisioned database ID configured in firebase_applet_config.xml.
 */
class FirebaseSyncService(context: Context) {

    private val dbId = context.getString(R.string.firestore_database_id)
    private val firestore = FirebaseFirestore.getInstance(dbId)
    private val auth: FirebaseAuth = Firebase.auth

    fun getCurrentFirebaseUser() = auth.currentUser

    suspend fun ensureFirebaseAuth(email: String, pass: String): String? {
        val current = auth.currentUser
        if (current != null) return current.uid
        val safeEmail = if (email.contains("@") && email.contains(".")) email.trim() else "user_${System.currentTimeMillis()}@stylek.iq"
        val safePass = if (pass.length >= 6) pass else "StylekPass123"
        return try {
            auth.signInWithEmailAndPassword(safeEmail, safePass).await().user?.uid
        } catch (e: Exception) {
            try {
                auth.createUserWithEmailAndPassword(safeEmail, safePass).await().user?.uid
            } catch (e2: Exception) {
                Log.w("FirebaseSyncService", "Firebase Auth note: ${e2.message}")
                null
            }
        }
    }

    suspend fun syncUserToFirestore(user: UserEntity, rawPass: String = "StylekPass123", profile: StyleProfileEntity? = null) {
        val uid = auth.currentUser?.uid ?: ensureFirebaseAuth(user.email, rawPass) ?: user.id.toString()
        try {
            val userMap = mutableMapOf<String, Any>(
                "userId" to uid,
                "localId" to user.id,
                "name" to user.name,
                "email" to user.email,
                "phone" to user.phone,
                "role" to user.role,
                "city" to user.city,
                "gender" to user.gender,
                "age" to user.age,
                "status" to user.status,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (profile != null) {
                userMap["heightCm"] = profile.heightCm
                userMap["weightKg"] = profile.weightKg
                userMap["size"] = profile.size
                userMap["bodyShape"] = profile.bodyShape
                userMap["skinTone"] = profile.skinTone
                userMap["styleType"] = profile.styleType
                userMap["favoriteColors"] = profile.favoriteColors
            }

            firestore.collection("users").document(uid).set(userMap, SetOptions.merge()).await()
            Log.d("FirebaseSyncService", "User synced to Firestore: $uid")
        } catch (e: Exception) {
            Log.w("FirebaseSyncService", "Firestore sync note: ${e.message}")
        }
    }

    suspend fun syncMerchantToFirestore(merchant: MerchantEntity) {
        val uid = auth.currentUser?.uid ?: merchant.userId.toString()
        try {
            val merchantMap = mapOf(
                "merchantId" to merchant.id.toString(),
                "userId" to uid,
                "storeName" to merchant.storeName,
                "ownerName" to merchant.ownerName,
                "phone" to merchant.phone,
                "email" to merchant.email,
                "city" to merchant.city,
                "address" to merchant.address,
                "storeType" to merchant.storeType,
                "description" to merchant.description,
                "logoUrl" to merchant.logoUrl,
                "bannerUrl" to merchant.bannerUrl,
                "status" to merchant.status,
                "followerCount" to merchant.followerCount,
                "rating" to merchant.rating.toDouble(),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            firestore.collection("merchants").document(merchant.id.toString())
                .set(merchantMap, SetOptions.merge()).await()
            Log.d("FirebaseSyncService", "Merchant synced to Firestore: ${merchant.id}")
        } catch (e: Exception) {
            Log.w("FirebaseSyncService", "Firestore sync merchant note: ${e.message}")
        }
    }

    suspend fun syncMerchantStatus(merchantId: Long, status: String) {
        try {
            firestore.collection("merchants").document(merchantId.toString())
                .update("status", status, "updatedAt", FieldValue.serverTimestamp()).await()
            Log.d("FirebaseSyncService", "Merchant status updated in Firestore: $merchantId -> $status")
        } catch (e: Exception) {
            Log.w("FirebaseSyncService", "Firestore update merchant status note: ${e.message}")
        }
    }

    suspend fun syncProductToFirestore(product: ProductEntity) {
        val uid = auth.currentUser?.uid ?: product.merchantId.toString()
        try {
            val productMap = mapOf(
                "productId" to product.id.toString(),
                "merchantId" to product.merchantId.toString(),
                "userId" to uid,
                "storeName" to product.storeName,
                "name" to product.name,
                "description" to product.description,
                "category" to product.category,
                "gender" to product.gender,
                "style" to product.style,
                "price" to product.price,
                "discountPrice" to (product.discountPrice ?: product.price),
                "stockQuantity" to product.stockQuantity,
                "sizes" to product.sizes,
                "colors" to product.colors,
                "city" to product.city,
                "imageUrl" to product.imageUrl,
                "status" to product.status,
                "salesCount" to product.salesCount,
                "rating" to product.rating.toDouble(),
                "isFeatured" to product.isFeatured,
                "updatedAt" to FieldValue.serverTimestamp()
            )

            firestore.collection("products").document(product.id.toString())
                .set(productMap, SetOptions.merge()).await()
            Log.d("FirebaseSyncService", "Product synced to Firestore: ${product.id}")
        } catch (e: Exception) {
            Log.w("FirebaseSyncService", "Firestore sync product note: ${e.message}")
        }
    }

    suspend fun syncProductStatus(productId: Long, status: String) {
        try {
            firestore.collection("products").document(productId.toString())
                .update("status", status, "updatedAt", FieldValue.serverTimestamp()).await()
            Log.d("FirebaseSyncService", "Product status updated in Firestore: $productId -> $status")
        } catch (e: Exception) {
            Log.w("FirebaseSyncService", "Firestore update product status note: ${e.message}")
        }
    }

    suspend fun syncOrderToFirestore(order: OrderEntity, merchantIds: List<String>) {
        val uid = auth.currentUser?.uid ?: order.userId.toString()
        try {
            val orderMap = mapOf(
                "orderId" to order.id.toString(),
                "orderNumber" to order.orderNumber,
                "userId" to uid,
                "recipientName" to order.recipientName,
                "phone" to order.phone,
                "city" to order.city,
                "address" to order.address,
                "paymentMethod" to order.paymentMethod,
                "status" to order.status,
                "totalAmount" to order.totalAmount,
                "deliveryFee" to order.deliveryFee,
                "discountAmount" to order.discountAmount,
                "notes" to order.notes,
                "merchantIds" to merchantIds,
                "updatedAt" to FieldValue.serverTimestamp()
            )

            firestore.collection("orders").document(order.id.toString())
                .set(orderMap, SetOptions.merge()).await()
            Log.d("FirebaseSyncService", "Order synced to Firestore: ${order.id}")
        } catch (e: Exception) {
            Log.w("FirebaseSyncService", "Firestore sync order note: ${e.message}")
        }
    }

    suspend fun syncOrderStatus(orderId: Long, status: String) {
        try {
            firestore.collection("orders").document(orderId.toString())
                .update("status", status, "updatedAt", FieldValue.serverTimestamp()).await()
            Log.d("FirebaseSyncService", "Order status updated in Firestore: $orderId -> $status")
        } catch (e: Exception) {
            Log.w("FirebaseSyncService", "Firestore update order status note: ${e.message}")
        }
    }
}
