package com.otclub.humate.mission.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.otclub.humate.R
import com.otclub.humate.databinding.FragmentMatchingBinding
import com.otclub.humate.mission.adapter.MatchingAdapter
import com.otclub.humate.mission.viewModel.MissionViewModel

class MatchingFragment : Fragment() {
    private val matchingViewModel: MissionViewModel by activityViewModels()
    private var mBinding: FragmentMatchingBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMatchingBinding.inflate(inflater, container, false)
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
            title.setText(getString(R.string.matching_title))

            // 버튼의 가시성 설정
            val showLeftButton = false
            val showRightButton = false
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE
        }

        // RecyclerView 설정
        mBinding?.matchingRecyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MatchingAdapter(emptyList()) { matching ->
                val bundle = Bundle().apply {
                    putInt("companionId", matching.companionId)
                    Log.i("companionId", matching.companionId.toString())
                }
                findNavController().navigate(
                    R.id.action_matchingFragment_to_missionFragment,
                    bundle
                )

            }
        }

        // ViewModel에서 데이터 관찰
        matchingViewModel.matchingResponseDTOList.observe(viewLifecycleOwner) { response ->
            response?.let {
                val adapter = MatchingAdapter(it) { matching ->
                    val bundle = Bundle().apply {
                        putInt("companionId", matching.companionId)
                    }
                    findNavController().navigate(
                        R.id.action_matchingFragment_to_missionFragment,
                        bundle
                    )
                }
                Log.i("adapter : ", adapter.toString())
                mBinding?.matchingRecyclerView?.adapter = adapter
            }
        }

        matchingViewModel.fetchMatching()

        mBinding?.toolbar?.leftButton?.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }


}