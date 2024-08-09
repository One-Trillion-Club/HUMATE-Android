package com.otclub.humate.mission.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.databinding.MissionClearedDetailsFragmentBinding
import com.otclub.humate.mission.adapter.ImagePagerAdapter
import com.otclub.humate.mission.data.ClearedMissionDetailsDTO
import com.otclub.humate.mission.viewModel.MissionViewModel


/**
 * 완료된 활동 상세 Fragment
 * @author 손승완
 * @since 2024.08.02
 * @version 1.1
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.02 	손승완        최초 생성
 * 2024.08.04 	손승완        상단 toolbar 추가
 * 2024.08.06   손승완        다국어 처리
 * </pre>
 */
class ClearedMissionDetailsFragment : Fragment() {
    private val viewModel: MissionViewModel by activityViewModels()
    private var mBinding: MissionClearedDetailsFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = MissionClearedDetailsFragmentBinding.inflate(inflater, container, false)
        mBinding = binding
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.hideBottomNavigationBar()
        val toolbar = mBinding?.toolbar?.toolbar

        toolbar?.let {
            val leftButton: ImageButton = toolbar.findViewById(R.id.left_button)
            val rightButton: Button = toolbar.findViewById(R.id.right_button)
            val title: TextView = toolbar.findViewById(R.id.toolbar_title)
            title.setText(getString(R.string.mission_completed_title))

            // 버튼의 가시성 설정
            val showLeftButton = true
            val showRightButton = false
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE
        }

        val companionActivityId = arguments?.getInt("companionActivityId") ?: return
        viewModel.fetchClearedMissionDetails(companionActivityId)

        viewModel.clearedMissionDetailsDTO.observe(viewLifecycleOwner) { details ->
            details?.let {
                updateMission(it)
            }
        }

        mBinding?.toolbar?.leftButton?.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    /**
     * 업데이트된 활동 정보 적용 및 활동 제목 다국어 처리
     */
    private fun updateMission(details: ClearedMissionDetailsDTO) {
        mBinding?.apply {
            if (viewModel.sharedPreferencesManager.getLanguage() == 1) {
                clearedMissionTitle.text = details.activityTitleKo
            } else {
                clearedMissionTitle.text = details.activityTitleEn
            }

            clearedMissionCreatedAt.text = details.createdAt

            // Set up the ViewPager with the adapter
            val adapter = ImagePagerAdapter(details.imgUrls)
            viewPager.adapter = adapter
            dotsIndicator.setViewPager(viewPager)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.showBottomNavigationBar()
    }
}