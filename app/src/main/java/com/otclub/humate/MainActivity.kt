package com.otclub.humate

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.otclub.humate.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private var defaultToolbar: Toolbar? = null
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