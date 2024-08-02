package com.otclub.humate.auth.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.otclub.humate.R
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
            // todo: 서버 로직 추가
            addAuthCodeInputField()
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun addAuthCodeInputField() {
        val authCodeInput: EditText = binding.inputCode

        authCodeInput.visibility = View.VISIBLE
        authCodeInput.layoutParams.height = (48 * resources.displayMetrics.density).toInt()
        authCodeInput.requestLayout()

        binding.sendAuthCode.text = "인증 완료"
        binding.sendAuthCode.setOnClickListener {
            sendAuthCode(authCodeInput.text.toString())
        }
    }

    private fun sendAuthCode(code: String) {

        if (code.isEmpty()) {
            Log.i("sendAuthCode", "비어있음")
            return
        }

        parentFragmentManager.beginTransaction()
                .replace(R.id.authFragment, InputIdPasswordFragment())
                .addToBackStack(null)
                .commit()
    }

}