package pl.edu.pwr.budgetbuddy.ui.category

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import pl.edu.pwr.budgetbuddy.data.Category
import pl.edu.pwr.budgetbuddy.data.TransactionType
import pl.edu.pwr.budgetbuddy.ui.BudgetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    viewModel: BudgetViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val currentType = if (selectedTabIndex == 0) TransactionType.EXPENSE else TransactionType.INCOME

    val categories by if (currentType == TransactionType.EXPENSE) {
        viewModel.expenseCategories.collectAsState(initial = emptyList())
    } else {
        viewModel.incomeCategories.collectAsState(initial = emptyList())
    }

    var showDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }
    var nameInput by remember { mutableStateOf("") }

    fun openDialog(category: Category? = null) {
        categoryToEdit = category
        nameInput = category?.name ?: ""
        showDialog = true
    }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, topBar = {
        TopAppBar(
            title = { Text("Kategorie") }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = { openDialog(null) },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(Icons.Filled.Add, "Dodaj kategorię")
        }
    }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Wydatki") })
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Przychody") })
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(categories) { category ->
                    ListItem(
                        headlineContent = {
                            Text(
                                text = category.name, style = MaterialTheme.typography.bodyLarge
                            )
                        }, trailingContent = {
                            Row {
                                IconButton(onClick = { openDialog(category) }) {
                                    Icon(
                                        Icons.Filled.Edit,
                                        "Edytuj",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { viewModel.deleteCategory(category) }) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        "Usuń",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }, colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false }, title = {
            Text(
                text = if (categoryToEdit == null) "Nowa kategoria" else "Edytuj kategorię",
                style = MaterialTheme.typography.headlineSmall
            )
        }, text = {
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("Nazwa kategorii") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                shape = MaterialTheme.shapes.medium
            )
        }, confirmButton = {
            Button(
                onClick = {
                    if (nameInput.isNotBlank()) {
                        if (categoryToEdit == null) {
                            viewModel.addCategory(nameInput.trim(), currentType)
                        } else {
                            val updated = categoryToEdit!!.copy(name = nameInput.trim())
                            viewModel.updateCategory(updated)
                        }
                        showDialog = false
                    }
                }) {
                Text("Zapisz")
            }
        }, dismissButton = {
            TextButton(onClick = { showDialog = false }) {
                Text("Anuluj")
            }
        })
    }
}