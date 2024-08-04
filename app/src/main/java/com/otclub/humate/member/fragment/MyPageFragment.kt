package com.otclub.humate.member.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
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
        val toolbar = binding.toolbar?.toolbar

        toolbar?.let {
            val leftButton: ImageButton = toolbar.findViewById(R.id.left_button)
            val rightButton: Button = toolbar.findViewById(R.id.right_button)
            val title: TextView = toolbar.findViewById(R.id.toolbar_title)
            title.setText("마이페이지")

            // 버튼의 가시성 설정
            val showLeftButton = false
            val showRightButton = false
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE
        }


        // 서버 정보 받아서 프로필 업데이트
        updateProfile()

        binding.logoutText.setOnClickListener{
            logout()
        }

        binding.profileLayout.setOnClickListener {
            findNavController().navigate(R.id.action_myPageFragment_to_myProfileFragment)
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.my_nav_host, MyProfileFragment())
//                .addToBackStack(null)
//                .commit()
        }

    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun logout() {
        Log.i("마이페이지: 로그아웃", "로그아웃")
        viewModel.fetchLogout(
            onSuccess = {response ->
                Toast.makeText(requireContext(), "로그아웃 성공", Toast.LENGTH_SHORT).show()
                activity?.let {
                    startActivity(Intent(it, AuthActivity::class.java))
                    it.finish()
                }
            },
            onError = {error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun updateProfile() {
        Log.i("마이페이지", "마이페이지 업데이트프로필")

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
                        .placeholder(R.drawable.ic_member_profile_default)
                        .into(binding.profileImage)
                }
            },
            onError = { error ->
                Log.i("겟마이프로필실패", error)
            }
        )
    }
}