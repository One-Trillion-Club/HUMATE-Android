package com.otclub.humate.member.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.common.LoadingDialog
import com.otclub.humate.databinding.MemberFragmentMyMatesBinding
import com.otclub.humate.mate.data.MateDetailResponseDTO
import com.otclub.humate.member.adapter.MateListAdapter
import com.otclub.humate.member.viewmodel.MemberViewModel

/**
 * 내 메이트 조회 Fragment
 * @author 조영욱
 * @since 2024.08.05
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.05  	조영욱        최초 생성
 * 2024.08.07  	조영욱        메이트 클릭 시 상세 정보 Dialog 띄우기 기능 추가
 * </pre>
 */
class MyMatesFragment: Fragment() {
    private val viewModel: MemberViewModel by activityViewModels()
    private var mBinding : MemberFragmentMyMatesBinding? = null
    private val binding get() = mBinding!!

    private lateinit var mateList: List<MateDetailResponseDTO>
    private lateinit var adapter: MateListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var mateNumber: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = MemberFragmentMyMatesBinding.inflate(inflater, container, false)
        recyclerView = binding.mateList
        recyclerView.layoutManager = LinearLayoutManager(context)
        mBinding = binding
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 내비게이션 바 숨김
        (activity as? MainActivity)?.hideBottomNavigationBar()

        // 툴바 설정
        val toolbar = binding.toolbar?.toolbar
        toolbar?.let {
            val leftButton: ImageButton = toolbar.findViewById(R.id.left_button)
            val rightButton: Button = toolbar.findViewById(R.id.right_button)
            val title: TextView = toolbar.findViewById(R.id.toolbar_title)
            mateNumber = toolbar.findViewById(R.id.additional_number)
            title.setText(R.string.member_mymate_title)

            // 버튼의 가시성 설정
            val showLeftButton = true
            val showRightButton = false
            val showMateNumber = true
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE
            mateNumber.visibility = if (showMateNumber) View.VISIBLE else View.GONE

            leftButton.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        getMyMateList()
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
        (activity as? MainActivity)?.showBottomNavigationBar()
    }

    /**
     * 내 메이트 목록 조회
     */
    private fun getMyMateList() {
        viewModel.fetchGetMyMateList(
            onSuccess = { mateList ->
                this.mateList = mateList
                mateNumber?.setText(mateList.size.toString())
                adapter = MateListAdapter(mateList, onMateClick = { memberId -> // 카드 뷰 클릭 시
                    // 카드 뷰 클릭 시 모달 창 띄우기
                    viewModel.getOtherMemberProfile(
                        memberId = memberId,
                        onSuccess = { profile ->
                            val loadingDialog = LoadingDialog(requireContext())
                            loadingDialog.showMateDetailPopup(profile)
                        },
                        onError = { error ->
                            Toast.makeText(context, R.string.toast_please_one_more_time, Toast.LENGTH_SHORT).show()
                        }
                    )
                })
                recyclerView.adapter = adapter
            },
            onError = { error ->
                Toast.makeText(context, R.string.toast_fail_server_connection, Toast.LENGTH_SHORT).show()
            }
        )
    }
}