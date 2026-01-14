package pl.edu.pwr.budgetbuddy.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.edu.pwr.budgetbuddy.data.TransactionType
import pl.edu.pwr.budgetbuddy.ui.BudgetViewModel
import pl.edu.pwr.budgetbuddy.ui.transaction.TransactionCard
import java.time.LocalDate
import java.time.ZoneId

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    viewModel: BudgetViewModel,
    navController: NavController
) {
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())
    val zone = ZoneId.systemDefault()
    val today = LocalDate.now()

    val todayTransactions = transactions.filter { tx ->
        tx.transaction.date.toInstant().atZone(zone).toLocalDate() == today
    }

    val todayAmount = todayTransactions.sumOf { tx ->
        if (tx.transaction.type == TransactionType.EXPENSE) {
            -tx.transaction.amount
        } else {
            tx.transaction.amount
        }
    }

    Scaffold(contentWindowInsets = WindowInsets(0.dp), topBar = {
        TopAppBar(title = {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BudgetBuddy",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
            }
        })
    }) { it ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
        ) {
            Text("Cześć!", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            Card {
                Row(
                    Modifier
                        .padding(top = 16.dp, bottom = 16.dp, start = 8.dp, end = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val balanceColor = when {
                        todayAmount > 0 -> Color(0xFF2E7D32)
                        todayAmount < 0 -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                    val prefix = if (todayAmount > 0) "+" else ""

                    Text(
                        text = "Bilans dnia:",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "$prefix$todayAmount zł",
                        style = MaterialTheme.typography.titleMedium,
                        color = balanceColor
                    )
                }
            }
            Text(
                text = "Dzisiejsze transakcje:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp),
            )

            if (todayTransactions.isEmpty()) {
                Column(
                    Modifier.fillMaxSize(),
                    Arrangement.Center,
                    Alignment.CenterHorizontally
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
            } else LazyColumn(Modifier.fillMaxSize()) {
                items(
                    items = todayTransactions,
                    key = { it.transaction.id }
                ) { item ->
                    TransactionCard(
                        item = item,
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}