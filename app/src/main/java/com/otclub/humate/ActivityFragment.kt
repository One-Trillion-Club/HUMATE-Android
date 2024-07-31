package com.otclub.humate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.otclub.humate.databinding.FragmentActivityBinding

class ActivityFragment : Fragment() {

    private var mBinding : FragmentActivityBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = FragmentActivityBinding.inflate(inflater, container, false)

        mBinding = binding

        return mBinding?.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}