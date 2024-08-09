package com.otclub.humate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.otclub.humate.auth.activity.AuthActivity
import com.otclub.humate.databinding.ActivityMainBinding
import com.otclub.humate.member.viewmodel.MemberViewModel
import com.otclub.humate.retrofit.RetrofitConnection
import com.otclub.humate.sharedpreferences.SharedPreferencesManager


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var memberViewModel: MemberViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RetrofitConnection.init(this)
        memberViewModel = ViewModelProvider(this).get(MemberViewModel::class.java)

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

        // 언어 설정
        memberViewModel.fetchGetMyProfile(
            onSuccess = { response ->
                Log.i("메인 액티비티: 언어 상태", response.toString())
                if (response.memberId[0] == 'K') {
                    sharedPreferencesManager.setLanguage(1)
                } else {
                    sharedPreferencesManager.setLanguage(2)
                }
                Log.i("메인 액티비티: 언어 상태", sharedPreferencesManager.getLanguage().toString())

            },
            onError = { error ->
                sharedPreferencesManager.setLanguage(1)
                Log.i("메인 액티비티: 언어 상태", sharedPreferencesManager.getLanguage().toString())
            }
        )

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


    fun hideBottomNavigationBar() {
        val bottomNavigationView = mBinding.bottomNavigationView
        bottomNavigationView.visibility = View.GONE
    }

    fun showBottomNavigationBar() {
        val bottomNavigationView = mBinding.bottomNavigationView
        bottomNavigationView.visibility = View.VISIBLE
    }
}