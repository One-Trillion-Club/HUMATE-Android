package com.otclub.humate

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * 앱 접속 첫 화면 Activity
 * @author 김지현
 * @since 2024.08.07
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.07  	김지현        최초 생성
 * </pre>
 */
@SuppressLint("CustomSplashScreen")
class CommonSplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common_splash)

        val leftCoverView = findViewById<View>(R.id.leftCoverView)
        val rightCoverView = findViewById<View>(R.id.rightCoverView)
        val logoView = findViewById<View>(R.id.logoView)
        val humateView = findViewById<View>(R.id.humateView)
        val text = findViewById<TextView>(R.id.text)

        val slideDownAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down_reveal)
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        val icon1 = findViewById<View>(R.id.ic1)
        val icon2 = findViewById<View>(R.id.ic2)
        val icon3 = findViewById<View>(R.id.ic3)
        val icon4 = findViewById<View>(R.id.ic4)
        val icon5 = findViewById<View>(R.id.ic5)
        val icon6 = findViewById<View>(R.id.ic6)

        val fadeInViews = listOf(icon2, icon3, icon6, icon4, icon1, icon5)
        val delayIncrement = 300L // 애니메이션 시작 지연 시간

        val coverAnimationDuration = 1500L // 덮개 애니메이션의 지속 시간
        val logoAndTextStartDelay = coverAnimationDuration + 300L // 덮개 애니메이션 후 로고와 텍스트 애니메이션 시작 지연 시간
        val iconsStartDelay = logoAndTextStartDelay + 1000L // 로고와 텍스트 애니메이션 후 아이콘 애니메이션 시작 지연 시간

        // 아이콘들을 초기 상태로 보이지 않도록 설정
        fadeInViews.forEach { view ->
            view.visibility = View.INVISIBLE
        }

        text.visibility = View.INVISIBLE

        leftCoverView.visibility = View.VISIBLE // 초기 상태를 VISIBLE로 설정
        leftCoverView.startAnimation(slideDownAnimation)

        rightCoverView.visibility = View.VISIBLE // 초기 상태를 VISIBLE로 설정
        rightCoverView.startAnimation(slideDownAnimation)

        leftCoverView.postDelayed({
            leftCoverView.visibility = View.GONE
            rightCoverView.visibility = View.GONE

            // logoView와 humateView에 fade_in 애니메이션 적용
            logoView.visibility = View.VISIBLE
            humateView.visibility = View.VISIBLE
            text.visibility = View.VISIBLE

            logoView.startAnimation(fadeInAnimation)
            humateView.startAnimation(fadeInAnimation)
            text.startAnimation(fadeInAnimation)

            logoView.postDelayed({
                fadeInViews.forEachIndexed { index, view ->
                    val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_icon)
                    fadeIn.startOffset = index * delayIncrement // 순차적으로 지연 시간 적용
                    view.startAnimation(fadeIn)
                    view.visibility = View.VISIBLE

                    // 모든 애니메이션이 끝난 후 MainActivity로 이동
                    view.postDelayed({
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish() // 현재 액티비티 종료
                    }, (delayIncrement * fadeInViews.size) + 1000L) // 아이콘 애니메이션이 끝난 후 MainActivity로 전환
                }
            }, 1000L)
        }, coverAnimationDuration)

    }
}

