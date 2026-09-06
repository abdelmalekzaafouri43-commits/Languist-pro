package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.LessonViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiWizardScreen(
    viewModel: LessonViewModel
) {
    var currentStep by remember { mutableIntStateOf(1) }
    
    // Form fields
    var gradeLevel by remember { mutableStateOf("Middle School (Grade 7-9)") }
    var cefrLevel by remember { mutableStateOf("B1 Intermediate") }
    var topic by remember { mutableStateOf("Environmental Science & Sustainable Living") }
    var materialType by remember { mutableStateOf("A4 Worksheet") }
    var learningObjectives by remember { mutableStateOf("Students will learn eco-vocabulary and practice conditional sentences (First and Second Conditionals).") }
    
    var isGenerating by remember { mutableStateOf(false) }
    var generatedResult by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Lesson Wizard (Gemini)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = {
                        if (currentStep > 1 && currentStep < 4) {
                            currentStep--
                        } else {
                            viewModel.navigateTo(Screen.Home)
                        }
                    }) {
                        Text("← Back", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Step Indicator Progress Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..4) {
                        val active = i <= currentStep
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp),
                            shape = RoundedCornerShape(3.dp),
                            color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        ) {}
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = when (currentStep) {
                        1 -> "Step 1: Student Demographics & CEFR Level"
                        2 -> "Step 2: Topic & Material Format"
                        3 -> "Step 3: Learning Objectives & Custom Instructions"
                        else -> "Step 4: AI Generation & Preview"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            when (currentStep) {
                1 -> {
                    item {
                        OutlinedTextField(
                            value = gradeLevel,
                            onValueChange = { gradeLevel = it },
                            label = { Text("Student Grade Level / Age Group") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = cefrLevel,
                            onValueChange = { cefrLevel = it },
                            label = { Text("CEFR Language Proficiency Level") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { currentStep = 2 },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Next Step")
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                2 -> {
                    item {
                        OutlinedTextField(
                            value = topic,
                            onValueChange = { topic = it },
                            label = { Text("Core Lesson Topic") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = materialType,
                            onValueChange = { materialType = it },
                            label = { Text("Material Format (e.g., A4 Worksheet, PowerPoint Deck)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { currentStep = 1 },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Back")
                            }
                            Button(
                                onClick = { currentStep = 3 },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Next Step")
                            }
                        }
                    }
                }
                3 -> {
                    item {
                        OutlinedTextField(
                            value = learningObjectives,
                            onValueChange = { learningObjectives = it },
                            label = { Text("Learning Objectives & Specific Teacher Requirements") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 5
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { currentStep = 2 },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Back")
                            }
                            Button(
                                onClick = {
                                    currentStep = 4
                                    isGenerating = true
                                    scope.launch {
                                        viewModel.generateLessonWithGemini(
                                            gradeLevel = gradeLevel,
                                            cefrLevel = cefrLevel,
                                            topic = topic,
                                            learningObjectives = learningObjectives,
                                            materialType = materialType
                                        ) { result ->
                                            generatedResult = result
                                            isGenerating = false
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Generate via Gemini", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                4 -> {
                    if (isGenerating) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Gemini AI is crafting your professional $materialType...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                ) {
                                    Text(
                                        text = "Generated Lesson Preview ($topic)",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = generatedResult,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    viewModel.saveLesson(
                                        title = topic,
                                        type = if (materialType.contains("Worksheet", ignoreCase = true)) "WORKSHEET" else "PRESENTATION",
                                        subType = materialType,
                                        cefrLevel = cefrLevel,
                                        topic = topic,
                                        contentJson = generatedResult,
                                        teacherNotes = "Grade: $gradeLevel | Objectives: $learningObjectives"
                                    ) { newId ->
                                        viewModel.navigateTo(Screen.Detail(newId))
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Save Material to Vault", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
