package com.otclub.humate.auth.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.otclub.humate.MainActivity
import com.otclub.humate.auth.viewmodel.AuthViewModel
import com.otclub.humate.databinding.AuthFragmentInputProfileBinding
import com.otclub.humate.databinding.FragmentInputIdPasswordBinding

class InputProfileFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()
    private var mBinding : AuthFragmentInputProfileBinding? = null
    private val binding get() = mBinding!!

    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = AuthFragmentInputProfileBinding.inflate(inflater, container, false)
        mBinding = binding
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.checkNicknameButton.setOnClickListener {
            handleCheckNicknameButtonClick()
        }

        binding.profileImage.setOnClickListener {
            openImagePicker()
        }

        binding.finishButton.setOnClickListener {
            handleFisishButtonClick()
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                binding.profileImage.setImageURI(uri)
            }
        }
    }

    private fun handleCheckNicknameButtonClick() {
        // todo: 서버 로직 추가
        binding.guideCheckNickname.setText("사용 가능한 닉네임입니다.")
    }

    private fun handleFisishButtonClick() {
        // todo: sign up 로직 연동

        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}