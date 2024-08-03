package com.otclub.humate.mate.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.databinding.MateFragmentPostDetailBinding
import com.otclub.humate.mate.adapter.PostDetailAdapter
import com.otclub.humate.mate.viewmodel.PostDetailViewModel

class PostDetailFragment : Fragment() {

    private var mBinding : MateFragmentPostDetailBinding? = null
    private val binding get() = mBinding!!
    private val args: PostDetailFragmentArgs by navArgs()

    private lateinit var postDetailViewModel: PostDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = MateFragmentPostDetailBinding.inflate(inflater, container, false)

        // ViewModel 초기화
        postDetailViewModel = ViewModelProvider(requireActivity()).get(PostDetailViewModel::class.java)

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
            val showRightButton = false
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE

            // 액션 바의 타이틀을 설정하거나 액션 바의 다른 속성을 조정
            it.setToolbarTitle("매칭글 정보")
        }

        val postId = args.postId
        Log.i("postId", "$postId")
    }
}