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
import com.otclub.humate.R
import com.otclub.humate.databinding.FragmentClearedMissionDetailsBinding
import com.otclub.humate.mission.adapter.ImagePagerAdapter
import com.otclub.humate.mission.data.ClearedMissionDetailsDTO
import com.otclub.humate.mission.viewModel.MissionViewModel


class ClearedMissionDetailsFragment : Fragment() {
    private val viewModel: MissionViewModel by activityViewModels()
    private var mBinding: FragmentClearedMissionDetailsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentClearedMissionDetailsBinding.inflate(inflater, container, false)
        mBinding = binding
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                updateUI(it)
            }
        }

        mBinding?.toolbar?.leftButton?.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun updateUI(details: ClearedMissionDetailsDTO) {
        mBinding?.apply {
            // Set the title and creation date
            clearedMissionTitle.text = details.activityTitle
            clearedMissionCreatedAt.text = details.createdAt

            // Set up the ViewPager with the adapter
            val adapter = ImagePagerAdapter(details.imgUrls)
            viewPager.adapter = adapter
            dotsIndicator.setViewPager(viewPager)
        }
    }
}