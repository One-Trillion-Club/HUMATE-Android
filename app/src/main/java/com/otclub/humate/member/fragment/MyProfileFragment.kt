package com.otclub.humate.member.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.auth.viewmodel.AuthViewModel
import com.otclub.humate.databinding.MemberFragmentMyProfileBinding
import com.otclub.humate.member.data.ModifyProfileRequestDTO
import com.otclub.humate.member.viewmodel.MemberViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class MyProfileFragment : Fragment() {
    private val viewModel: MemberViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private var mBinding : MemberFragmentMyProfileBinding? = null
    private val binding get() = mBinding!!

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null

    private lateinit var modifyProfileRequestDTO: ModifyProfileRequestDTO

    private lateinit var originNickname: String
    private lateinit var originIntroduction: String
    private var originProfileImgUrl: String? = null
    private var isRequestAvailable = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = MemberFragmentMyProfileBinding.inflate(inflater, container, false)
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
        (activity as? MainActivity)?.hideBottomNavigationBar()
        val toolbar = binding.toolbar?.toolbar
        originNickname = viewModel.profileResponseDTO.value!!.nickname
        originIntroduction = viewModel.profileResponseDTO.value!!.introduction?:""
        originProfileImgUrl = viewModel.profileResponseDTO.value!!.profileImgUrl

        modifyProfileRequestDTO = ModifyProfileRequestDTO(null, originIntroduction)

        binding.inputNickname.setText(originNickname)
        binding.inputIntroduction.setText(originIntroduction)
        if (originProfileImgUrl != null) {
            Glide.with(binding.profileImage.context)
                .load(originProfileImgUrl)
                .into(binding.profileImage)
        }

        toolbar?.let {
            val leftButton: ImageButton = toolbar.findViewById(R.id.left_button)
            val rightButton: Button = toolbar.findViewById(R.id.right_button)
            val title: TextView = toolbar.findViewById(R.id.toolbar_title)
            title.setText(R.string.mypage_profile_title)

            // 버튼의 가시성 설정
            val showLeftButton = true
            val showRightButton = false
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE

            leftButton.setOnClickListener {
                findNavController().navigateUp()
            }
        }

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
        (activity as? MainActivity)?.showBottomNavigationBar()
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

                if (imageSizeInMB > 50) {
                    Toast.makeText(requireContext(), R.string.toast_image_size_over, Toast.LENGTH_SHORT).show()
                    selectedImageUri = null
                    return
                } else {
                    selectedImageUri = uri
                    binding.profileImage.setImageURI(selectedImageUri)
                }
            }
        } catch (e: Exception) {
            Log.e("이미지 처리 에러", e.toString())
            Toast.makeText(requireContext(), R.string.toast_please_one_more_time, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleCheckNicknameButtonClick() {
        val inputNickname: String = binding.inputNickname.text.toString()
        val guideCheckNickname: TextView = binding.guideCheckNickname

        if (originNickname == inputNickname) {
            guideCheckNickname.setText(R.string.mypage_profile_different_nickname)
            guideCheckNickname.setTextColor(ContextCompat.getColor(requireContext(), R.color.smooth_red))
            modifyProfileRequestDTO.nickname = null
            isRequestAvailable = false
            return
        }

        if (inputNickname.length < 2) {
            guideCheckNickname.setText(R.string.mypage_profile_two_letter_nickname)
            guideCheckNickname.setTextColor(ContextCompat.getColor(requireContext(), R.color.smooth_red))
            modifyProfileRequestDTO.nickname = null
            isRequestAvailable = false
            return
        }

        authViewModel.fetchCheckNickname(
            inputNickname,
            onSuccess = {response ->
                guideCheckNickname.setText(R.string.mypage_profile_available_nickname)
                guideCheckNickname.setTextColor(ContextCompat.getColor(requireContext(), R.color.smooth_blue))
                modifyProfileRequestDTO.nickname = inputNickname
                isRequestAvailable = true
            },
            onError = {error ->
                guideCheckNickname.setText(R.string.mypage_profile_already_in_use_nickname)
                guideCheckNickname.setTextColor(ContextCompat.getColor(requireContext(), R.color.smooth_red))
                modifyProfileRequestDTO.nickname = null
                isRequestAvailable = false
            }
        )
    }

    private fun handleFinishButtonClick() {
        if (!isRequestAvailable) {
            Toast.makeText(requireContext(), R.string.mypage_profile_duplication_check_needed, Toast.LENGTH_SHORT).show()
            return
        }

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

        modifyProfileRequestDTO.introduction = binding.inputIntroduction.text.toString()

        Log.i("회원 수정 여기***", modifyProfileRequestDTO.toString())

        viewModel.fetchModifyProfile(modifyProfileRequestDTO, imageFile,
            onSuccess = { response ->
                Toast.makeText(requireContext(), R.string.toast_mypage_profile_edit_success, Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            },
            onError = { error ->
                Log.e("회원 정보 수정 실패", error)
                Toast.makeText(requireContext(), R.string.toast_mypage_profile_edit_fail, Toast.LENGTH_SHORT).show()
            }
        )
    }
}