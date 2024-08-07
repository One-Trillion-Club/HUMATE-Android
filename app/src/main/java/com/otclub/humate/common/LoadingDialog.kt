package com.otclub.humate.common

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.otclub.humate.R
import com.otclub.humate.member.data.ProfileResponseDTO
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class LoadingDialog(context: Context) : Dialog(context){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)
        // 취소 불가능
        setCancelable(false)
        // 배경 투명
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun showMateDetailPopup(profile: ProfileResponseDTO) {
        // 팝업 창
        val dialogView = LayoutInflater.from(context).inflate(R.layout.member_dialog_mate_detail, null)
        val dialogBuilder = AlertDialog.Builder(context)
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
        genderTextView.text = if (profile.gender == "m")
            context.getString(R.string.member_mymate_gender_male)
        else context.getString(R.string.member_mymate_gender_female)
        ageTextView.text = "${calculateAge(profile.birthdate)}${context.getString(R.string.member_mymate_postage)}"
        progressBar.progress = profile.manner.toInt()

        Glide.with(context)
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