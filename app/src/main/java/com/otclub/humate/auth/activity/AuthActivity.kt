package com.otclub.humate.auth.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.otclub.humate.R
import com.otclub.humate.auth.fragment.SignInFragment
import com.otclub.humate.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        // fragment 지정
        supportFragmentManager.beginTransaction()
            .replace(R.id.authFragment, SignInFragment())
            .commit()

    }

}