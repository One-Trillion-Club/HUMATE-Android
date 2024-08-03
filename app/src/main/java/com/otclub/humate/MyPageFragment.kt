package com.otclub.humate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.otclub.humate.auth.activity.AuthActivity
import com.otclub.humate.databinding.MemberFragmentMyPageBinding
import com.otclub.humate.member.viewmodel.MemberViewModel


class MyPageFragment : Fragment() {
    private val viewModel:MemberViewModel by activityViewModels()
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

        val activity = activity as? MainActivity
        activity?.let {
            val toolbar = it.getToolbar() // MainActivity의 Toolbar를 가져옴
            val leftButton: ImageButton = toolbar.findViewById(R.id.left_button)
            val rightButton: Button = toolbar.findViewById(R.id.right_button)

            // 버튼의 가시성 설정
            val showLeftButton = false
            val showRightButton = false
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE

            // 액션 바의 타이틀을 설정하거나 액션 바의 다른 속성을 조정
            it.setToolbarTitle("마이페이지")
        }

        binding.logoutText.setOnClickListener{
            logout()
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
}