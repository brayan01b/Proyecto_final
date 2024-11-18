package com.example.proyecto1.Screen



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search

import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto1.Entidad.Product
import com.example.proyecto1.ViewModel.ProductViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    navController: NavController,
    productViewModel: ProductViewModel = viewModel(),

) {
    val products by productViewModel.allProducts.observeAsState(emptyList())
    var showScanner by remember { mutableStateOf(false) } // Para mostrar el escáner
    var scannerMode by remember { mutableStateOf("add") } // "add" para agregar al carrito
    var searchQuery by remember { mutableStateOf("") }
    var codigobarra by remember { mutableStateOf("") }
    // Mostrar el escáner en un modal (Dialog)
    if (showScanner) {
        Dialog(onDismissRequest = { showScanner = false }) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp)
                    .fillMaxWidth() // Limita el ancho al 90% del ancho de la pantalla
                    .fillMaxHeight(0.5f)
            ) {
                BarcodeScannerScreen(
                    onBarcodeDetected = { barcode ->
                        showScanner = false
                        codigobarra = barcode
                        // Aquí buscamos el producto por el código de barras
                        val product = products.find { it.codigobarra == barcode }
                        if (product != null) {
                            // Si el producto es encontrado, agregarlo al carrito
                            productViewModel.addToCartScanner(product)
                        }
                    },
                    onClose = { showScanner = false }
                )
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("purchase") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Ir al carrito")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Lista de Productos",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    scannerMode = "add" // Modo agregar
                    showScanner = true // Mostrar escáner
                }) {
                    Icon(Icons.Default.Search, contentDescription = "Escanear código de barras")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Escanear producto para agregar al carrito")
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(products) { product ->
                    ProductItem(
                        product = product,
                        onAddToCart = { selectedProduct ->
                            productViewModel.addToCart(selectedProduct)
                        },
                        productViewModel = productViewModel // Pasamos el ProductViewModel aquí
                    )
                }
            }
        }
    }
}
@Composable
fun ProductItem(
    product: Product,
    onAddToCart: (Product) -> Unit,
    productViewModel: ProductViewModel // Usamos ProductViewModel aquí
) {
    // Obtenemos la cantidad actual del producto en el carrito
    val cartItems by productViewModel.cart.observeAsState(initial = emptyMap())
    val currentQuantityInCart = cartItems[product] ?: 0

    // Verificamos si hay stock suficiente para habilitar o deshabilitar el botón
    val isButtonEnabled = currentQuantityInCart < product.stock

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = product.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "Precio: $${product.price}", fontSize = 14.sp)
                Text(text = "Stock: ${product.stock}", fontSize = 12.sp)
            }
            IconButton(
                onClick = { onAddToCart(product) },
                enabled = isButtonEnabled // Habilitamos o deshabilitamos el botón según el stock
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Agregar al carrito",
                    tint = if (isButtonEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    }
}
