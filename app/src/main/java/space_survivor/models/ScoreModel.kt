package space_survivor.models

import com.soywiz.klock.TimeSpan
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScoreModel(var id: Long = 0, var playerName: String = "SpaceSurvivor", var score: TimeSpan) : Parcelable
