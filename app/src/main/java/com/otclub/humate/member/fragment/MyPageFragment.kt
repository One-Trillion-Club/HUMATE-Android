package com.otclub.humate.member.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.otclub.humate.auth.viewmodel.AuthViewModel
import com.otclub.humate.databinding.MemberFragmentMyPageBinding

class MyPageFragment : Fragment(){
    private val viewModel: AuthViewModel by activityViewModels()
    private var mBinding : MemberFragmentMyPageBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = MemberFragmentMyPageBinding.inflate(inflater, container, false)
        mBinding = binding
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logoutText.setOnClickListener {

        }
    }
}