package pl.edu.pwr.budgetbuddy.data

import kotlinx.coroutines.flow.Flow

class CategoryRepository(
    private val categoryDao: CategoryDao
) {
    fun getByType(type: TransactionType): Flow<List<Category>> = categoryDao.getByType(type)

    suspend fun count(): Int = categoryDao.count()

    suspend fun insertAll(items: List<Category>) = categoryDao.insertAll(items)
}
