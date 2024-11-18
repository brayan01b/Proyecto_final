package com.example.proyecto1.Screen

import androidx.compose.runtime.livedata.observeAsState


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.proyecto1.Entidad.Product
import com.example.proyecto1.ViewModel.ProductViewModel
import androidx.compose.runtime.livedata.observeAsState

import com.example.proyecto1.ViewModel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseScreen(navController: NavHostController, productViewModel: ProductViewModel = viewModel()) {
    // Observa el LiveData con observeAsState()
    val cartItems by productViewModel.cart.observeAsState(initial = emptyMap())

    // Calcula el total del carrito
    val totalAmount = remember(cartItems) {
        cartItems.entries.sumOf { (product, quantity) -> product.price * quantity }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Carrito de Compras",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Contenedor scrollable para los productos
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(cartItems.entries.toList()) { (product, quantity) ->
                    CartItem(
                        product = product,
                        quantity = quantity,
                        onAddClick = { productViewModel.addToCart(product) },
                        onRemoveClick = { productViewModel.removeFromCart(product) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar el total
            Text(
                text = "Total: $${String.format("%.2f", totalAmount)}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Botón fijo al final
            Button(
                onClick = {
                    productViewModel.finalizePurchase()
                    navController.navigate("producto")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Finalizar Compra")
            }
        }
    }
}

@Composable
fun CartItem(
    product: Product,
    quantity: Int,
    onAddClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Divider(modifier = Modifier.padding(vertical = 1.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = product.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Subtotal: $${String.format("%.2f", product.price * quantity)}")
        }
        Spacer(modifier = Modifier.width(16.dp))
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onRemoveClick) {
                Icon(Icons.Default.Delete, contentDescription = "Remove Item")
            }
            Text(text = "$quantity")
            IconButton(
                onClick = onAddClick,
                enabled = quantity < product.stock // Deshabilitar si alcanza el stock
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    }
}
