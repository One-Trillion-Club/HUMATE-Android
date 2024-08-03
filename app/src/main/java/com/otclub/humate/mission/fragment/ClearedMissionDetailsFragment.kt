package com.otclub.humate.mission.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import com.otclub.humate.R
import com.otclub.humate.mission.adapter.ImagePagerAdapter
import com.otclub.humate.mission.data.ClearedMissionDetailsDTO
import com.otclub.humate.mission.viewModel.MissionViewModel

class ClearedMissionDetailsFragment : Fragment() {
    private val viewModel: MissionViewModel by activityViewModels()
    private var mBinding: View? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = inflater.inflate(R.layout.fragment_cleared_mission_details, container, false)
        return binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val companionActivityId = arguments?.getInt("companionActivityId") ?: return
        viewModel.fetchClearedMissionDetails(companionActivityId)

        viewModel.clearedMissionDetailsDTO.observe(viewLifecycleOwner) { details ->
            details?.let {
                updateUI(it)
            }
        }
    }

    private fun updateUI(details: ClearedMissionDetailsDTO) {
        binding.findViewById<TextView>(R.id.clearedMissionTitle).text = details.activityTitle
        binding.findViewById<TextView>(R.id.clearedMissionCreatedAt).text = details.createdAt

        val viewPager = binding.findViewById<ViewPager>(R.id.viewPager)
        val adapter = ImagePagerAdapter(details.imgUrls)
        viewPager.adapter = adapter
    }
}