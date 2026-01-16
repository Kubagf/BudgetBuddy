package pl.edu.pwr.budgetbuddy.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.edu.pwr.budgetbuddy.data.TransactionType
import pl.edu.pwr.budgetbuddy.ui.BudgetViewModel
import pl.edu.pwr.budgetbuddy.ui.transaction.TransactionCard
import java.time.LocalDate
import java.time.ZoneId

private data class HomeChartStat(
    val amount: Double, val color: Color, val percentage: Float
)

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    viewModel: BudgetViewModel, navController: NavController
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

    val expensesSum = todayTransactions.filter { it.transaction.type == TransactionType.EXPENSE }
        .sumOf { it.transaction.amount }

    val incomesSum = todayTransactions.filter { it.transaction.type == TransactionType.INCOME }
        .sumOf { it.transaction.amount }

    val totalVolume = expensesSum + incomesSum

    val chartStats = mutableListOf<HomeChartStat>()

    if (totalVolume > 0) {
        if (incomesSum > 0) {
            chartStats.add(
                HomeChartStat(
                    amount = incomesSum,
                    color = Color(0xFF4CAF50),
                    percentage = (incomesSum / totalVolume).toFloat()
                )
            )
        }
        if (expensesSum > 0) {
            chartStats.add(
                HomeChartStat(
                    amount = expensesSum,
                    color = MaterialTheme.colorScheme.error,
                    percentage = (expensesSum / totalVolume).toFloat()
                )
            )
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

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
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
                            color = balanceColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (todayTransactions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // WYKRES
                            Box(
                                contentAlignment = Alignment.Center, modifier = Modifier.weight(1f)
                            ) {
                                HomeDonutChart(
                                    stats = chartStats, modifier = Modifier.size(120.dp)
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${todayTransactions.size}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "transakcji",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // LEGENDA
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                LegendItem(
                                    color = Color(0xFF4CAF50),
                                    label = "Przychody",
                                    amount = incomesSum
                                )
                                LegendItem(
                                    color = MaterialTheme.colorScheme.error,
                                    label = "Wydatki",
                                    amount = expensesSum
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = "Dzisiejsze transakcje:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
            )

            if (todayTransactions.isEmpty()) {
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
            } else LazyColumn(Modifier.fillMaxSize()) {
                items(
                    items = todayTransactions, key = { it.transaction.id }) { item ->
                    TransactionCard(
                        item = item, navController = navController, viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String, amount: Double) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$amount zł",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun HomeDonutChart(
    stats: List<HomeChartStat>, modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 30f
        val radius = size.minDimension / 2 - strokeWidth
        val center = Offset(size.width / 2, size.height / 2)
        var startAngle = -90f

        stats.forEach { stat ->
            val sweepAngle = stat.percentage * 360f

            drawArc(
                color = stat.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth)
            )
            startAngle += sweepAngle
        }
    }
}