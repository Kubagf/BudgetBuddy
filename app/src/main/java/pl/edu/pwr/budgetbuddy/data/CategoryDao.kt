package pl.edu.pwr.budgetbuddy.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY name ASC")
    fun getByType(type: TransactionType): Flow<List<Category>>

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<Category>)

    @Insert
    suspend fun insert(item: Category)

    @Query("DELETE FROM categories")
    suspend fun deleteAll()

    @Update
    suspend fun update(item: Category)

    @Delete
    suspend fun delete(item: Category)
}
