package pl.edu.pwr.budgetbuddy.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import pl.edu.pwr.budgetbuddy.data.AppDatabase
import pl.edu.pwr.budgetbuddy.data.Category
import pl.edu.pwr.budgetbuddy.data.CategoryRepository
import pl.edu.pwr.budgetbuddy.data.Transaction
import pl.edu.pwr.budgetbuddy.data.TransactionRepository
import pl.edu.pwr.budgetbuddy.data.TransactionType
import pl.edu.pwr.budgetbuddy.data.TransactionWithCategory
import java.util.Date

class BudgetViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

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

            if (transactionRepo.getCount() == 0) {
                val now = Date()
                val yesterday = Date(now.time - 24 * 60 * 60 * 1000)
                val twoDaysAgo = Date(now.time - 48 * 60 * 60 * 1000)

                transactionRepo.insertAllTransactions(
                    listOf(
                        Transaction(
                            title = "Zakupy w Biedronce",
                            amount = 156.50,
                            categoryId = 4,
                            description = "Tygodniowe zakupy spożywcze",
                            date = now,
                            type = TransactionType.EXPENSE
                        ), Transaction(
                            title = "Paliwo",
                            amount = 250.00,
                            categoryId = 3,
                            description = "Orlen - pełny bak",
                            date = now,
                            type = TransactionType.EXPENSE
                        ), Transaction(
                            title = "Kino",
                            amount = 80.00,
                            categoryId = 5,
                            description = "Wyjście na Diunę",
                            date = yesterday,
                            type = TransactionType.EXPENSE
                        ), Transaction(
                            title = "Wypłata",
                            amount = 5500.00,
                            categoryId = 6,
                            description = "Wynagrodzenie za styczeń",
                            date = twoDaysAgo,
                            type = TransactionType.INCOME
                        ), Transaction(
                            title = "Myszka komputerowa",
                            amount = 120.00,
                            categoryId = 1,
                            description = "Logitech",
                            date = twoDaysAgo,
                            type = TransactionType.EXPENSE
                        )
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

    fun addCategory(name: String, type: TransactionType) {
        viewModelScope.launch(Dispatchers.IO) {
            val newCategory = Category(name = name, type = type)
            categoryRepo.insert(newCategory)
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepo.update(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            val count = transactionRepo.getTransactionCountByCategory(category.id)
            if (count > 0) {
                _uiEvent.emit("Nie można usunąć: Kategoria ma $count transakcji!")
            } else {
                categoryRepo.delete(category)
                _uiEvent.emit("Kategoria usunięta.")
            }
        }
    }
}