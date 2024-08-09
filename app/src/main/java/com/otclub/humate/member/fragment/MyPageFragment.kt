package com.otclub.humate.member.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.otclub.humate.R
import com.otclub.humate.auth.activity.AuthActivity
import com.otclub.humate.databinding.MemberFragmentMyPageBinding
import com.otclub.humate.member.viewmodel.MemberViewModel

/**
 * 마이 페이지 Fragment
 * @author 조영욱
 * @since 2024.08.03
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.03  	조영욱        최초 생성
 * 2024.08.04  	조영욱        내 정보 조회 추가
 * 2024.08.05  	조영욱        내 메이트 리스트 조회 버튼 추가
 * 2024.08.06  	조영욱        로그아웃 아이콘 추가
 * </pre>
 */
class MyPageFragment : Fragment() {
    private val viewModel: MemberViewModel by activityViewModels()
    private var mBinding : MemberFragmentMyPageBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = MemberFragmentMyPageBinding.inflate(inflater, container, false)
        mBinding = binding
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 툴바 설정
        val toolbar = binding.toolbar?.toolbar
        toolbar?.let {
            val leftButton: ImageButton = toolbar.findViewById(R.id.left_button)
            val rightButton: Button = toolbar.findViewById(R.id.right_button)
            val title: TextView = toolbar.findViewById(R.id.toolbar_title)
            val logoutIcon: ImageView = toolbar.findViewById(R.id.logout)
            title.setText(R.string.mypage_title)

            // 버튼의 가시성 설정
            val showLeftButton = false
            val showRightButton = false
            val showLogoutIcon = true
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE
            logoutIcon.visibility = if (showLogoutIcon) View.VISIBLE else View.GONE

            logoutIcon.setOnClickListener {
                logout()
            }
        }

        // 서버 정보 받아서 프로필 업데이트
        updateProfile()

        binding.profileLayout.setOnClickListener {
            findNavController().navigate(R.id.action_myPageFragment_to_myProfileFragment)
        }

        binding.mateListLayout.setOnClickListener {
            findNavController().navigate(R.id.action_myPageFragment_to_myMatesFragment)
        }

    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    /**
     * 회원 로그아웃
     */
    private fun logout() {
        viewModel.fetchLogout(
            onSuccess = {response ->
                Toast.makeText(requireContext(), R.string.toast_member_logout_success, Toast.LENGTH_SHORT).show()
                activity?.let {
                    startActivity(Intent(it, AuthActivity::class.java))
                    it.finish()
                }
            },
            onError = {error ->
                Toast.makeText(requireContext(), R.string.toast_fail_server_connection, Toast.LENGTH_SHORT).show()
            }
        )
    }

    /**
     * 내 정보 조회
     */
    private fun updateProfile() {
        Log.i("마이페이지", "마이페이지 업데이트 프로필")

        viewModel.fetchGetMyProfile(
            onSuccess = { response ->
                // 매너바
                binding.mannerBar.progress = response.manner.toInt()
                binding.mannerText.setText("${response.manner}°C")

                // 닉네임
                binding.nicknameText.setText(response.nickname)

                // 이미지
                if (response.profileImgUrl != null) {
                    Glide.with(binding.profileImage.context)
                        .load(response.profileImgUrl)
                        .into(binding.profileImage)
                }
            },
            onError = { error ->
                Log.i("get My Profile failed", error)
            }
        )
    }
}