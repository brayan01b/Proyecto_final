package com.example.proyecto1.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto1.Database.AppDatabase
import com.example.proyecto1.Entidad.User
import com.example.proyecto1.Repositorio.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository
    private val userDao = AppDatabase.getDatabase(application).userDao()

    init {
        userRepository = UserRepository(userDao)
    }

    fun registerUser(username: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            userRepository.insertUser(User(username = username, password = password))
            onSuccess()
        }
    }

    fun loginUser(username: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = userRepository.getUser(username, password)
            onResult(user != null)
        }
    }
}
