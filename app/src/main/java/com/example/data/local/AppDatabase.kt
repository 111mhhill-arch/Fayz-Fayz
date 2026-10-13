package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.CartDao
import com.example.data.local.dao.FavoriteDao
import com.example.data.local.dao.MerchantDao
import com.example.data.local.dao.OrderDao
import com.example.data.local.dao.PlatformDao
import com.example.data.local.dao.ProductDao
import com.example.data.local.dao.UserDao
import com.example.data.local.dao.WardrobeDao
import com.example.data.local.entity.AdminLogEntity
import com.example.data.local.entity.BannerEntity
import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.FavoriteEntity
import com.example.data.local.entity.MerchantEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.OfferEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.OrderItemEntity
import com.example.data.local.entity.PlatformSettingEntity
import com.example.data.local.entity.ProductEntity
import com.example.data.local.entity.StyleProfileEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WardrobeItemEntity

@Database(
    entities = [
        UserEntity::class,
        StyleProfileEntity::class,
        MerchantEntity::class,
        ProductEntity::class,
        CartItemEntity::class,
        FavoriteEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        WardrobeItemEntity::class,
        OfferEntity::class,
        BannerEntity::class,
        NotificationEntity::class,
        AdminLogEntity::class,
        PlatformSettingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun merchantDao(): MerchantDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun wardrobeDao(): WardrobeDao
    abstract fun platformDao(): PlatformDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stylek_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
