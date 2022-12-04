package space_survivor.models

interface ScoreStore {
    fun findAll(): List<ScoreModel>
    fun create(score: ScoreModel)
}