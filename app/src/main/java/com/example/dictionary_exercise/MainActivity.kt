package com.example.dictionary_exercise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dictionary_exercise.ui.theme.DictionaryExerciseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DictionaryAppScreen()
        }
    }
}

@Composable
fun DictionaryAppScreen() {
    var searchText by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf("") }
    val context = LocalContext.current // Get the context outside remember
    val dbHelper = remember(context) { DatabaseHelper(context) } // Pass the context as a key

    DictionaryExerciseTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Enter a word") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    val definition = dbHelper.getDefinition(searchText)
                    searchResult = if (definition != null) {
                        "Definition of '$searchText': $definition"
                    } else {
                        val suggestions = dbHelper.getSubstringMatches(searchText)
                        if (suggestions.isNotEmpty()) {
                            "No exact match found.\nDid you mean?\n" + suggestions.joinToString("\n- ")
                        } else {
                            "'$searchText' not found."
                        }
                    }
                }) {
                    Text("Lookup")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = searchResult, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DictionaryAppPreview() {
    DictionaryAppScreen()
}