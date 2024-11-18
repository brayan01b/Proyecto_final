package com.example.proyecto1.Database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.proyecto1.Dao.ProductDao
import com.example.proyecto1.Dao.UserDao
import com.example.proyecto1.Entidad.Product
import com.example.proyecto1.Entidad.User

@Database(entities = [User::class, Product::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "login_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
