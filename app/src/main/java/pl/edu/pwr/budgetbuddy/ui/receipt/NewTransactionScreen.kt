package pl.edu.pwr.budgetbuddy.ui.receipt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.edu.pwr.budgetbuddy.data.Category
import pl.edu.pwr.budgetbuddy.data.Transaction
import pl.edu.pwr.budgetbuddy.data.TransactionType
import pl.edu.pwr.budgetbuddy.ui.BudgetViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTransactionScreen(
    navController: NavController, viewModel: BudgetViewModel
) {
    var titleInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(TransactionType.EXPENSE) }

    val expenseCategories by viewModel.expenseCategories.collectAsState(initial = emptyList())
    val incomeCategories by viewModel.incomeCategories.collectAsState(initial = emptyList())
    val categories = if (type == TransactionType.EXPENSE) expenseCategories else incomeCategories

    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(type, categories) {
        val current = selectedCategory
        selectedCategory = when {
            categories.isEmpty() -> null
            current == null -> categories.first()
            categories.any { it.id == current.id } -> current
            else -> categories.first()
        }
    }

    val amountValue by remember(amountInput) {
        derivedStateOf { amountInput.trim().replace(',', '.').toDoubleOrNull() }
    }

    val canSave by remember(amountValue, selectedCategory, titleInput) {
        derivedStateOf {
            val a = amountValue
            a != null && a > 0.0 && selectedCategory != null && titleInput.trim().isNotEmpty()
        }
    }

    Scaffold(contentWindowInsets = WindowInsets(0.dp), floatingActionButton = {
        FloatingActionButton(
            onClick = {
                if (!canSave) return@FloatingActionButton

                val tx = Transaction(
                    title = titleInput.trim(),
                    amount = amountValue!!,
                    categoryId = selectedCategory!!.id,
                    description = descriptionInput.trim().ifBlank { null },
                    date = Date(),
                    receiptImagePath = null,
                    type = type
                )
                viewModel.addTransaction(tx)
                navController.navigateUp()
            }) {
            Icon(Icons.Filled.Done, contentDescription = "Zapisz")
        }
    }, topBar = {
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dodaj transakcję", style = MaterialTheme.typography.titleLarge)
                    Button(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.Close, contentDescription = "Zamknij")
                    }
                }
            })
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val options = listOf("Wydatek", "Przychód")

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                options.forEachIndexed { index, label ->
                    val t = TransactionType.entries.toTypedArray()[index]
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index, options.size),
                        onClick = { type = t },
                        selected = type == t
                    ) { Text(label) }
                }
            }

            OutlinedTextField(
                value = titleInput,
                onValueChange = { titleInput = it },
                label = { Text("Nazwa") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = titleInput.isBlank()
            )

            OutlinedTextField(
                value = amountInput,
                onValueChange = { input ->
                    amountInput = input.filter { c -> c.isDigit() || c == '.' || c == ',' }
                },
                label = { Text("Kwota (PLN)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = amountInput.isBlank() && (amountValue == null || amountValue!! <= 0.0)
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { if (categories.isNotEmpty()) expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Kategoria") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    enabled = categories.isNotEmpty(),
                    isError = selectedCategory == null
                )

                ExposedDropdownMenu(
                    expanded = expanded, onDismissRequest = { expanded = false }) {
                    categories.forEach { cat ->
                        DropdownMenuItem(text = { Text(cat.name) }, onClick = {
                            selectedCategory = cat
                            expanded = false
                        })
                    }
                }
            }

            OutlinedTextField(
                value = descriptionInput,
                onValueChange = { descriptionInput = it },
                label = { Text("Opis (opcjonalnie)") },
                modifier = Modifier.fillMaxWidth()
            )

            if (categories.isEmpty()) {
                Text(
                    text = "Brak kategorii dla tego typu.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}