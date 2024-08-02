package com.otclub.humate.mission.fragment

import NewMissionAdapter
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
import com.otclub.humate.databinding.FragmentNewMissionBinding
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

        val activity = activity as? MainActivity
        activity?.let {
            val toolbar = it.getToolbar() // MainActivity의 Toolbar를 가져옴
            val leftButton: ImageButton = toolbar.findViewById(R.id.left_button)
            val rightButton: Button = toolbar.findViewById(R.id.right_button)
            it.setToolbarTitle("새로운 활동 목록")

            // 버튼의 가시성 설정
            val showLeftButton = true
            val showRightButton = false
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE
            // leftButton 클릭 시 이전 화면으로 돌아가기
            leftButton.setOnClickListener {
                findNavController().navigateUp()
            }

        }

        // RecyclerView 설정
        mBinding?.newMissionRecyclerView?.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = NewMissionAdapter(emptyList()) { mission ->
                // 아이템 클릭 시 DetailFragment로 이동
                findNavController().navigate(
                    R.id.action_newMissionFragment_to_newMissionDetailsFragment,
                    Bundle().apply { putInt("activityId", mission.activityId) }
                )
            }
        }

        // ViewModel에서 데이터 관찰
        missionViewModel.missionResponseDTO.observe(viewLifecycleOwner) { response ->
            response?.let {
                val adapter = NewMissionAdapter(it.newMissionList) { mission ->
                    val bundle = Bundle().apply {
                        putInt("activityId", mission.activityId)
                    }
                    findNavController().navigate(R.id.action_newMissionFragment_to_newMissionDetailsFragment, bundle)
                }
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