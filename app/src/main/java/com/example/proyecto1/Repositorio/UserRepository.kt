package com.example.proyecto1.Repositorio

import com.example.proyecto1.Dao.UserDao
import com.example.proyecto1.Entidad.User

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun getUser(username: String, password: String): User? {
        return userDao.getUser(username, password)
    }
}
