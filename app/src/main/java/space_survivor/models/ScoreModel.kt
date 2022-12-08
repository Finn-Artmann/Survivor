package space_survivor.models

import com.soywiz.klock.TimeSpan
import android.os.Parcelable
import com.soywiz.klock.seconds
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScoreModel(
    var id: String? = "0",
    var playerName: String? = "SpaceSurvivor",
    var score: Long? = 0.seconds.millisecondsLong
) : Parcelable {

    constructor() : this("0", "SpaceSurvivor", 0.seconds.millisecondsLong)
}
