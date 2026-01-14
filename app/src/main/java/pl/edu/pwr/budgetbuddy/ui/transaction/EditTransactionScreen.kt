package pl.edu.pwr.budgetbuddy.ui.transaction

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import pl.edu.pwr.budgetbuddy.data.Transaction
import pl.edu.pwr.budgetbuddy.ui.BudgetViewModel

@Composable
fun EditTransactionScreen(
    navController: NavController, viewModel: BudgetViewModel, transactionId: Int
) {
    var transaction by remember { mutableStateOf<Transaction?>(null) }

    LaunchedEffect(transactionId) {
        transaction = viewModel.getTransactionById(transactionId)
    }

    if (transaction != null) {
        TransactionForm(
            viewModel = viewModel,
            initialTransaction = transaction,
            onClose = { navController.navigateUp() },
            onSave = { updatedTransaction ->
                viewModel.updateTransaction(updatedTransaction)
                navController.navigateUp()
            })
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}