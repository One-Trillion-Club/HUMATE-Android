package com.otclub.humate

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
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
    private var defaultToolbar: Toolbar? = null
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

            bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
                restoreToolbar()
                NavigationUI.onNavDestinationSelected(menuItem, navController)
            }
        }

        // 기본 Toolbar 설정
        setupDefaultToolbar()
    }

    private fun setupDefaultToolbar() {
        defaultToolbar = layoutInflater.inflate(R.layout.common_toolbar, null) as Toolbar
        val toolbarContainer: FrameLayout = findViewById(R.id.toolbar_container)
        toolbarContainer.addView(defaultToolbar)
        setSupportActionBar(defaultToolbar)
    }

    fun setToolbarTitle(title: String) {
        val toolbarTitle: TextView = findViewById(R.id.toolbar_title)
        toolbarTitle.text = title
    }

    fun getToolbar(): Toolbar {
        return defaultToolbar ?: findViewById(R.id.toolbar)
    }

    fun replaceToolbar(newToolbar: Toolbar) {
        val toolbarContainer: FrameLayout = findViewById(R.id.toolbar_container)
        toolbarContainer.removeAllViews()
        toolbarContainer.addView(newToolbar)
        setSupportActionBar(newToolbar)
    }

    fun restoreToolbar() {
        val toolbarContainer: FrameLayout = findViewById(R.id.toolbar_container)
        toolbarContainer.removeAllViews()

        // 기존 Toolbar를 복구
        defaultToolbar?.visibility = View.VISIBLE
        toolbarContainer.addView(defaultToolbar)
        setSupportActionBar(defaultToolbar)
    }
}