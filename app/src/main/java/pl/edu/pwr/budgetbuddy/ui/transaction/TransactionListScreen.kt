package pl.edu.pwr.budgetbuddy.ui.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.edu.pwr.budgetbuddy.ui.BudgetViewModel

@ExperimentalMaterial3Api
@Composable
fun TransactionListScreen(
    viewModel: BudgetViewModel, navController: NavController
) {
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = { TopAppBar(title = { Text("Lista transakcji") }) }) { padding ->
        if (transactions.isEmpty()) {
            Column(
                Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Brak transakcji.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = "Dodaj coś! ^_^",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        } else LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(
                items = transactions, key = { it.transaction.id }) { item ->
                TransactionCard(
                    item = item, navController = navController, viewModel = viewModel
                )
            }
        }
    }
}