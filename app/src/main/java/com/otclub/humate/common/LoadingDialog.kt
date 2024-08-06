package com.otclub.humate.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.otclub.humate.R

class LoadingDialog(context: Context) : Dialog(context){

    init {
        setContentView(R.layout.dialog_loading)
        // 취소 불가능
        setCancelable(false)
        // 배경 투명
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    companion object {
        @Volatile
        private var INSTANCE: LoadingDialog? = null

        fun getInstance(context: Context): LoadingDialog {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LoadingDialog(context).also { INSTANCE = it }
            }
        }
    }
}