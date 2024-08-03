package com.otclub.humate.auth.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.otclub.humate.R
import com.otclub.humate.auth.viewmodel.AuthViewModel
import com.otclub.humate.databinding.AuthFragmentInputUserInfoBinding

class InputUserInfoFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()
    private var mBinding : AuthFragmentInputUserInfoBinding? = null
    private val binding get() = mBinding!!

    private var selectedGenderButton: View? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = AuthFragmentInputUserInfoBinding.inflate(inflater, container, false)
        mBinding = binding
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.maleButton.setOnClickListener {
            handleGenderButtonClick(binding.maleButton)
        }

        binding.femaleButton.setOnClickListener {
            handleGenderButtonClick(binding.femaleButton)
        }

        binding.englishButton.setOnClickListener {
            handleLanguageButtonClick(binding.englishButton)
        }

        binding.japaneseButton.setOnClickListener {
            handleLanguageButtonClick(binding.japaneseButton)
        }

        binding.chineseButton.setOnClickListener {
            handleLanguageButtonClick(binding.chineseButton)
        }

        binding.nextButton.setOnClickListener {
            handleNextButtonClick()
        }

    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun handleNextButtonClick() {
        if (selectedGenderButton == null) {
            Toast.makeText(requireContext(), "성별을 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        val year = binding.inputYear.text.toString()
        val month = binding.inputMonth.text.toString()
        val day = binding.inputDay.text.toString()
        if (year.isEmpty() || year.toInt() > 2024 || year.toInt() < 1900 ||
            month.isEmpty() || month.toInt() > 12 || month.toInt() < 1 ||
            day.isEmpty() || day.toInt() > 31 || day.toInt() < 1) {
            Toast.makeText(requireContext(), "생년월일을 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        var language: String = "1"
        if (binding.englishButton.isSelected) {
            language += ",2"
        }
        if (binding.japaneseButton.isSelected) {
            language += ",3"
        }
        if (binding.chineseButton.isSelected) {
            language += ",4"
        }

        if (selectedGenderButton == binding.maleButton) {
            viewModel.signUpRequestDTO.gender = "m"
        } else if (selectedGenderButton == binding.femaleButton) {
            viewModel.signUpRequestDTO.gender = "f"
        }
        viewModel.signUpRequestDTO.birthdate = "${year}-${month}-${day}"
        viewModel.signUpRequestDTO.language = language

        parentFragmentManager.beginTransaction()
            .replace(R.id.authFragment, InputProfileFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun handleGenderButtonClick(button: View) {
        if (selectedGenderButton == button) {
            resetButtonStyle(button)
            selectedGenderButton = null
        } else {
            selectedGenderButton?.let { resetButtonStyle(it) }
            updateButtonStyle(button)
            selectedGenderButton = button
        }
    }

    private fun handleLanguageButtonClick(button: View) {
        if (button.isSelected) {
            resetButtonStyle(button)
            button.isSelected = false
        } else {
            updateButtonStyle(button)
            button.isSelected = true
        }
    }

    private fun updateButtonStyle(button: View) {
        button.apply {
            (this as? android.widget.Button)?.apply {
                setTextColor(Color.WHITE)
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 15f)
                setBackgroundResource(R.drawable.auth_main_large_button)
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            }
        }
    }

    private fun resetButtonStyle(button: View) {
        button.apply {
            (this as? android.widget.Button)?.apply {
                setTextColor(ContextCompat.getColor(context, R.color.light_gray))
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14f)
                setBackgroundResource(R.drawable.auth_light_gray_border)
                setTypeface(typeface, android.graphics.Typeface.NORMAL)
            }
        }
    }
}