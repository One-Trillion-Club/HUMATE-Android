import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.otclub.humate.R
import com.otclub.humate.mission.data.NewMissionDetailsDTO
import com.otclub.humate.mission.viewModel.NewMissionDetailsViewModel

class NewMissionDetailsFragment : Fragment() {

    private val viewModel: NewMissionDetailsViewModel by activityViewModels()
    private var _binding: View? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflater.inflate(R.layout.fragment_new_mission_details, container, false)
        return binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activityId = arguments?.getInt("activityId") ?: return
        viewModel.fetchDetail(activityId)

        viewModel.newMissionDetailsDTO.observe(viewLifecycleOwner) { details ->
            details?.let {
                updateUI(it)
            }
        }

    }

    private fun updateUI(details: NewMissionDetailsDTO) {
        binding.findViewById<TextView>(R.id.missionTitle).text = details.title
        binding.findViewById<TextView>(R.id.missionContent).text = details.content
        binding.findViewById<TextView>(R.id.missionPoint).text = "${details.point} P"

        if (details.imgUrl.isNotEmpty()) {
            Glide.with(this)
                .load(details.imgUrl)
                .into(binding.findViewById<ImageView>(R.id.missionImage))
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}