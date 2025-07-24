package com.nutrizulia.data.local.pojo

import androidx.room.ColumnInfo
import java.time.LocalDate

data class DailyAppointmentCount(
    @ColumnInfo(name = "date") val date: LocalDate,
    @ColumnInfo(name = "count") val count: Int
)