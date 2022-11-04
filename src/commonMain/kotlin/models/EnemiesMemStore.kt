package models

import views.*


class EnemiesMemStore : EnemiesStore{

    private var lastId = 0L
    private val enemies =  ArrayList<EnemyModel>()

    private fun getNewId(): Long {
        return lastId++
    }

    override fun create(enemy: EnemyModel) : Long{
        enemy.id = getNewId()
        enemy.view = EnemyView(enemy)
        enemies.add(enemy)
        return enemy.id
    }

    override fun update(enemy: EnemyModel) {
        TODO("Not yet implemented")
    }

    override fun delete(enemy: EnemyModel) {
        enemies.remove(enemy)
    }

    override fun findOne(id: Long): EnemyModel? {
        return enemies.find { e -> e.id == id }
    }

    override fun findAll(): List<EnemyModel> {
        return enemies
    }

}
