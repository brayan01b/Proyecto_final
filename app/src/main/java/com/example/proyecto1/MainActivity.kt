package com.example.proyecto1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyecto1.Screen.InicioScreen
import com.example.proyecto1.Screen.LoginScreen
import com.example.proyecto1.Screen.ProductListScreen
import com.example.proyecto1.Screen.ProductScreen
import com.example.proyecto1.Screen.PurchaseScreen
import com.example.proyecto1.Screen.RegisterScreen
import com.example.proyecto1.ViewModel.ProductViewModel
import com.example.proyecto1.ViewModel.UserViewModel

class MainActivity : ComponentActivity() {
    private val userViewModel by viewModels<UserViewModel>()

    private val productViewModel by viewModels<ProductViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "login") {
                composable("login") { LoginScreen(navController, userViewModel) }
                composable("register") { RegisterScreen(navController, userViewModel) }
                composable("products") { ProductScreen(navController, productViewModel) }
                composable("purchase") { PurchaseScreen(navController, productViewModel) }
                composable("inicio") { InicioScreen(navController) }
                composable("producto") { ProductListScreen(navController, productViewModel) }
            }
        }
    }
}
