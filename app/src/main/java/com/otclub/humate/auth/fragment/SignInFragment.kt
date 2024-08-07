package com.otclub.humate.auth.fragment

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.auth.data.LoginRequestDTO
import com.otclub.humate.auth.viewmodel.AuthViewModel
import com.otclub.humate.common.LoadingDialog
import com.otclub.humate.databinding.AuthFragmentSignInBinding

class SignInFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()
    private var mBinding : AuthFragmentSignInBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = AuthFragmentSignInBinding.inflate(inflater, container, false)
        mBinding = binding
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signInButton.setOnClickListener {
            logIn(binding.inputLoginId.text.toString(), binding.inputPassword.text.toString())
        }

        // Sign up 텍스트 클릭 시 SelectSignUpTypeFragment로 이동
        binding.signUpText.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.authFragment, SelectSignUpTypeFragment())
                .addToBackStack(null) // 백스택에 추가하여 뒤로가기가 가능하도록 설정
                .commit()
        }

    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun logIn(loginId: String, password: String) {
        Log.i("sign in btn click", "loginId:${loginId}, password:${password}")
        val loadingDialog = LoadingDialog(requireContext())
        loadingDialog.show()
        viewModel.fetchLogIn(
            LoginRequestDTO(loginId, password),
            onSuccess = {response ->
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), R.string.toast_signin_success, Toast.LENGTH_SHORT).show()
                activity?.let {
                    startActivity(Intent(it, MainActivity::class.java))
                    it.finish()
                }
            },
            onError = {error ->
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), R.string.toast_signup_invalid_id_password, Toast.LENGTH_SHORT).show()
            }
        )
    }
}