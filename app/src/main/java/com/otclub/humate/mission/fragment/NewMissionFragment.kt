package com.otclub.humate.mission.fragment

import NewMissionAdapter
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.tabs.TabLayout
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.databinding.MissionNewFragmentBinding
import com.otclub.humate.mission.api.MissionService
import com.otclub.humate.mission.data.CommonResponseDTO
import com.otclub.humate.mission.viewModel.MissionViewModel
import com.otclub.humate.retrofit.RetrofitConnection
import retrofit2.Call
import retrofit2.Response

/**
 * 새로운 활동 목록 Fragment
 * @author 손승완
 * @since 2024.08.01
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01 	손승완        최초 생성
 * 2024.08.04 	손승완        toolbar 기능 추가
 * 2024.08.05 	손승완        TabLayout 적용
 * </pre>
 */
class NewMissionFragment : Fragment() {
    private val missionViewModel: MissionViewModel by activityViewModels()
    private var mBinding: MissionNewFragmentBinding? = null
    private var selectedButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = MissionNewFragmentBinding.inflate(inflater, container, false)
        mBinding = binding
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.hideBottomNavigationBar()
        mBinding?.tabLayout?.addTab(mBinding!!.tabLayout.newTab().setText(getString(R.string.mission_finished)))
        mBinding?.tabLayout?.addTab(mBinding!!.tabLayout.newTab().setText(getString(R.string.mission_new)))
        mBinding?.tabLayout?.getTabAt(1)?.select()

        mBinding?.tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.text.toString().equals(getString(R.string.mission_finished))) {
                    findNavController().navigate(R.id.action_newMissionFragment_to_missionFragment)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })


        // RecyclerView 설정
        mBinding?.newMissionRecyclerView?.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = NewMissionAdapter(emptyList(), missionViewModel.sharedPreferencesManager.getLanguage()) { mission ->
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
                val adapter = NewMissionAdapter(it.newMissionList, missionViewModel.sharedPreferencesManager.getLanguage()) { mission ->
                    val bundle = Bundle().apply {
                        putInt("activityId", mission.activityId)
                    }
                    findNavController().navigate(R.id.action_newMissionFragment_to_newMissionDetailsFragment, bundle)
                }
                Log.i("adapter : ", adapter.toString())
                mBinding?.newMissionRecyclerView?.adapter = adapter
                setupToolbar(it.postTitle)
            }
        }
    }

    /**
     * 상단 Toolbar 설정
     */
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
                findNavController().navigate(R.id.action_newMissionFragment_to_matchingFragment)
            }

            rightButton.setOnClickListener {
                showMissionPopupMenu(rightButton)
            }
        }
    }

    /**
     * 동행 종료 및 후기 남기기 팝업 활성화
     */
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
                    findNavController().navigate(R.id.action_newMissionFragment_to_reviewFragment)

                    true
                }

                R.id.write_review -> {
                    findNavController().navigate(R.id.action_newMissionFragment_to_reviewFragment)
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    /**
     * 동행 종료하기
     */
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


    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}