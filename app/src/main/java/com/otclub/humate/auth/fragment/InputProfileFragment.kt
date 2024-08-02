package com.otclub.humate.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.otclub.humate.auth.viewmodel.AuthViewModel
import com.otclub.humate.databinding.AuthFragmentInputProfileBinding
import com.otclub.humate.databinding.FragmentInputIdPasswordBinding

class InputProfileFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()
    private var mBinding : AuthFragmentInputProfileBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = AuthFragmentInputProfileBinding.inflate(inflater, container, false)
        mBinding = binding
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.checkIdButton.setOnClickListener {
//            // todo: 서버 로직 추가
//            handleCheckIdButtonClick()
//        }
//
//        binding.nextButton.setOnClickListener {
//            handleNextButtonClick()
//        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}