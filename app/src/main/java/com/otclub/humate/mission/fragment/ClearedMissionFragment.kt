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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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
        (activity as? MainActivity)?.hideBottomNavigationBar()
        val companionId = arguments?.getInt("companionId")
        mBinding?.tabLayout?.addTab(mBinding!!.tabLayout.newTab().setText(getString(R.string.mission_finished)))
        mBinding?.tabLayout?.addTab(mBinding!!.tabLayout.newTab().setText(getString(R.string.mission_new)))

        mBinding?.tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.text.toString().equals(getString(R.string.mission_new))) {
                    findNavController().navigate(R.id.action_missionFragment_to_newMissionFragment)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })


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

    }

    private fun updateEmptyTitle(response: MissionResponseDTO) {
        val clearedMissionList = response.clearedMissionList

        val isListEmpty = clearedMissionList.isEmpty()
        mBinding?.recyclerView?.visibility = if (isListEmpty) View.GONE else View.VISIBLE
        mBinding?.emptyMessage?.visibility = if (isListEmpty) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun setupToolbar(postTitle: String) {
        val toolbar = mBinding?.toolbar?.missionToolbar
        toolbar?.let {
            val leftButton: ImageButton = it.findViewById(R.id.mission_left_button)
            val rightButton: ImageButton = it.findViewById(R.id.mission_menu_button)
            val title: TextView = it.findViewById(R.id.mission_toolbar_title)
            title.text = postTitle

            // 버튼의 가시성 설정
            val showLeftButton = true
            val showRightButton = true
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE

            // leftButton 클릭 시 이전 화면으로 돌아가기
            leftButton.setOnClickListener {
                findNavController().navigate(R.id.action_missionFragment_to_matchingFragment)
            }

            rightButton.setOnClickListener {
                showMissionPopupMenu(rightButton)
            }
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
                    findNavController().navigate(R.id.action_missionFragment_to_reviewFragment)
                    true
                }

                R.id.write_review -> {
                    findNavController().navigate(R.id.action_missionFragment_to_reviewFragment)
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
                        getString(R.string.companion_finished_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    missionViewModel.lastCompanionId?.let { missionViewModel.fetchMission(it) }
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.companion_finished_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            override fun onFailure(call: Call<CommonResponseDTO>, t: Throwable) {
            }
        })
    }
}