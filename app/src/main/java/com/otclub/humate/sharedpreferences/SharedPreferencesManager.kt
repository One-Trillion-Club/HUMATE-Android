package com.otclub.humate.sharedpreferences

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SharedPreferencesManager(context: Context) {
    private val authSharedPreferences: SharedPreferences =
        context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)

    fun setIsLogin(isLogin: Boolean) {
        with(authSharedPreferences.edit()) {
            putBoolean("isLoggedIn", isLogin)
            apply()
        }

        Log.i("쉐어드프리퍼런스매니저: 셋이즈로그인", authSharedPreferences.getBoolean("isLoggedIn", false).toString())
    }

    fun getIsLogin(): Boolean {
        return authSharedPreferences.getBoolean("isLoggedIn", false)
    }

    fun clear() {
        with(authSharedPreferences.edit()) {
            clear()
            apply()
        }
    }

    fun setLoginToken(accessToken: String, refreshToken: String) {
        with(authSharedPreferences.edit()) {
            putString("accessToken", accessToken)
            putString("refreshToken", refreshToken)
            apply()
        }
    }

    fun getLoginToken(): Pair<String?, String?> {
        val accessToken = authSharedPreferences.getString("accessToken", null)
        val refreshToken = authSharedPreferences.getString("refreshToken", null)
        return Pair(accessToken, refreshToken)
    }


}
