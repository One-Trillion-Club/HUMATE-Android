package com.otclub.humate.mission.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.databinding.FragmentMissionUploadBinding
import com.otclub.humate.mission.adapter.UploadMissionAdapter
import com.otclub.humate.mission.api.MissionService
import com.otclub.humate.mission.data.CommonResponseDTO
import com.otclub.humate.mission.viewModel.MissionViewModel
import com.otclub.humate.retrofit.RetrofitConnection
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MissionUploadFragment : Fragment() {
    private lateinit var missionService: MissionService
    private lateinit var imagesAdapter: UploadMissionAdapter
    private val imageFiles = ArrayList<Uri>()
    private val missionViewModel: MissionViewModel by activityViewModels()
    private var mBinding: FragmentMissionUploadBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentMissionUploadBinding.inflate(inflater, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = mBinding?.toolbar?.toolbar

        toolbar?.let {
            val leftButton: ImageButton = toolbar.findViewById(R.id.left_button)
            val rightButton: Button = toolbar.findViewById(R.id.right_button)
            val title: TextView = toolbar.findViewById(R.id.toolbar_title)
            title.setText(R.string.mission_upload_title)
            rightButton.setText(R.string.mission_record)
            rightButton.setTypeface(Typeface.DEFAULT_BOLD)

            // 버튼의 가시성 설정
            val showLeftButton = true
            val showRightButton = true
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE
        }

        mBinding?.toolbar?.leftButton?.setOnClickListener {
            findNavController().navigateUp()
        }

        mBinding?.toolbar?.rightButton?.setOnClickListener {
            uploadImages()
        }


        missionService = RetrofitConnection.getInstance().create(MissionService::class.java)

        imagesAdapter = UploadMissionAdapter(imageFiles, requireContext())
        binding.imagesRecyclerView.layoutManager = GridLayoutManager(context, 4)
        binding.imagesRecyclerView.adapter = imagesAdapter

        // RecyclerView 설정
        Log.i("imageFiles.size : ", imageFiles.size.toString())

        binding.selectImagesButton.setOnClickListener {
            var intent = Intent(Intent.ACTION_PICK)
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 200)
        }

        missionViewModel.newMissionDetailsDTO.observe(viewLifecycleOwner) { missionDetailsDTO ->
            missionDetailsDTO?.let {
                binding.uploadMissionTitle.text = it.title
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 200) {
            imageFiles.clear()

            if (data?.clipData != null) {
                val count = data.clipData!!.itemCount
                if (count > 10) {
                    return;
                }

                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    imageFiles.add(imageUri)
                }

            } else {
                data?.data?.let { uri ->
                    val imageUri : Uri? = data?.data
                    if (imageUri != null) {
                        imageFiles.add(uri)
                    }
                }
            }
        }

        // 어댑터에 데이터 변경 사항 알리기
        imagesAdapter.notifyDataSetChanged()
    }

    private fun uploadImages() {
        val companionId = missionViewModel.lastCompanionId
        val activityId = missionViewModel.lastActivityId

        val requestBody = """
            {
                "companionId": $companionId,
                "activityId": $activityId
            }
        """.trimIndent()
        val jsonRequestBody =
            requestBody.toRequestBody("application/json".toMediaTypeOrNull())
        val uploadActivityRequestDTO = MultipartBody.Part.createFormData(
            "uploadActivityRequestDTO",
            null,
            jsonRequestBody
        )

        val imageParts = imageFiles.mapNotNull { uri ->
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val requestFile = inputStream?.readBytes() // InputStream에서 바이트 배열로 읽기
            val requestBody = requestFile?.toRequestBody("image/jpeg".toMediaTypeOrNull())
            if (requestBody != null) {
                MultipartBody.Part.createFormData(
                    "images",
                    uri.lastPathSegment ?: "image.jpg",
                    requestBody
                )
            } else {
                null
            }
        }

        missionService.uploadActivity(uploadActivityRequestDTO, imageParts).enqueue(object :
            Callback<CommonResponseDTO> {
            override fun onResponse(
                call: Call<CommonResponseDTO>,
                response: Response<CommonResponseDTO>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Upload successful",
                        Toast.LENGTH_SHORT
                    ).show()
                    missionViewModel.lastCompanionId?.let { missionViewModel.fetchMission(it) }
                    findNavController().navigate(R.id.action_missionUploadFragment_to_missionFragment)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Upload failed: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<CommonResponseDTO>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "Upload error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
