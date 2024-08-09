import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.otclub.humate.R
import com.otclub.humate.mission.data.NewMissionDetailsDTO

/**
 * 새로운 활동 Adapter
 * @author 손승완
 * @since 2024.08.01
 * @version 1.1
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01 	손승완        최초 생성
 * 2024.08.06   손승완        다국어 처리
 * </pre>
 */
class NewMissionAdapter(
    private val newMissionList: List<NewMissionDetailsDTO>,
    private val languageCode: Int,
    private val onItemClick: (NewMissionDetailsDTO) -> Unit
) : RecyclerView.Adapter<NewMissionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mission_new_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mission = newMissionList[position]
        if (languageCode == 1) {
            holder.missionTitle.text = mission.titleKo
        } else {
            holder.missionTitle.text = mission.titleEn
        }


        if (mission.imgUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(mission.imgUrl)
                .into(holder.missionImage)
        }

        // 아이템 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            onItemClick(mission)
        }
    }

    override fun getItemCount(): Int = newMissionList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val missionImage: ImageView = itemView.findViewById(R.id.newMissionImage)
        val missionTitle: TextView = itemView.findViewById(R.id.newMissionTitle)
    }
}