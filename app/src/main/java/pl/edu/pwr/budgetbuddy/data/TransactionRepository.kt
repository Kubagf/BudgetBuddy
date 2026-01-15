package pl.edu.pwr.budgetbuddy.data

import kotlinx.coroutines.flow.Flow

class TransactionRepository(
    private val transactionDao: TransactionDao
) {
    fun getAllTransactions(): Flow<List<TransactionWithCategory>> {
        return transactionDao.getAllTransactions()
    }

    fun getExpenses(): Flow<List<TransactionWithCategory>> {
        return transactionDao.getTransactionsByType(TransactionType.EXPENSE)
    }

    fun getIncomes(): Flow<List<TransactionWithCategory>> {
        return transactionDao.getTransactionsByType(TransactionType.INCOME)
    }

    suspend fun getTransactionById(id: Int): Transaction? {
        return transactionDao.getTransactionById(id)
    }

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insert(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction)
    }
    
    suspend fun getTransactionCountByCategory(categoryId: Int): Int {
        return transactionDao.getCountByCategoryId(categoryId)
    }
}
