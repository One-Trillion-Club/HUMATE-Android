package com.otclub.humate.mission.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.databinding.FragmentMissionBinding
import com.otclub.humate.mission.adapter.MissionAdapter
import com.otclub.humate.mission.viewModel.MissionViewModel

class MissionFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MissionAdapter
    private val missionViewModel: MissionViewModel by activityViewModels()
    private var mBinding : FragmentMissionBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = FragmentMissionBinding.inflate(inflater, container, false)

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

            // 액션 바의 타이틀을 설정하거나 액션 바의 다른 속성을 조정
            it.setToolbarTitle("활동")
        }

        // RecyclerView 설정
        recyclerView = mBinding?.recyclerView ?: return
        recyclerView.layoutManager = LinearLayoutManager(context)

        // ViewModel에서 데이터 관찰
        missionViewModel.missionResponse.observe(viewLifecycleOwner) { response ->
            response?.let {
                adapter = MissionAdapter(it.clearedMissionList)
                recyclerView.adapter = adapter
            }
        }

        // API 호출
        val companionId = "4"
        missionViewModel.fetchMission(companionId)
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}