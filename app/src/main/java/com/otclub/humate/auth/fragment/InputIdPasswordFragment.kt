package com.otclub.humate.auth.fragment

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
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
        val inputLoginId: String = binding.inputLoginId.text.toString()
        val guideCheckId: TextView = binding.guideCheckId

        if (inputLoginId.length < 5) {
            viewModel.signUpRequestDTO.loginId = null
            guideCheckId.setText("다섯 글자 이상의 아이디를 입력해주세요.")
            guideCheckId.setTextColor(ContextCompat.getColor(requireContext(), R.color.smooth_red))
            return
        }

        viewModel.fetchCheckLoginId(
            inputLoginId,
            onSuccess = {response ->
                viewModel.signUpRequestDTO.loginId = inputLoginId
                guideCheckId.setText("사용 가능한 아이디입니다.")
                guideCheckId.setTextColor(ContextCompat.getColor(requireContext(), R.color.smooth_blue))
            },
            onError = {error ->
                viewModel.signUpRequestDTO.loginId = null
                guideCheckId.setText("이미 사용중인 아이디입니다.")
                guideCheckId.setTextColor(ContextCompat.getColor(requireContext(), R.color.smooth_red))
            }
        )
    }

    private fun handleNextButtonClick() {

        if (checkPassword()) {
            if (viewModel.signUpRequestDTO.loginId == null) {
                Toast.makeText(requireContext(), "아이디 중복 확인이 필요합니다.", Toast.LENGTH_SHORT).show()
                return
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.authFragment, InputUserInfoFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun checkPassword(): Boolean {
        val inputPassword: String = binding.inputPassword.text.toString()
        val inputPasswordConfirm: String = binding.inputPasswordConfirm.text.toString()

        if (!inputPassword.equals(inputPasswordConfirm)) {
            Toast.makeText(requireContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (inputPassword.length < 8 || inputPassword.length > 20) {
            Toast.makeText(requireContext(), "8~20글자 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return false
        }


        val hasLetter = inputPassword.any { it.isLetter() }
        val hasDigit = inputPassword.any { it.isDigit() }
        val hasSpecialChar = inputPassword.any { !it.isLetterOrDigit() }

        val validCombination = listOf(hasLetter, hasDigit, hasSpecialChar).count { it } >= 2

        if (!validCombination) {
            Toast.makeText(requireContext(), "비밀번호는 영문자, 숫자, 특수문자 중 2가지 이상을 포함해야 합니다.", Toast.LENGTH_SHORT).show()
            return false
        }

        viewModel.signUpRequestDTO.password = inputPassword
        return true
    }
}