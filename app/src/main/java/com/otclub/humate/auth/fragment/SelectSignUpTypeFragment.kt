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

/**
 * 회원가입 시 한국인/외국인 타입 선택 Fragment
 * @author 조영욱
 * @since 2024.08.01
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01  	조영욱        최초 생성
 * 2024.08.06   조영욱        외국인 박스 클릭 이벤트 추가
 * 2024.08.08   조영욱        프레임 클릭 이벤트 메서드로 분리
 * </pre>
 */
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
            handleKoreanFrameClick()
        }

        binding.selectForeignerFrame.setOnClickListener {
            handleForeignerClick()
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    /**
     * 한국인으로 회원가입 프레임 선택 클릭 시
     */
    private fun handleKoreanFrameClick() {
        viewModel.signUpRequestDTO.nationality = 1

        val sharedPreferencesManager = SharedPreferencesManager(requireContext())
        sharedPreferencesManager.setLanguage(1)

        Log.i("한국인박스 클릭", sharedPreferencesManager.getLanguage().toString())

        parentFragmentManager.beginTransaction()
            .replace(R.id.authFragment, VerifyPhoneFragment())
            .addToBackStack(null)
            .commit()
    }

    /**
     * 외국인으로 회원가입 프레임 선택 클릭 시
     */
    private fun handleForeignerClick() {
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