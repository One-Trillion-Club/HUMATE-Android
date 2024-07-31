package com.otclub.humate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.otclub.humate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    }
}