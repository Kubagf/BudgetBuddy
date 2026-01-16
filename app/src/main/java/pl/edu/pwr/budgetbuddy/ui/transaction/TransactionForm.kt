package pl.edu.pwr.budgetbuddy.ui.transaction

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import pl.edu.pwr.budgetbuddy.R
import pl.edu.pwr.budgetbuddy.data.Category
import pl.edu.pwr.budgetbuddy.data.Transaction
import pl.edu.pwr.budgetbuddy.data.TransactionType
import pl.edu.pwr.budgetbuddy.ui.BudgetViewModel
import pl.edu.pwr.budgetbuddy.utils.ImageUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionForm(
    viewModel: BudgetViewModel,
    initialTransaction: Transaction? = null,
    onSave: (Transaction) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var receiptPath by remember { mutableStateOf(initialTransaction?.receiptImagePath) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            val permanentPath = ImageUtils.saveImageToInternalStorage(context, tempCameraUri!!)
            receiptPath = permanentPath
        }
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val permanentPath = ImageUtils.saveImageToInternalStorage(context, uri)
            receiptPath = permanentPath
        }
    }


    var titleInput by remember { mutableStateOf(initialTransaction?.title ?: "") }
    var amountInput by remember { mutableStateOf(initialTransaction?.amount?.toString() ?: "") }
    var descriptionInput by remember { mutableStateOf(initialTransaction?.description ?: "") }
    var type by remember { mutableStateOf(initialTransaction?.type ?: TransactionType.EXPENSE) }

    val calendar = remember {
        Calendar.getInstance().apply {
            time = initialTransaction?.date ?: Date()
        }
    }

    var dateText by remember { mutableStateOf("") }
    var timeText by remember { mutableStateOf("") }

    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    fun updateDateTimeTexts() {
        dateText = dateFormat.format(calendar.time)
        timeText = timeFormat.format(calendar.time)
    }

    LaunchedEffect(Unit) { updateDateTimeTexts() }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val expenseCategories by viewModel.expenseCategories.collectAsState(initial = emptyList())
    val incomeCategories by viewModel.incomeCategories.collectAsState(initial = emptyList())
    val categories = if (type == TransactionType.EXPENSE) expenseCategories else incomeCategories

    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(type, categories, initialTransaction) {
        if (selectedCategory == null) {
            if (initialTransaction != null && categories.isNotEmpty()) {
                selectedCategory = categories.find { it.id == initialTransaction.categoryId }
            } else if (categories.isNotEmpty()) {
                selectedCategory = categories.first()
            }
        } else if (categories.isNotEmpty() && categories.none { it.id == selectedCategory?.id }) {
            selectedCategory = categories.first()
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

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = calendar.timeInMillis
        )
        DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val newDate = Calendar.getInstance()
                    newDate.timeInMillis = millis
                    calendar.set(Calendar.YEAR, newDate.get(Calendar.YEAR))
                    calendar.set(Calendar.MONTH, newDate.get(Calendar.MONTH))
                    calendar.set(Calendar.DAY_OF_MONTH, newDate.get(Calendar.DAY_OF_MONTH))
                    updateDateTimeTexts()
                }
                showDatePicker = false
            }) { Text("OK") }
        }, dismissButton = {
            TextButton(onClick = { showDatePicker = false }) { Text("Anuluj") }
        }) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE),
            is24Hour = true
        )
        AlertDialog(onDismissRequest = { showTimePicker = false }, confirmButton = {
            TextButton(onClick = {
                calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                calendar.set(Calendar.MINUTE, timePickerState.minute)
                updateDateTimeTexts()
                showTimePicker = false
            }) { Text("OK") }
        }, dismissButton = {
            TextButton(onClick = { showTimePicker = false }) { Text("Anuluj") }
        }, text = {
            TimePicker(state = timePickerState)
        })
    }

    Scaffold(contentWindowInsets = WindowInsets(0.dp), floatingActionButton = {
        FloatingActionButton(
            onClick = {
                if (!canSave) return@FloatingActionButton

                val tx = Transaction(
                    id = initialTransaction?.id ?: 0,
                    title = titleInput.trim(),
                    amount = amountValue!!,
                    categoryId = selectedCategory!!.id,
                    description = descriptionInput.trim().ifBlank { null },
                    date = calendar.time,
                    receiptImagePath = receiptPath,
                    type = type
                )
                onSave(tx)
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
                    Text(
                        text = if (initialTransaction == null) "Dodaj transakcję" else "Edytuj transakcję",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Button(onClick = onClose) {
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
                        shape = SegmentedButtonDefaults.itemShape(index, options.size), onClick = {
                            type = t
                            selectedCategory = null
                        }, selected = type == t
                    ) { Text(label) }
                }
            }

            Text("Rachunek", style = MaterialTheme.typography.bodyMedium)

            if (receiptPath == null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            val uri = ImageUtils.createTempPictureUri(context)
                            tempCameraUri = uri
                            cameraLauncher.launch(uri)
                        }, modifier = Modifier.weight(1f)
                    ) {
                        Icon(painterResource(id = R.drawable.camera), contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Aparat")
                    }

                    Button(
                        onClick = {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }, modifier = Modifier.weight(1f)
                    ) {
                        Icon(painterResource(id = R.drawable.gallery), contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Galeria")
                    }
                }
            }

            if (receiptPath != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = receiptPath),
                        contentDescription = "Zdjęcie paragonu",
                        modifier = Modifier.fillMaxSize()
                    )
                    IconButton(
                        onClick = { receiptPath = null },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(
                                MaterialTheme.colorScheme.surface,
                                shape = MaterialTheme.shapes.small
                            )
                    ) {
                        Icon(Icons.Filled.Close, "Usuń zdjęcie")
                    }
                }
            }

            OutlinedTextField(
                value = titleInput,
                onValueChange = { titleInput = it },
                label = { Text("Nazwa") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = amountInput,
                onValueChange = { input ->
                    amountInput = input.filter { c -> c.isDigit() || c == '.' || c == ',' }
                },
                label = { Text("Kwota (PLN)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showDatePicker = true }) {
                    OutlinedTextField(
                        value = dateText,
                        onValueChange = {},
                        label = { Text("Data") },
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.DateRange, "Wybierz datę") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showTimePicker = true }) {
                    OutlinedTextField(
                        value = timeText,
                        onValueChange = {},
                        label = { Text("Godzina") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

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
                    enabled = categories.isNotEmpty()
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
        }
    }
}