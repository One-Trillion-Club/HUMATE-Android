package com.otclub.humate.auth.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.auth.data.LoginRequestDTO
import com.otclub.humate.auth.viewmodel.AuthViewModel
import com.otclub.humate.databinding.AuthFragmentInputProfileBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class InputProfileFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()
    private var mBinding : AuthFragmentInputProfileBinding? = null
    private val binding get() = mBinding!!

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = AuthFragmentInputProfileBinding.inflate(inflater, container, false)
        mBinding = binding

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    handleImageUri(uri)
                }
            }
        }

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
            handleFinishButtonClick()
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun handleImageUri(uri: Uri) {
        try {
            requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                val imageSizeInBytes = inputStream.available()
                val imageSizeInMB = imageSizeInBytes / (1024 * 1024).toFloat()

                if (imageSizeInMB > 1) {
                    Toast.makeText(requireContext(), "이미지 크기가 1MB를 초과합니다. 다른 이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
                    selectedImageUri = null
                    binding.profileImage.setImageURI(null) // 이미지 뷰 초기화
                } else {
                    selectedImageUri = uri
                    binding.profileImage.setImageURI(selectedImageUri)
                }
            }
        } catch (e: Exception) {
            Log.e("이미지 처리 에러", e.toString())
            Toast.makeText(requireContext(), "이미지 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleCheckNicknameButtonClick() {
        val inputNickname: String = binding.inputNickname.text.toString()
        val guideCheckNickname: TextView = binding.guideCheckNickname

        if (inputNickname.length < 2) {
            viewModel.signUpRequestDTO.nickname = null
            guideCheckNickname.setText("두 글자 이상의 닉네임을 입력해주세요.")
            guideCheckNickname.setTextColor(ContextCompat.getColor(requireContext(), R.color.smooth_red))
            return
        }

        viewModel.fetchCheckNickname(
            inputNickname,
            onSuccess = {response ->
                viewModel.signUpRequestDTO.nickname = inputNickname
                guideCheckNickname.setText("사용 가능한 닉네임입니다.")
                guideCheckNickname.setTextColor(ContextCompat.getColor(requireContext(), R.color.smooth_blue))
            },
            onError = {error ->
                viewModel.signUpRequestDTO.nickname = null
                guideCheckNickname.setText("이미 사용중인 닉네임입니다.")
                guideCheckNickname.setTextColor(ContextCompat.getColor(requireContext(), R.color.smooth_red))
            }
        )
    }

    private fun handleFinishButtonClick() {

        if (viewModel.signUpRequestDTO.nickname == null) {
            Toast.makeText(requireContext(), "닉네임 중복 확인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // 입력 안 했으면 ""
        viewModel.signUpRequestDTO.introduction = binding.inputIntroduction.text.toString()

        val imageFile = selectedImageUri?.let { uri ->
            try {
                requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                    val requestFile = inputStream.readBytes()

                    // MIME 타입 감지
                    val mimeType = requireContext().contentResolver.getType(uri) ?: "image/jpeg"
                    val mediaType = mimeType.toMediaTypeOrNull()
                    // MIME 타입에서 파일 확장자 추출
                    val fileExtension = mimeType.substringAfter('/').takeIf { it.isNotEmpty() } ?: "jpg"
                    val fileName = "${System.currentTimeMillis()}.$fileExtension"
                    val requestBody = requestFile.toRequestBody(mediaType)

                    MultipartBody.Part.createFormData(
                        "image",
                        fileName,
                        requestBody

                    )
                }
            } catch (e: Exception) {
                Log.e("이미지 처리 에러", e.toString())
                null
            }
        }

        Log.i("여기***", viewModel.signUpRequestDTO.toString())

        viewModel.signUp(viewModel.signUpRequestDTO, imageFile,
            onSuccess = { response ->
                Toast.makeText(requireContext(), "회원가입 성공.", Toast.LENGTH_SHORT).show()

                viewModel.fetchLogIn(
                    LoginRequestDTO(viewModel.signUpRequestDTO.loginId!!, viewModel.signUpRequestDTO.password!!),
                    onSuccess = {response ->
                        Toast.makeText(requireContext(), "로그인 성공", Toast.LENGTH_SHORT).show()
                        activity?.let {
                            startActivity(Intent(it, MainActivity::class.java))
                            it.finish()
                        }
                    },
                    onError = {error ->
                        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            onError = { error ->
                Log.e("회원가입 실패", error)
                Toast.makeText(requireContext(), "회원가입 실패. ${error}", Toast.LENGTH_SHORT).show()
            }
        )


    }
}