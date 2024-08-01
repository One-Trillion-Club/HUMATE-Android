package com.otclub.humate.mission.fragment

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.otclub.humate.R
import com.otclub.humate.databinding.FragmentNewMissionBinding
import com.otclub.humate.mission.adapter.ClearedMissionAdapter
import com.otclub.humate.mission.adapter.NewMissionAdapter
import com.otclub.humate.mission.viewModel.MissionViewModel

class NewMissionFragment : Fragment() {
    private val missionViewModel: MissionViewModel by activityViewModels()
    private var mBinding: FragmentNewMissionBinding? = null
    private var selectedButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentNewMissionBinding.inflate(inflater, container, false)
        mBinding = binding
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 설정
        mBinding?.newMissionRecyclerView?.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = NewMissionAdapter(emptyList())
        }

        // ViewModel에서 데이터 관찰
        missionViewModel.missionResponseDTO.observe(viewLifecycleOwner) { response ->
            response?.let {
                val adapter = NewMissionAdapter(it.newMissionList)
                Log.i("adapter : ", adapter.toString())
                mBinding?.newMissionRecyclerView?.adapter = adapter
            }
        }

        // 버튼 클릭 리스너 설정
        mBinding?.completedMissionButton?.setOnClickListener {
            selectButton(mBinding?.completedMissionButton)
        }

        mBinding?.newMissionButton?.setOnClickListener {
            selectButton(mBinding?.newMissionButton)
        }

        selectButton(mBinding?.newMissionButton)
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
                // 완료된 활동 버튼 클릭 시 ClearedMissionFragment로 전환
                findNavController().navigate(R.id.action_newMissionFragment_to_missionFragment)
            }
            R.id.newMissionButton -> {
                // 새로운 활동 버튼 클릭 시 처리
            }
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}