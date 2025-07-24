package com.nutrizulia.util

import android.annotation.SuppressLint
import android.os.Parcel
import com.google.android.material.datepicker.CalendarConstraints

@SuppressLint("ParcelCreator")
class UnavailableDatesValidator(private val unavailableDates: List<Long>) : CalendarConstraints.DateValidator {

    override fun isValid(date: Long): Boolean {
        return date !in unavailableDates
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLongArray(unavailableDates.toLongArray())
    }

    override fun describeContents(): Int {
        return 0
    }
}