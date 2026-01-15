package pl.edu.pwr.budgetbuddy.ui.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import pl.edu.pwr.budgetbuddy.data.TransactionType
import pl.edu.pwr.budgetbuddy.data.TransactionWithCategory
import pl.edu.pwr.budgetbuddy.ui.BudgetViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TransactionCard(
    item: TransactionWithCategory,
    navController: NavController,
    viewModel: BudgetViewModel = viewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    var showFullscreenImage by remember { mutableStateOf(false) } // Stan dla trybu pełnoekranowego

    val tx = item.transaction
    val cat = item.category

    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }

    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false }, title = { Text(tx.title) }, text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("Kwota: ${tx.amount} PLN")
                Text("Kategoria: ${cat.name}")
                Text("Data: ${dateFormat.format(tx.date)}")

                if (!tx.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Opis: ${tx.description}")
                }

                if (tx.receiptImagePath != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Paragon:", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    AsyncImage(
                        model = tx.receiptImagePath,
                        contentDescription = "Zdjęcie paragonu",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                showFullscreenImage = true
                            },
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        "(Kliknij, aby powiększyć)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
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

    if (showFullscreenImage && tx.receiptImagePath != null) {
        Dialog(
            onDismissRequest = { showFullscreenImage = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { showFullscreenImage = false }, contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = tx.receiptImagePath,
                    contentDescription = "Pełny ekran",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
            }
        }
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