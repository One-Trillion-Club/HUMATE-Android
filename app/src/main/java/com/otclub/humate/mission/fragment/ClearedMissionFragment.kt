package com.otclub.humate.mission.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.databinding.FragmentClearedMissionBinding
import com.otclub.humate.mission.adapter.ClearedMissionAdapter
import com.otclub.humate.mission.api.MissionService
import com.otclub.humate.mission.data.CommonResponseDTO
import com.otclub.humate.mission.data.MissionResponseDTO
import com.otclub.humate.mission.viewModel.MissionViewModel
import com.otclub.humate.retrofit.RetrofitConnection
import retrofit2.Call
import retrofit2.Response

class ClearedMissionFragment : Fragment() {

    private val missionViewModel: MissionViewModel by activityViewModels()
    private var mBinding : FragmentClearedMissionBinding? = null
    private var selectedButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = FragmentClearedMissionBinding.inflate(inflater, container, false)

        mBinding = binding

        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val companionId = arguments?.getInt("companionId")

        // RecyclerView 설정
        mBinding?.recyclerView?.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = ClearedMissionAdapter(emptyList()) {
                mission ->
                findNavController().navigate(
                    R.id.action_missionFragment_to_clearedMissionDetailsFragment,
                    Bundle().apply { putInt("companionActivityId", mission.companionActivityId)}
                )
            }
        }

        // ViewModel에서 데이터 관찰
        missionViewModel.missionResponseDTO.observe(viewLifecycleOwner) { response ->
            response?.let {
                val adapter = ClearedMissionAdapter(it.clearedMissionList) { mission ->
                    val bundle = Bundle().apply {
                        putInt("companionActivityId", mission.companionActivityId)
                    }
                    findNavController().navigate(
                        R.id.action_missionFragment_to_clearedMissionDetailsFragment,
                        bundle
                    )
                }
                Log.i("adapter : ", adapter.toString())
                mBinding?.recyclerView?.adapter = adapter
                updateEmptyTitle(it)
                setupToolbar(it.postTitle)
            }
        }

        // API 호출
        companionId?.let {
            missionViewModel.fetchMission(it)
        }

        // 버튼 클릭 리스너 설정
        mBinding?.completedMissionButton?.setOnClickListener {
            selectButton(mBinding?.completedMissionButton)
        }

        mBinding?.newMissionButton?.setOnClickListener {
            selectButton(mBinding?.newMissionButton)

        }

        selectButton(mBinding?.completedMissionButton)
    }


    private fun selectButton(button: Button?) {
        // 기존 선택된 버튼이 있다면 원래 상태로 되돌리기
        selectedButton?.let {
            it.isSelected = false // 선택 상태를 해제
            it.setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
            it.setTextColor(Color.BLACK)
        }

        // 새로 선택된 버튼의 스타일 적용
        button?.let {
            it.isSelected = true // 선택 상태를 해제
            it.setTypeface(Typeface.DEFAULT_BOLD) // 텍스트를 굵게
            it.setTextColor(Color.WHITE) // 텍스트 색깔을 흰색으로 설정 (예: Color.WHITE 또는 Color.parseColor("#FFFFFF"))
        }

        selectedButton = button

        // 버튼에 따라 Fragment 전환
        when (button?.id) {
            R.id.completedMissionButton -> {
                // 완료된 활동 버튼 클릭 시 처리
            }
            R.id.newMissionButton -> {
                val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                prefs.edit().putBoolean("navigate_to_new_mission", true).apply()
                // 새로운 활동 버튼 클릭 시 NewMissionFragment로 전환
                findNavController().navigate(R.id.action_missionFragment_to_newMissionFragment)
            }
        }
    }

    private fun updateEmptyTitle(response: MissionResponseDTO) {
        val clearedMissionList = response.clearedMissionList

        val isListEmpty = clearedMissionList.isEmpty()
        mBinding?.recyclerView?.visibility = if (isListEmpty) View.GONE else View.VISIBLE
        mBinding?.emptyMessage?.visibility = if (isListEmpty) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        mBinding = null
        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val navigateToNewMission = prefs.getBoolean("navigate_to_new_mission", false)

        // 플래그를 초기화하여 다음 호출에 영향을 주지 않도록 함
        prefs.edit().putBoolean("navigate_to_new_mission", false).apply()

        if (!navigateToNewMission) {
            (activity as? MainActivity)?.restoreToolbar()
        }

        super.onDestroyView()
    }

    private fun setupToolbar(postTitle: String) {
        val activity = activity as? MainActivity
        activity?.let {
            val mainToolbar = it.getToolbar()
            mainToolbar?.visibility = View.GONE
            val toolbar = LayoutInflater.from(context).inflate(R.layout.mission_toolbar, null) as Toolbar
            val leftButton: ImageButton = toolbar.findViewById(R.id.mission_left_button)
            val rightButton: ImageButton = toolbar.findViewById(R.id.mission_menu_button)
            val title: TextView = toolbar.findViewById(R.id.mission_toolbar_title)
            title.setText(postTitle)

            // 버튼의 가시성 설정
            val showLeftButton = true
            val showRightButton = true
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE
            // leftButton 클릭 시 이전 화면으로 돌아가기
            leftButton.setOnClickListener {
                findNavController().navigateUp()
            }

            rightButton.setOnClickListener {
                showMissionPopupMenu(rightButton)
            }

            it.replaceToolbar(toolbar)
        }
    }

    private fun showMissionPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.mission_menu, popupMenu.menu)
        popupMenu.menu.findItem(R.id.finish_companion).isVisible = (missionViewModel.isFinished == 0)
        popupMenu.menu.findItem(R.id.write_review).isVisible = (missionViewModel.isFinished == 1)
        popupMenu.setOnMenuItemClickListener { item ->

            when (item.itemId) {
                R.id.finish_companion -> {
                    val companionId = missionViewModel.lastCompanionId
                    companionId?.let { id ->
                        finishCompanion(id)
                    }
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun finishCompanion(companionId: Int) {
        val call = RetrofitConnection.getInstance().create(MissionService::class.java).finishCompanion(companionId)
        call.enqueue(object : retrofit2.Callback<CommonResponseDTO> {
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
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Upload failed: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            override fun onFailure(call: Call<CommonResponseDTO>, t: Throwable) {

            }


        })
    }
}