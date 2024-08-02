package com.otclub.humate.mate.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.databinding.MateFragmentPostListFilterBinding


class PostListFilterFragment : Fragment() {

    private var mBinding : MateFragmentPostListFilterBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = MateFragmentPostListFilterBinding.inflate(inflater, container, false)

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
            it.setToolbarTitle("필터 설정")
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}