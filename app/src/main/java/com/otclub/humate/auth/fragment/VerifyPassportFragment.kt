package com.otclub.humate.auth.fragment

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.otclub.humate.auth.data.VerifyPassportRequestDTO
import com.otclub.humate.auth.viewmodel.AuthViewModel
import com.otclub.humate.databinding.AuthFragmentVerifyPassportBinding

class VerifyPassportFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()
    private var mBinding : AuthFragmentVerifyPassportBinding? = null
    private val binding get() = mBinding!!
    private var selectedCountry = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = AuthFragmentVerifyPassportBinding.inflate(inflater, container, false)
        mBinding = binding
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinner: Spinner = binding.selectSpinner
        val countries = arrayOf("select country", "United States", "Japan", "China", "South Korea")
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, countries)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position == 0) {
                    binding.selectedType.text = "select country"
                    selectedCountry = 0
                } else {
                    binding.selectedType.text = countries[position]
                    selectedCountry = position
                    viewModel.signUpRequestDTO.nationality = position + 1
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                binding.selectedType.text = "select country"
            }
        }


        binding.nextButton.setOnClickListener {
            this.handleNextButtonClick()
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun handleNextButtonClick() {
        val year = binding.inputYear.text.toString()
        val month = binding.inputMonth.text.toString()
        val day = binding.inputDay.text.toString()
        // 생일 선택이 올바르지 않을 시
        if (year.isEmpty() || year.toInt() > 2024 || year.toInt() < 1900 ||
            month.isEmpty() || month.toInt() > 12 || month.toInt() < 1 ||
            day.isEmpty() || day.toInt() > 31 || day.toInt() < 1) {
            Toast.makeText(requireContext(), "Incorrect birthdate", Toast.LENGTH_SHORT).show()
            return
        }
        var nationality : String
        var country : String
        when (selectedCountry) {
            0 -> {
                // 국가 선택 안 했을 시
                Toast.makeText(requireContext(), "Select Country", Toast.LENGTH_SHORT).show()
                return
            }
            1 -> {
                // 미국
                nationality = "99"
                country = "미국"
            }
            2 -> {
                // 중국
                nationality = "7"
                country = "중국"
            }
            3 -> {
                // 일본
                nationality = "6"
                country = "일본"
            }
            4 -> {
                // 한국
                nationality = "99"
                country = "한국"
            }
            else -> {
                // 국가 비정상 선택
                Toast.makeText(requireContext(), "Select Country", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val inputPassportNo = binding.inputPassport.text.toString()

        val verifyPassportRequestDTO = VerifyPassportRequestDTO(
            year+month.padStart(2, '0')+day.padStart(2, '0'),
            nationality,
            country,
            inputPassportNo
        )
        viewModel.fetchVerifyPassport(
            verifyPassportRequestDTO,
            onSuccess = { response ->
                Toast.makeText(requireContext(), "Successfully Authenticated", Toast.LENGTH_SHORT).show()
                viewModel.signUpRequestDTO.passportNo = inputPassportNo
                viewModel.signUpRequestDTO.verifyCode = response.message
                viewModel.signUpRequestDTO.birthdate = "${year}-${month}-${day}"
                parentFragmentManager.beginTransaction()
                    .replace(com.otclub.humate.R.id.authFragment, InputIdPasswordFragment())
                    .addToBackStack(null)
                    .commit()
            },
            onError = { error ->
                Toast.makeText(requireContext(), "Incorrect Passport Info", Toast.LENGTH_SHORT).show()
            })

    }
}