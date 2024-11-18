package com.example.proyecto1.ViewModel

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.proyecto1.Database.AppDatabase
import com.example.proyecto1.Entidad.Product
import com.example.proyecto1.Repositorio.ProductRepository
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProductRepository
    val allProducts: LiveData<List<Product>>
    private val context = getApplication<Application>().applicationContext

    private val _cart = MutableLiveData<Map<Product, Int>>(emptyMap())
    val cart: LiveData<Map<Product, Int>> = _cart
    private fun showStockLimitReachedMessage() {
        // Aquí puedes implementar un mensaje de alerta
        // Si estás en un entorno de Android con Jetpack Compose, podrías usar Snackbar
        println("No puedes agregar más de este producto, se alcanzó el stock disponible.")
    }


    init {
        val productDao = AppDatabase.getDatabase(application).productDao()
        repository = ProductRepository(productDao)

        allProducts = liveData {
            repository.allProducts.collect { products ->
                emit(products)
            }
        }
    }


    fun addProduct(name: String, price: Double, stock: Int, codigobarra: String) {
        viewModelScope.launch {
            val product = Product(name = name, price = price, stock = stock, codigobarra = codigobarra)
            repository.addProduct(product)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }
    fun addToCartScanner(product: Product) {

        if (product.stock > 0) {

            val currentCartSc = _cart.value
            if (currentCartSc != null) {
                if (currentCartSc.containsKey(product)) {

                    return
                } else {

                    _cart.value = currentCartSc + (product to 1)


                    Toast.makeText(context, "${product.name} agregado al carrito.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {

            Toast.makeText(context, "El producto ${product.name} no tiene stock disponible.", Toast.LENGTH_SHORT).show()
        }
    }

    fun addToCart(product: Product) {
        val currentCart = _cart.value?.toMutableMap() ?: mutableMapOf()
        val currentQuantity = currentCart[product] ?: 0


        if (currentQuantity < product.stock) {
            currentCart[product] = currentQuantity + 1
            _cart.value = currentCart
        } else {

            showStockLimitReachedMessage()
        }
    }



    fun removeFromCart(product: Product) {
        val currentCart = _cart.value?.toMutableMap() ?: mutableMapOf()
        val currentQuantity = currentCart[product] ?: return

        if (currentQuantity > 1) {
            currentCart[product] = currentQuantity - 1
        } else {
            currentCart.remove(product)
        }
        _cart.value = currentCart
    }
    fun clearCart() {
        _cart.value = mutableMapOf()
    }
    fun getTotalPrice(): Double {
        return _cart.value?.entries?.sumOf { it.key.price * it.value } ?: 0.0
    }
    fun finalizePurchase() {
        val currentCart = _cart.value ?: return

        viewModelScope.launch {
            currentCart.forEach { (product, quantity) ->

                val newStock = product.stock - quantity
                if (newStock >= 0) {
                    val updatedProduct = product.copy(stock = newStock)
                    repository.updateProduct(updatedProduct)
                }
            }

            clearCart()
        }
    }

}
