package com.example.proyecto1.Entidad

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val name: String,
    val price: Double,
    var stock: Int,
    val codigobarra: String,
)