package space_survivor.models

 import timber.log.Timber.i

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}
class ScoreMemStore : ScoreStore {
    private val scores = ArrayList<ScoreModel>()

    override fun findAll(): List<ScoreModel> {
        return scores
    }

    override fun create(score: ScoreModel) {
        score.id = getId()
        scores.add(score)
        logAll()
    }

    override fun update(score: ScoreModel) {
        var foundScore: ScoreModel? = scores.find { p -> p.id == score.id }
        if (foundScore != null) {
            foundScore.playerName = score.playerName
            foundScore.score = score.score
            logAll()
        }
    }

    private fun logAll() {
        scores.forEach { i("${it}") }
    }
}