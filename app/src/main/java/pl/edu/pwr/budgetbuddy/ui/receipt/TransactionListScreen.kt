package pl.edu.pwr.budgetbuddy.ui.receipt

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.pwr.budgetbuddy.ui.BudgetViewModel

@ExperimentalMaterial3Api
@Composable
fun TransactionListScreen(viewModel: BudgetViewModel) {
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = { TopAppBar(title = { Text("BudgetBuddy") }) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(
                items = transactions, key = { it.transaction.id }) { item ->
                val tx = item.transaction
                val cat = item.category

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text(tx.title) },
                        text = {
                            Column {
                                Text(tx.description ?: "")
                                Text(cat.name)
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                viewModel.deleteTransaction(tx)
                                showDialog = false
                            }

                            ) {
                                Text("Delete")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Close")
                            }
                        })
                }

                Card(modifier = Modifier.padding(8.dp), onClick = {
                    showDialog = true
                }) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth()
                    ) {
                        Row {
                            Text(tx.title, style = MaterialTheme.typography.titleMedium)
                            Text(tx.amount.toString(), style = MaterialTheme.typography.titleMedium)
                        }
                        if (!tx.description.isNullOrBlank()) {
                            Text(tx.description, style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(tx.type.name, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}