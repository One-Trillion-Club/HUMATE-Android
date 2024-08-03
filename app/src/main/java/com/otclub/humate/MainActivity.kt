package com.otclub.humate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.otclub.humate.auth.activity.AuthActivity
import com.otclub.humate.databinding.ActivityMainBinding
import com.otclub.humate.sharedpreferences.SharedPreferencesManager


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 로그인 상태 확인
        val sharedPreferencesManager = SharedPreferencesManager(this)
        val isLoggedIn = sharedPreferencesManager.getIsLogin()

        Log.i("메인 액티비티: 로그인 상태", isLoggedIn.toString())

        if (!isLoggedIn) {
            // 로그인 상태가 아니면 AuthActivity로 전환
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish() // 현재 MainActivity 종료
            return
        }


        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val bottomNavigationView = mBinding.bottomNavigationView
        bottomNavigationView.itemIconTintList = null

        // 내비게이션들을 담는 호스트
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.my_nav_host)

        // 내비게이션 컨트롤러
        val navController = navHostFragment?.findNavController()

        if (navController != null) {
            NavigationUI.setupWithNavController(bottomNavigationView, navController)
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) // 액티비티의 앱바(App Bar)로 지정

        val actionBar: ActionBar? = supportActionBar // 앱바 제어를 위해 액션 바 접근
    }

    fun setToolbarTitle(title: String) {
        val toolbarTitle: TextView = findViewById(R.id.toolbar_title)
        toolbarTitle.text = title
    }

    fun getToolbar(): Toolbar {
        return findViewById(R.id.toolbar)
    }
}