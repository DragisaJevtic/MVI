package com.example.supertestapplication.repository.store

import android.content.Context
import android.content.SharedPreferences

class TokenStore(private val context: Context) {

    private val sharedPrefs: Lazy<SharedPreferences> = lazy {
        context.getSharedPreferences("com.example.supertestapplication", Context.MODE_PRIVATE)
    }

    companion object {
        private const val USER_TOKEN_KEY = "user_token"
    }

    val getAccessToken: String? = sharedPrefs.value.getString(USER_TOKEN_KEY, null)

    fun saveToken(token: String) {
        sharedPrefs.value.edit().putString(USER_TOKEN_KEY, token).apply()
    }
}