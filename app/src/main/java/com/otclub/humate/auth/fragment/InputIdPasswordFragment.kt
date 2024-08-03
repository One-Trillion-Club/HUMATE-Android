package com.otclub.humate.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.otclub.humate.R
import com.otclub.humate.auth.viewmodel.AuthViewModel
import com.otclub.humate.databinding.FragmentInputIdPasswordBinding

class InputIdPasswordFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()
    private var mBinding : FragmentInputIdPasswordBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentInputIdPasswordBinding.inflate(inflater, container, false)
        mBinding = binding
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.checkIdButton.setOnClickListener {
            // todo: 서버 로직 추가
            handleCheckIdButtonClick()
        }

        binding.nextButton.setOnClickListener {
            handleNextButtonClick()
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun handleCheckIdButtonClick() {
        binding.guideCheckId.setText("사용 가능한 아이디입니다.")
    }

    private fun handleNextButtonClick() {
        // todo: 값 다 들어왔는지
        parentFragmentManager.beginTransaction()
            .replace(R.id.authFragment, InputUserInfoFragment())
            .addToBackStack(null)
            .commit()
    }
}