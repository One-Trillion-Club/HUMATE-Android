package com.otclub.humate.auth.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.marginTop
import androidx.fragment.app.activityViewModels
import com.otclub.humate.R
import com.otclub.humate.auth.viewmodel.AuthViewModel
import com.otclub.humate.databinding.FragmentSignInBinding
import com.otclub.humate.databinding.FragmentVerifyPhoneBinding

class VerifyPhoneFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()
    private var mBinding : FragmentVerifyPhoneBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentVerifyPhoneBinding.inflate(inflater, container, false)
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
        val authCodeInput = EditText(requireContext()).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                width = binding.inputPhone.width
                height = binding.inputPhone.height
            }
            hint = "인증번호 6자리"
            textSize = 14f
            setHintTextColor(resources.getColor(R.color.light_gray, null))
            setPadding(17, 0, 0, 0)
            setBackgroundResource(R.drawable.light_gray_border)
        }

        val constraintLayout = binding.root as ConstraintLayout
        constraintLayout.addView(authCodeInput)

        val set = ConstraintSet()
        set.clone(constraintLayout)

        set.connect(authCodeInput.id, ConstraintSet.TOP, binding.inputPhone.id, ConstraintSet.BOTTOM, 16)
        set.connect(authCodeInput.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        set.connect(authCodeInput.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        set.connect(binding.sendAuthCode.id, ConstraintSet.TOP, authCodeInput.id, ConstraintSet.BOTTOM, 30)

        set.applyTo(constraintLayout)

        var sendAuthCodeButton: Button = binding.sendAuthCode
        sendAuthCodeButton.text = "인증 완료"

        sendAuthCodeButton.setOnClickListener {
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