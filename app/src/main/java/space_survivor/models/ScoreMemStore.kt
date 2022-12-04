package space_survivor.models

class ScoreMemStore {
    val scores = ArrayList<ScoreModel>()

    fun findAll(): List<ScoreModel> {
        return scores
    }

    fun create(score: ScoreModel) {
        scores.add(score)
    }
}