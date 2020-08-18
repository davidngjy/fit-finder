package com.sample.fitfinder.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sample.fitfinder.domain.User

class UserRepository {
    fun getUsers() : LiveData<List<User>> {
        val users = arrayListOf<User>(
            User(1, "David", "David", "", null),
            User(2, "Gareth", "Gareth", "", null)
            )
        val data = MutableLiveData<List<User>>()
        data.value = users
        return data
    }
}