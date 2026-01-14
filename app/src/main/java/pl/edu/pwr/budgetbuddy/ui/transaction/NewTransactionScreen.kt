package pl.edu.pwr.budgetbuddy.ui.transaction

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import pl.edu.pwr.budgetbuddy.ui.BudgetViewModel

@Composable
fun NewTransactionScreen(
    navController: NavController,
    viewModel: BudgetViewModel
) {
    TransactionForm(
        viewModel = viewModel,
        initialTransaction = null, // Brak transakcji = tryb dodawania
        onClose = { navController.navigateUp() },
        onSave = { newTransaction ->
            viewModel.addTransaction(newTransaction)
            navController.navigateUp()
        }
    )
}