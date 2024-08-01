package com.otclub.humate.mission.fragment

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.databinding.FragmentClearedMissionBinding
import com.otclub.humate.mission.adapter.ClearedMissionAdapter
import com.otclub.humate.mission.viewModel.MissionViewModel

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

        val activity = activity as? MainActivity
        activity?.let {
            val toolbar = it.getToolbar() // MainActivity의 Toolbar를 가져옴
            val leftButton: ImageButton = toolbar.findViewById(R.id.left_button)
            val rightButton: Button = toolbar.findViewById(R.id.right_button)

            // 버튼의 가시성 설정
            val showLeftButton = true
            val showRightButton = true
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE

        }

        // RecyclerView 설정
        mBinding?.recyclerView?.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = ClearedMissionAdapter(emptyList())
        }

        // ViewModel에서 데이터 관찰
        missionViewModel.missionResponseDTO.observe(viewLifecycleOwner) { response ->
            response?.let {
                val adapter = ClearedMissionAdapter(it.clearedMissionList)
                Log.i("adapter : ", adapter.toString())
                mBinding?.recyclerView?.adapter = adapter

                // Toolbar 타이틀 설정
                val activity = activity as? MainActivity
                activity?.let { mainActivity ->
                    val toolbar = mainActivity.getToolbar()
                    mainActivity.setToolbarTitle(it.postTitle)
                }
            }
        }

        // API 호출
        val companionId = "4"
        missionViewModel.fetchMission(companionId)

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
                // 새로운 활동 버튼 클릭 시 NewMissionFragment로 전환
                findNavController().navigate(R.id.action_missionFragment_to_newMissionFragment)
            }
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}