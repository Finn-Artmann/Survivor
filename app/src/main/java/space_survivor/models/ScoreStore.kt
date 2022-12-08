package space_survivor.models

interface ScoreStore {
    fun findAll(): List<ScoreModel>
    fun findOne(score: ScoreModel): ScoreModel?
    fun create(score: ScoreModel)
    fun update(score: ScoreModel)
    fun delete(score: ScoreModel)
}