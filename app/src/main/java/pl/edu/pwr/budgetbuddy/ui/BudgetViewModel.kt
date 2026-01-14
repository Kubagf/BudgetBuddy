package pl.edu.pwr.budgetbuddy.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pl.edu.pwr.budgetbuddy.data.AppDatabase
import pl.edu.pwr.budgetbuddy.data.Category
import pl.edu.pwr.budgetbuddy.data.CategoryRepository
import pl.edu.pwr.budgetbuddy.data.Transaction
import pl.edu.pwr.budgetbuddy.data.TransactionRepository
import pl.edu.pwr.budgetbuddy.data.TransactionType
import pl.edu.pwr.budgetbuddy.data.TransactionWithCategory

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)

    private val transactionRepo = TransactionRepository(db.transactionDao())
    private val categoryRepo = CategoryRepository(db.categoryDao())

    val transactions: Flow<List<TransactionWithCategory>> = transactionRepo.getAllTransactions()

    val expenseCategories: Flow<List<Category>> = categoryRepo.getByType(TransactionType.EXPENSE)

    val incomeCategories: Flow<List<Category>> = categoryRepo.getByType(TransactionType.INCOME)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (categoryRepo.count() == 0) {
                categoryRepo.insertAll(
                    listOf(
                        Category(name = "Elektronika", type = TransactionType.EXPENSE),
                        Category(name = "Dom", type = TransactionType.EXPENSE),
                        Category(name = "Samochód", type = TransactionType.EXPENSE),
                        Category(name = "Spożywcze", type = TransactionType.EXPENSE),
                        Category(name = "Inne", type = TransactionType.EXPENSE),
                        Category(name = "Wypłata", type = TransactionType.INCOME),
                        Category(name = "Inne", type = TransactionType.INCOME),
                    )
                )
            }
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepo.insertTransaction(transaction)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepo.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepo.deleteTransaction(transaction)
        }
    }

    suspend fun getTransactionById(id: Int): Transaction? {
        return transactionRepo.getTransactionById(id)
    }
}