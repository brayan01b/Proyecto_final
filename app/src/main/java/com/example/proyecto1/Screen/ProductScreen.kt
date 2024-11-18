package com.example.proyecto1.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.proyecto1.Entidad.Product
import com.example.proyecto1.ViewModel.ProductViewModel

@Composable
fun ProductScreen(navController: NavHostController, productViewModel: ProductViewModel) {
    val products by productViewModel.allProducts.observeAsState(emptyList())
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var codigobarra by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedProduct: Product? by remember { mutableStateOf(null) }
    var showEditDialog by remember { mutableStateOf(false) }

    // Estados para escáner
    var showScanner by remember { mutableStateOf(false) }
    var scannerMode by remember { mutableStateOf("") } // "add" o "search"

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
                    if (scannerMode == "add") {
                        codigobarra = barcode // Para agregar nuevo producto
                    } else if (scannerMode == "search") {
                        searchQuery = barcode // Para buscar producto
                    }
                },
                onClose = { showScanner = false }
            )
        }
    }
}
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Gestión de Productos", style = MaterialTheme.typography.titleLarge)

        // Campo de búsqueda y botón de escaneo
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar Producto") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = {
                scannerMode = "search"
                showScanner = true
            }) {
                Icon(Icons.Default.Search, contentDescription = "Escanear para Buscar")
            }
        }

        // Campos de entrada para nuevo producto
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre del Producto") },
            modifier = Modifier.fillMaxWidth().padding(end = 56.dp)
        )
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Precio") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(end = 56.dp)
        )
        OutlinedTextField(
            value = stock,
            onValueChange = { stock = it },
            label = { Text("Stock") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(end = 56.dp)
        )

        // Escáner para agregar código de barras
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = codigobarra,
                onValueChange = { codigobarra = it },
                label = { Text("Código de Barras") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = {
                scannerMode = "add"
                showScanner = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Escanear para Agregar")
            }
        }

        // Botón para agregar producto
        Button(onClick = {
            if (name.isNotBlank() && price.isNotBlank() && stock.isNotBlank() && codigobarra.isNotBlank()) {
                productViewModel.addProduct(name, price.toDouble(), stock.toInt(), codigobarra)
                name = ""
                price = ""
                stock = ""
                codigobarra = ""
            }
        }, modifier = Modifier.padding(top = 8.dp)) {
            Text("Agregar Producto")
        }

        // Lista de productos filtrada por el código de barras o nombre
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        LazyColumn {
            items(products.filter {
                it.name.contains(searchQuery, ignoreCase = true) || it.codigobarra == searchQuery
            }) { product ->
                ProductItem(
                    product = product,
                    onEdit = { selectedProduct = it; showEditDialog = true },
                    onDelete = { productViewModel.deleteProduct(it) }
                )
            }
        }
    }

    // Dialog para editar producto
    if (showEditDialog && selectedProduct != null) {
        EditProductDialog(
            product = selectedProduct!!,
            onDismiss = { showEditDialog = false },
            onSave = { updatedProduct ->
                productViewModel.updateProduct(updatedProduct)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun ProductItem(product: Product, onEdit: (Product) -> Unit, onDelete: (Product) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Nombre: ${product.name}")
                Text(text = "Precio: ${product.price}")
                Text(text = "Stock: ${product.stock}")
                Text(text = "Codigo de barras: ${product.codigobarra}")
            }
            Row {
                IconButton(onClick = { onEdit(product) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = { onDelete(product) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}

@Composable
fun EditProductDialog(product: Product, onDismiss: () -> Unit, onSave: (Product) -> Unit) {
    var name by remember { mutableStateOf(product.name) }
    var price by remember { mutableStateOf(product.price.toString()) }
    var stock by remember { mutableStateOf(product.stock.toString()) }
    var codigobarra by remember { mutableStateOf(product.codigobarra) }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Editar Producto") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Precio") })
                OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") })
                OutlinedTextField(value = codigobarra, onValueChange = { stock = it }, label = { Text("Codigo de barras") })
            }
        },
        confirmButton = {
            Button(onClick = {
                val updatedProduct = product.copy(
                    name = name,
                    price = price.toDouble(),
                    stock = stock.toInt(),
                    codigobarra = codigobarra
                )
                onSave(updatedProduct)
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
}
