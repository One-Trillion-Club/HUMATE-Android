package com.otclub.humate.auth.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.otclub.humate.R
import com.otclub.humate.auth.data.GeneratePhoneCodeRequestDTO
import com.otclub.humate.auth.data.VerifyPhoneCodeRequestDTO
import com.otclub.humate.auth.viewmodel.AuthViewModel
import com.otclub.humate.databinding.AuthFragmentVerifyPhoneBinding

class VerifyPhoneFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()
    private var mBinding : AuthFragmentVerifyPhoneBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = AuthFragmentVerifyPhoneBinding.inflate(inflater, container, false)
        mBinding = binding
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sendAuthCode.setOnClickListener {
            this.handleSendAuthCodeClick()
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun handleSendAuthCodeClick() {
        val phone: String = binding.inputPhone.text.toString()
        if (phone.length < 11) {
            Toast.makeText(requireContext(), "올바른 휴대폰 번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.fetchGeneratePhoneCode(
            GeneratePhoneCodeRequestDTO(phone),
            onSuccess = {response ->
                addAuthCodeInputField(phone)
            },
            onError = {error ->
                Log.i("폰 인증 페이지", error)
                Toast.makeText(requireContext(), "이미 가입된 번호입니다.", Toast.LENGTH_SHORT).show()
            })

    }

    private fun addAuthCodeInputField(phone: String) {
        val authCodeInput: EditText = binding.inputCode

        authCodeInput.visibility = View.VISIBLE
        authCodeInput.layoutParams.height = (48 * resources.displayMetrics.density).toInt()
        authCodeInput.requestLayout()

        binding.sendAuthCode.text = "인증 완료"
        binding.sendAuthCode.setOnClickListener {
            sendPhoneVerifyCode(phone)
        }
    }

    private fun sendPhoneVerifyCode(phone: String) {
        val code = binding.inputCode.text.toString()
        if (code.length < 6) {
            Toast.makeText(requireContext(), "6자리 코드를 인증번호를 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.fetchVerifyPhoneCode(
            VerifyPhoneCodeRequestDTO(phone, code),
            onSuccess = {response ->
                Toast.makeText(requireContext(), "인증에 성공하였습니다", Toast.LENGTH_SHORT).show()
                viewModel.signUpRequestDTO.phone = phone
                viewModel.signUpRequestDTO.verifyCode = response.message
                parentFragmentManager.beginTransaction()
                    .replace(R.id.authFragment, InputIdPasswordFragment())
                    .addToBackStack(null)
                    .commit()
            },
            onError = {error ->
                Toast.makeText(requireContext(), "인증번호가 올바르지 않습니다", Toast.LENGTH_SHORT).show()
            })


    }

}