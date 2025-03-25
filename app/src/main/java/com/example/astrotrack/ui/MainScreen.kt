package com.example.astrotrack.ui

import android.app.DatePickerDialog
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.astrotrack.ui.components.ApodListItem
import com.example.astrotrack.viewmodel.ApodViewModel
import java.time.LocalDate
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(viewModel: ApodViewModel, navController: NavController) {
    var startDate by remember { mutableStateOf(viewModel.savedStartDate) }
    var endDate by remember { mutableStateOf(viewModel.savedEndDate) }
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val apodList by viewModel.apodList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current

    // 🔍 Filter across multiple fields
    val filteredList = if (searchQuery.isNotBlank())
        apodList.filter {
            it.title.contains(searchQuery, true) ||
                    it.explanation.contains(searchQuery, true) ||
                    it.date.contains(searchQuery, true) ||
                    (it.copyright?.contains(searchQuery, true) ?: false)
        }
    else apodList

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Explore Space", style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = { showSearch = !showSearch }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }

        if (showSearch) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by title, explanation...") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DateInputField("Start Date", startDate, {
                startDate = it
                viewModel.savedStartDate = it
            }, Modifier.weight(1f))

            DateInputField("End Date", endDate, {
                endDate = it
                viewModel.savedEndDate = it
            }, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        val today = LocalDate.now().toString()
        Button(
            onClick = {
                when {
                    startDate.isEmpty() || endDate.isEmpty() -> viewModel.setError("Please select both dates.")
                    endDate > today -> viewModel.setError("End date cannot be in the future.")
                    else -> viewModel.fetchApod(startDate, endDate)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Explore")
        }

        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            LazyColumn { items(6) { ShimmerCardItem() } }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                itemsIndexed(filteredList) { _, item ->
                    ApodListItem(
                        item = item,
                        onClick = {
                            viewModel.setSelectedItem(item)
                            navController.navigate("detail/selected")
                        },
                        onFavoriteClick = { apod, isFav ->
                            viewModel.toggleFavorite(apod, isFav) { message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        },
                        isFavorite = viewModel.isFavorited(item)
                    )
                }
            }
        }
    }
}

@Composable
fun DateInputField(
    label: String,
    date: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var text by remember { mutableStateOf(date) }

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            if (it.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) {
                onDateSelected(it)
            }
        },
        label = { Text(label) },
        placeholder = { Text("yyyy-mm-dd") },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Pick date",
                modifier = Modifier.clickable {
                    val calendar = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            val selected = String.format("%04d-%02d-%02d", year, month + 1, day)
                            text = selected
                            onDateSelected(selected)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            )
        },
        singleLine = true,
        modifier = modifier
    )
}
