package com.example.lab2271407

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import com.example.lab2271407.ui.theme.MyApplicationTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val viewModel: ViewModel = viewModel(factory = ViewModel.Factory)
                MainScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: ViewModel? = null) {
    val fallbackFlow = remember { MutableStateFlow(emptyList<ItemEntity>()) }
    val itemsList by (viewModel?.allItems ?: fallbackFlow).collectAsState()
    var openDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .semantics { testTagsAsResourceId = true }
                    .testTag("ActionButton"),
                onClick = {
                    name = ""
                    openDialog = true
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(itemsList) { item ->
                ListItem(
                    headlineContent = {
                        Text(
                            modifier = Modifier
                                .semantics { testTagsAsResourceId = true }
                                .testTag("Text"),
                            text = item.name
                        )
                    }
                )
            }
        }

        if (openDialog) {
            AlertDialog(
                onDismissRequest = { openDialog = false },
                title = { Text("항목 추가") },
                text = {
                    TextField(
                        modifier = Modifier
                            .semantics { testTagsAsResourceId = true }
                            .testTag("TextField"),
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") }
                    )
                },
                confirmButton = {
                    TextButton(
                        modifier = Modifier
                            .semantics { testTagsAsResourceId = true }
                            .testTag("Button"),
                        onClick = {
                            if (name.isNotBlank()) {
                                viewModel?.insert(name)
                            }
                            openDialog = false
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { openDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MyApplicationTheme {
        MainScreen()
    }
}