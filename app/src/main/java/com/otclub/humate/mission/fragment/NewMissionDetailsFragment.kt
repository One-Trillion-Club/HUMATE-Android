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
import com.bumptech.glide.Glide
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.databinding.FragmentNewMissionDetailsBinding
import com.otclub.humate.mission.data.NewMissionDetailsDTO
import com.otclub.humate.mission.viewModel.MissionViewModel

/**
 * 새로운 활동 상세 Fragment
 * @author 손승완
 * @since 2024.08.02
 * @version 1.2
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.02 	손승완        최초 생성
 * 2024.08.04 	손승완        툴바 기능 추가
 * 2024.08.06 	손승완        활동 제목 및 내용 다국어 처리
 * </pre>
 */
class NewMissionDetailsFragment : Fragment() {

    private val viewModel: MissionViewModel by activityViewModels()
    private var mBinding: FragmentNewMissionDetailsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentNewMissionDetailsBinding.inflate(inflater, container, false)

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
            title.setText(getString(R.string.mission_new_title))

            // 버튼의 가시성 설정
            val showLeftButton = true
            val showRightButton = false
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE
        }

        val activityId = arguments?.getInt("activityId") ?: return
        viewModel.fetchNewMissionDetails(activityId)

        viewModel.newMissionDetailsDTO.observe(viewLifecycleOwner) { details ->
            details?.let {
                updateMission(it)
            }
        }

        (activity as? MainActivity)?.hideBottomNavigationBar()

        mBinding?.toolbar?.leftButton?.setOnClickListener {
            findNavController().navigateUp()
        }

        mBinding?.recordButton?.setOnClickListener {
            findNavController().navigate(R.id.action_newMissionDetailsFragment_to_missionUploadFragment)
        }
    }

    // 새로운 활동 상세 정보 업데이트 및 제목, 내용 다국어 처리
    private fun updateMission(details: NewMissionDetailsDTO) {
        mBinding?.apply {

            if (viewModel.sharedPreferencesManager.getLanguage() == 1) {
                missionTitle.text = details.titleKo
                missionContent.text = details.contentKo
            } else {
                missionTitle.text = details.titleEn
                missionContent.text = details.contentEn
            }

            missionPoint.text = "${details.point} P"

            if (details.imgUrl.isNotEmpty()) {
                Glide.with(this@NewMissionDetailsFragment)
                    .load(details.imgUrl)
                    .into(missionImage)
            }
        }
    }


    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}