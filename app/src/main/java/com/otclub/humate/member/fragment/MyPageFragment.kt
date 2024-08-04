package com.otclub.humate.member.fragment

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
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.auth.activity.AuthActivity
import com.otclub.humate.auth.viewmodel.AuthViewModel
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