package pl.edu.pwr.budgetbuddy.ui.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.edu.pwr.budgetbuddy.data.TransactionType
import pl.edu.pwr.budgetbuddy.ui.BudgetViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class CategoryStat(
    val categoryName: String, val amount: Double, val color: Color, val percentage: Float
)

data class DailyStat(
    val day: Int, val amount: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: BudgetViewModel = viewModel()) {
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var currentMonth by remember { mutableStateOf(java.time.YearMonth.now()) }

    val zoneId = ZoneId.systemDefault()

    val filteredTransactions = transactions.filter {
        val txDate = it.transaction.date.toInstant().atZone(zoneId).toLocalDate()
        val txMonth = java.time.YearMonth.from(txDate)
        it.transaction.type == selectedType && txMonth == currentMonth
    }

    val totalAmount = filteredTransactions.sumOf { it.transaction.amount }

    val chartColors = listOf(
        Color(0xFF4CAF50),
        Color(0xFF2196F3),
        Color(0xFFFFC107),
        Color(0xFFE91E63),
        Color(0xFF9C27B0),
        Color(0xFF00BCD4),
        Color(0xFFFF5722),
        Color(0xFF795548),
        Color(0xFF607D8B),
        Color(0xFF3F51B5)
    )

    val categoryStats =
        filteredTransactions.groupBy { it.category?.name ?: "Brak kategorii" }.map { (name, txs) ->
            val sum = txs.sumOf { it.transaction.amount }
            name to sum
        }.sortedByDescending { it.second }.mapIndexed { index, (name, sum) ->
            CategoryStat(
                categoryName = name,
                amount = sum,
                percentage = if (totalAmount > 0) (sum / totalAmount).toFloat() else 0f,
                color = chartColors[index % chartColors.size]
            )
        }

    val daysInMonth = currentMonth.lengthOfMonth()
    val dailyStats = (1..daysInMonth).map { day ->
        val date = currentMonth.atDay(day)
        val sum = filteredTransactions.filter {
            val txDate = it.transaction.date.toInstant().atZone(zoneId).toLocalDate()
            txDate == date
        }.sumOf { it.transaction.amount }
        DailyStat(day, sum)
    }

    val maxDailyAmount = dailyStats.maxOfOrNull { it.amount }?.coerceAtLeast(1.0) ?: 1.0

    Scaffold(
        topBar = { TopAppBar(title = { Text("Statystyki") }) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val types = listOf("Wydatki", "Przychody")
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                types.forEachIndexed { index, label ->
                    val type = if (index == 0) TransactionType.EXPENSE else TransactionType.INCOME
                    SegmentedButton(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        shape = SegmentedButtonDefaults.itemShape(index, types.size)
                    ) { Text(label) }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Poprzedni")
                }
                val formatter = DateTimeFormatter.ofPattern("LLLL yyyy", Locale("pl"))
                Text(
                    text = currentMonth.format(formatter).replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Następny")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredTransactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Brak danych w tym miesiącu", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Text(
                            "Według kategorii",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(0.45f)
                                    .fillMaxHeight()
                                    .padding(8.dp)
                            ) {
                                DonutChart(stats = categoryStats)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "Suma",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "${totalAmount.toInt()} zł",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .weight(0.55f)
                                    .fillMaxHeight()
                                    .verticalScroll(rememberScrollState())
                                    .padding(start = 8.dp)
                            ) {
                                categoryStats.forEach { stat ->
                                    CategoryStatRow(stat)
                                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    item {
                        Text(
                            "Wydatki dzienne",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                        DailyBarChart(dailyStats = dailyStats, maxAmount = maxDailyAmount)
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DailyBarChart(dailyStats: List<DailyStat>, maxAmount: Double) {
    val barColor = MaterialTheme.colorScheme.primary
    val scrollState = rememberScrollState()
    val chartHeight = 160.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(chartHeight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(60.dp)
                .padding(end = 4.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "${maxAmount.toInt()} zł",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                textAlign = TextAlign.End
            )

            Text(
                text = "${(maxAmount / 2).toInt()} zł",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                textAlign = TextAlign.End
            )

            Text(
                text = "0 zł",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }

        VerticalDivider(color = Color.LightGray)

        Row(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(scrollState)
                .padding(start = 8.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            dailyStats.forEach { stat ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Box(
                        contentAlignment = Alignment.BottomCenter,
                        modifier = Modifier
                            .weight(1f)
                            .width(16.dp)
                    ) {
                        if (stat.amount > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight((stat.amount / maxAmount).toFloat())
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(barColor)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .height(1.dp)
                                    .fillMaxWidth()
                                    .background(Color.LightGray)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stat.day.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@Composable
fun DonutChart(stats: List<CategoryStat>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
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

@Composable
fun CategoryStatRow(stat: CategoryStat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(stat.color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = stat.categoryName,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${(stat.percentage * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
        Text(
            text = "${stat.amount.toInt()} zł",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}