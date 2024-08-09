package com.otclub.humate.auth.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.otclub.humate.R
import com.otclub.humate.auth.fragment.SignInFragment
import com.otclub.humate.databinding.ActivityAuthBinding

/**
 * 회원 가입/로그인 Activity
 * @author 조영욱
 * @since 2024.08.01
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01  	조영욱        최초 생성
 * </pre>
 */
class AuthActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityAuthBinding

    /**
     * 액티비티 생성 시 콜백
     */
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