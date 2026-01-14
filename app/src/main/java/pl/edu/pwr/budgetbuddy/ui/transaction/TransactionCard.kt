package pl.edu.pwr.budgetbuddy.ui.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pl.edu.pwr.budgetbuddy.data.TransactionType
import pl.edu.pwr.budgetbuddy.data.TransactionWithCategory
import pl.edu.pwr.budgetbuddy.ui.BudgetViewModel

@Composable
fun TransactionCard(
    item: TransactionWithCategory,
    navController: NavController,
    viewModel: BudgetViewModel = viewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    val tx = item.transaction
    val cat = item.category

    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false }, title = { Text(tx.title) }, text = {
            Column {
                Text("Kwota: ${tx.amount} PLN")
                Text("Kategoria: ${cat.name}")
                if (!tx.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Opis: ${tx.description}")
                }
            }
        }, confirmButton = {
            Button(onClick = {
                showDialog = false
                navController.navigate("edit/${tx.id}")
            }) {
                Text("Edytuj")
            }
        }, dismissButton = {
            Row {
                TextButton(onClick = { showDialog = false }) {
                    Text("Zamknij")
                }
                TextButton(
                    onClick = {
                        viewModel.deleteTransaction(tx)
                        showDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Usuń")
                }
            }
        })
    }

    Card(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp), onClick = {
        showDialog = true
    }) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
            ) {
                Text(tx.title, style = MaterialTheme.typography.titleMedium)

                val isExpense = tx.type == TransactionType.EXPENSE
                val color = if (isExpense) MaterialTheme.colorScheme.error else Color(0xFF2E7D32)
                val prefix = if (isExpense) "-" else "+"

                Text(
                    text = "$prefix${tx.amount} zł",
                    style = MaterialTheme.typography.titleMedium,
                    color = color
                )
            }
            if (!tx.description.isNullOrBlank()) {
                Text(tx.description, style = MaterialTheme.typography.bodyMedium)
            }
            Text(cat.name, style = MaterialTheme.typography.bodySmall)
        }
    }
}