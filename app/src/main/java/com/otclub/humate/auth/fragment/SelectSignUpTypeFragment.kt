package com.otclub.humate.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.otclub.humate.R
import com.otclub.humate.auth.viewmodel.AuthViewModel
import com.otclub.humate.databinding.AuthFragmentSelectSignUpTypeBinding

class SelectSignUpTypeFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()
    private var mBinding : AuthFragmentSelectSignUpTypeBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = AuthFragmentSelectSignUpTypeBinding.inflate(inflater, container, false)
        mBinding = binding
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.selectKoreanFrame.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.authFragment, VerifyPhoneFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}