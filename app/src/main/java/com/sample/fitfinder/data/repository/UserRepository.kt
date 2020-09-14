package com.sample.fitfinder.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sample.fitfinder.domain.User
import com.sample.fitfinder.domain.UserRole
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {
    fun getUsers() : LiveData<List<User>> {
        val users = arrayListOf<User>(
            User(1, "David", "David", "", UserRole.ADMIN, ""),
            User(2, "Gareth", "Gareth", "", UserRole.ADMIN, "")
            )
        val data = MutableLiveData<List<User>>()
        data.value = users
        return data
    }

    fun getCurrentUser() : LiveData<User> {
        val data = MutableLiveData<User>()
        data.value = User(1, "David", "David", "david.ng@gmail.com", UserRole.ADMIN, "")
        return data
    }
}