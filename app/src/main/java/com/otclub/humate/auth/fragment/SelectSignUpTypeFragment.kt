package com.otclub.humate.auth.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.otclub.humate.R
import com.otclub.humate.auth.viewmodel.AuthViewModel
import com.otclub.humate.databinding.AuthFragmentSelectSignUpTypeBinding
import com.otclub.humate.sharedpreferences.SharedPreferencesManager

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
            viewModel.signUpRequestDTO.nationality = 1

            val sharedPreferencesManager = SharedPreferencesManager(requireContext())
            sharedPreferencesManager.setLanguage(1)

            Log.i("한국인박스 클릭", sharedPreferencesManager.getLanguage().toString())

            parentFragmentManager.beginTransaction()
                .replace(R.id.authFragment, VerifyPhoneFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.selectForeignerFrame.setOnClickListener {
            viewModel.signUpRequestDTO.nationality = 2

            val sharedPreferencesManager = SharedPreferencesManager(requireContext())
            sharedPreferencesManager.setLanguage(2)

            Log.i("외국인박스 클릭", sharedPreferencesManager.getLanguage().toString())

            parentFragmentManager.beginTransaction()
                .replace(R.id.authFragment, VerifyPassportFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}