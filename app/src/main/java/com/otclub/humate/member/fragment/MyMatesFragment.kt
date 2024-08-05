package com.otclub.humate.member.fragment

//import com.otclub.humate.member.adapter.MateListAdapter
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.otclub.humate.R
import com.otclub.humate.databinding.MemberFragmentMyMatesBinding
import com.otclub.humate.mate.data.MateDetailResponseDTO
import com.otclub.humate.member.adapter.MateListAdapter
import com.otclub.humate.member.data.ProfileResponseDTO
import com.otclub.humate.member.viewmodel.MemberViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

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
        val toolbar = binding.toolbar?.toolbar

        toolbar?.let {
            val leftButton: ImageButton = toolbar.findViewById(R.id.left_button)
            val rightButton: Button = toolbar.findViewById(R.id.right_button)
            val title: TextView = toolbar.findViewById(R.id.toolbar_title)
            mateNumber = toolbar.findViewById(R.id.additional_number)
            title.setText("내 메이트")

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

        viewModel.fetchGetMyMateList(
            onSuccess = { mateList ->
                this.mateList = mateList
                mateNumber?.setText(mateList.size.toString())
                Log.i("lwaejflkwejfc", mateNumber.toString())
                adapter = MateListAdapter(mateList, onMateClick = { memberId -> // 카드 뷰 클릭 시
                    // 카드 뷰 클릭 시 모달 창 띄우기
                    viewModel.getOtherMemberProfile(
                        memberId = memberId,
                        onSuccess = { profile ->
                            showMateDetailPopup(profile)
                        },
                        onError = { error ->
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        }
                    )
                })
                recyclerView.adapter = adapter
//                    MateListAdapter.sumbitList(mateList)
                Log.i("dd", mateList.toString())
            },
            onError = { error ->
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun showMateDetailPopup(profile: ProfileResponseDTO) {
        // 팝업 창
        val dialogView = LayoutInflater.from(context).inflate(R.layout.member_dialog_mate_detail, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setView(dialogView)

        val nicknameTextView: TextView = dialogView.findViewById(R.id.nickname)
        val mannerTextView: TextView = dialogView.findViewById(R.id.mannerText)
        val profileImageView: ImageView = dialogView.findViewById(R.id.profileImage)
        val genderTextView: TextView = dialogView.findViewById(R.id.genderText)
        val ageTextView: TextView = dialogView.findViewById(R.id.ageText)
        val closeButton: ImageButton = dialogView.findViewById(R.id.close_button)
        val progressBar: ProgressBar = dialogView.findViewById(R.id.mannerBar)

        nicknameTextView.text = profile.nickname
        mannerTextView.text = "${profile.manner}°C"
        genderTextView.text = if (profile.gender == "m") "남자" else "여자"
        ageTextView.text = "${calculateAge(profile.birthdate)}세"
        progressBar.progress = profile.manner.toInt()

        Glide.with(this)
            .load(profile.profileImgUrl)
            .into(profileImageView)

        val dialog = dialogBuilder.create()

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        closeButton.setOnClickListener {
            dialog.dismiss()
        }


        dialog.setCancelable(true)
        dialog.show()
    }

    fun calculateAge(birthdate: String): Int {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val birthDate = LocalDate.parse(birthdate, formatter)
        val currentDate = LocalDate.now()

        // 만 나이 계산
        var age = ChronoUnit.YEARS.between(birthDate, currentDate)
        val birthdayThisYear = birthDate.plusYears(age)

        // 생일이 지나지 않았을 경우
        if (birthdayThisYear.isAfter(currentDate)) {
            age -= 1
        }

        return age.toInt()
    }
}