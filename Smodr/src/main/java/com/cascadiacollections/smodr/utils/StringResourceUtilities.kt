package com.cascadiacollections.smodr.utils

import android.content.Context
import androidx.annotation.StringRes

object StringResourceUtilities {
    @JvmStatic
    fun getString(context: Context?, @StringRes id: Int, vararg args: Any?): String {
        return context?.resources?.getString(id, *args) ?: ""
    }
}