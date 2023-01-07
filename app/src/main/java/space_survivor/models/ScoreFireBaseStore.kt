package space_survivor.models

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.soywiz.klock.TimeSpan
import timber.log.Timber.i


class ScoreFireBaseStore : ScoreStore {
    private val database = Firebase.database
    private val scoresRef = database.getReference("scores")
    private var scores = ArrayList<ScoreModel>()

    init {
        scoresRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                scores.clear()
                dataSnapshot.children.forEach { child ->
                    val score = child.getValue<ScoreModel>()
                    if (score != null) {
                        scores.add(score)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                i("Failed to read value. ${error.toException()}" )
            }
        })
    }

    override fun findAll(): List<ScoreModel> {
        return scores
    }

    override fun findOne(score: ScoreModel): ScoreModel? {
        return scores.find { p -> p.id == score.id }
    }

    override fun create(score: ScoreModel) {
        // Add the new score as a new child node under the "scores" node
        val newScoreRef = scoresRef.push()
        score.id = newScoreRef.key
        newScoreRef.setValue(score)
        logAll()
    }
    override fun update(score: ScoreModel) {
        // Update the values for the given score object in the database
        scoresRef.child(score.id.toString()).updateChildren(mapOf(
            "playerName" to score.playerName,
            "score" to score.score,
            "dateAndTime" to score.dateAndTime
        ))
    }

    override fun delete(score: ScoreModel) {
        // Remove the score from the database
        scoresRef.child(score.id.toString()).removeValue()
    }

    private fun logAll() {
        scoresRef.get().addOnSuccessListener {
            it.children.forEach { i("${it.getValue<ScoreModel>(ScoreModel::class.java)}") }
        }
    }

}