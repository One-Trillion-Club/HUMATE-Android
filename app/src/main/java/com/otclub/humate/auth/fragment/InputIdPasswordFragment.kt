package com.otclub.humate.auth.fragment

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
import com.otclub.humate.sharedpreferences.SharedPreferencesManager

/**
 * 회원가입 시 로그인 아이디, 비밀번호 입력 Fragment
 * @author 조영욱
 * @since 2024.08.02
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.02  	조영욱        최초 생성
 * </pre>
 */
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

    /**
     * 로그인 아이디 중복확인 버튼 클릭 시
     */
    private fun handleCheckIdButtonClick() {
        val inputLoginId: String = binding.inputLoginId.text.toString()
        val guideCheckId: TextView = binding.guideCheckId

        if (inputLoginId.length < 5) {
            viewModel.signUpRequestDTO.loginId = null
            guideCheckId.setText(R.string.signup_id_five_letter)
            guideCheckId.setTextColor(ContextCompat.getColor(requireContext(), R.color.smooth_red))
            return
        }

        viewModel.fetchCheckLoginId(
            inputLoginId,
            onSuccess = {response ->
                viewModel.signUpRequestDTO.loginId = inputLoginId
                guideCheckId.setText(R.string.signup_available_id)
                guideCheckId.setTextColor(ContextCompat.getColor(requireContext(), R.color.smooth_blue))
            },
            onError = {error ->
                viewModel.signUpRequestDTO.loginId = null
                guideCheckId.setText(R.string.signup_id_already_in_use)
                guideCheckId.setTextColor(ContextCompat.getColor(requireContext(), R.color.smooth_red))
            }
        )
    }

    /**
     * 다음 버튼 클릭 시
     */
    private fun handleNextButtonClick() {

        if (checkPassword()) {
            if (viewModel.signUpRequestDTO.loginId == null) {
                Toast.makeText(requireContext(), R.string.signup_id_duplication_check_needed, Toast.LENGTH_SHORT).show()
                return
            }

            val sharedPreferencesManager = SharedPreferencesManager(requireContext())
            val language = sharedPreferencesManager.getLanguage()
            if (language == 1) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.authFragment, InputUserInfoFragment())
                    .addToBackStack(null)
                    .commit()
            } else {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.authFragment, ForeignerInputUserInfoFragment())
                    .addToBackStack(null)
                    .commit()
            }

        }
    }

    /**
     * 비밀번호 입력 값 유효성 검증
     */
    private fun checkPassword(): Boolean {
        val inputPassword: String = binding.inputPassword.text.toString()
        val inputPasswordConfirm: String = binding.inputPasswordConfirm.text.toString()

        if (!inputPassword.equals(inputPasswordConfirm)) {
            Toast.makeText(requireContext(), R.string.signup_password_not_match, Toast.LENGTH_SHORT).show()
            return false
        }

        if (inputPassword.length < 8 || inputPassword.length > 20) {
            Toast.makeText(requireContext(), R.string.signup_password_letter_size, Toast.LENGTH_SHORT).show()
            return false
        }


        val hasLetter = inputPassword.any { it.isLetter() }
        val hasDigit = inputPassword.any { it.isDigit() }
        val hasSpecialChar = inputPassword.any { !it.isLetterOrDigit() }

        val validCombination = listOf(hasLetter, hasDigit, hasSpecialChar).count { it } >= 2

        if (!validCombination) {
            Toast.makeText(requireContext(), R.string.signup_password_constraints, Toast.LENGTH_SHORT).show()
            return false
        }

        viewModel.signUpRequestDTO.password = inputPassword
        return true
    }
}