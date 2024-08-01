package com.otclub.humate.auth.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.otclub.humate.R
import com.otclub.humate.auth.data.LogInRequestDTO
import com.otclub.humate.auth.viewmodel.AuthViewModel
import com.otclub.humate.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()
    private var mBinding : FragmentSignInBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignInBinding.inflate(inflater, container, false)
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
        viewModel.fetchLogIn(
            LogInRequestDTO(loginId, password),
            onSuccess = {response ->
                Log.i("tes", "te")
                        Toast.makeText(requireContext(), "로그인 성공", Toast.LENGTH_SHORT).show()
            },
            onError = {error ->
                        Toast.makeText(requireContext(), "로그인 실패", Toast.LENGTH_SHORT).show()
            })
    }

}