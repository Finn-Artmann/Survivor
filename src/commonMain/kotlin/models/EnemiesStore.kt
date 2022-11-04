package models


interface EnemiesStore {
    fun create(enemy: EnemyModel) : Long
    fun update(enemy: EnemyModel)
    fun delete(enemy: EnemyModel)
    fun findOne(id: Long): EnemyModel?
    fun findAll(): List<EnemyModel>
}
