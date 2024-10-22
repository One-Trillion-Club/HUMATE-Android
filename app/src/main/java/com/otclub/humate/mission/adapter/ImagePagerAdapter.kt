package com.otclub.humate.mission.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.otclub.humate.R

/**
 * 완료된 활동 사진 Pager Adapter
 * @author 손승완
 * @since 2024.08.02
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.02 	손승완        최초 생성
 * </pre>
 */
class ImagePagerAdapter(private val imgUrls: List<String>) : PagerAdapter() {

    override fun getCount(): Int = imgUrls.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val view = inflater.inflate(R.layout.mission_image_pager, container, false)
        val imageView = view.findViewById<ImageView>(R.id.imageView)

        Glide.with(container.context)
            .load(imgUrls[position])
            .into(imageView)

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}