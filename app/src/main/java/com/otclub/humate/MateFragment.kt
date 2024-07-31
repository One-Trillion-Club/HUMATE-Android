package com.otclub.humate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.otclub.humate.databinding.FragmentMateBinding

class MateFragment : Fragment() {

    private var mBinding : FragmentMateBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = FragmentMateBinding.inflate(inflater, container, false)

        mBinding = binding

        return mBinding?.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}