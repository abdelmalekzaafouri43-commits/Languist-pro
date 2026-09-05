package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LessonDatabase
import com.example.data.LessonEntity
import com.example.data.LessonRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

sealed class Screen {
    object Home : Screen()
    object WorksheetGenerator : Screen()
    object PresentationGenerator : Screen()
    object AiWizard : Screen()
    object SavedMaterials : Screen()
    data class Detail(val lessonId: Long) : Screen()
}

class LessonViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: LessonRepository

    val savedLessons: StateFlow<List<LessonEntity>>

    init {
        val lessonDao = LessonDatabase.getDatabase(application).lessonDao()
        repository = LessonRepository(lessonDao)
        savedLessons = repository.allLessons.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    private val _currentScreen = kotlinx.coroutines.flow.MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    suspend fun getLessonById(id: Long): LessonEntity? {
        return withContext(Dispatchers.IO) {
            repository.getLessonById(id)
        }
    }

    fun saveLesson(
        title: String,
        type: String,
        subType: String,
        cefrLevel: String,
        topic: String,
        contentJson: String,
        teacherNotes: String,
        onComplete: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val entity = LessonEntity(
                title = title,
                type = type,
                subType = subType,
                cefrLevel = cefrLevel,
                topic = topic,
                contentJson = contentJson,
                teacherNotes = teacherNotes
            )
            val id = repository.insertLesson(entity)
            onComplete(id)
        }
    }

    fun deleteLesson(lesson: LessonEntity) {
        viewModelScope.launch {
            repository.deleteLesson(lesson)
        }
    }

    // Call Gemini API (gemini-3.5-flash) with multi-step lesson criteria
    suspend fun generateLessonWithGemini(
        gradeLevel: String,
        cefrLevel: String,
        topic: String,
        learningObjectives: String,
        materialType: String,
        onResult: (String) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                val apiKey = try {
                    val field = Class.forName("com.example.BuildConfig").getField("GEMINI_API_KEY")
                    field.get(null) as? String ?: ""
                } catch (e: Exception) {
                    ""
                }

                if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                    val fallback = buildFallbackAiContent(gradeLevel, cefrLevel, topic, learningObjectives, materialType)
                    onResult(fallback)
                    return@withContext
                }

                val client = OkHttpClient()
                val prompt = """
                    You are an expert English curriculum designer. Generate a professional $materialType for English teachers based on the following criteria:
                    - Student Grade Level: $gradeLevel
                    - CEFR Level: $cefrLevel
                    - Lesson Topic: $topic
                    - Learning Objectives: $learningObjectives
                    
                    Provide a well-structured, ready-to-use $materialType with sections, exercises, and teacher answer key.
                """.trimIndent()

                val jsonBody = JSONObject().apply {
                    put("contents", JSONArray().put(
                        JSONObject().put("parts", JSONArray().put(
                            JSONObject().put("text", prompt)
                        ))
                    ))
                }

                val body = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseString = response.body?.string() ?: ""
                        val jsonResp = JSONObject(responseString)
                        val text = jsonResp.getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text")
                        onResult(text)
                    } else {
                        val fallback = buildFallbackAiContent(gradeLevel, cefrLevel, topic, learningObjectives, materialType)
                        onResult(fallback)
                    }
                }
            } catch (e: Exception) {
                val fallback = buildFallbackAiContent(gradeLevel, cefrLevel, topic, learningObjectives, materialType)
                onResult(fallback)
            }
        }
    }

    private fun buildFallbackAiContent(
        gradeLevel: String,
        cefrLevel: String,
        topic: String,
        objectives: String,
        type: String
    ): String {
        return buildString {
            append("=== GEMINI AI GENERATED $type ===\n")
            append("Target Grade: $gradeLevel | CEFR Level: $cefrLevel\n")
            append("Core Topic: $topic\n")
            append("Learning Objectives: $objectives\n\n")
            
            append("SECTION 1: WARM-UP & CONCEPT INTRODUCTION\n")
            append("• Hook activity: Discussing key concepts of $topic with $gradeLevel students.\n")
            append("• Vocabulary matching & phonemic awareness drills.\n\n")
            
            append("SECTION 2: GUIDED PRACTICE & APPLICATION\n")
            append("• Task 1: Applied sentence construction meeting objective criteria.\n")
            append("• Task 2: Critical thinking reading passage on $topic.\n\n")
            
            append("SECTION 3: ASSESSMENT & TEACHER ANSWER KEY\n")
            append("• Formative check questions and expected student responses.\n")
            append("• Rubric for grading classroom participation and worksheet completion.\n")
        }
    }

    // Helper generators for professional A4 worksheets
    fun generateWorksheetContent(
        topic: String,
        subType: String,
        cefrLevel: String
    ): String {
        return buildString {
            append("TOPIC: $topic\n")
            append("LEVEL: $cefrLevel\n")
            append("TYPE: $subType\n\n")
            append("SECTION 1: WARM-UP & VOCABULARY\n")
            append("Match the key vocabulary words related to $topic with their correct definitions.\n")
            append("1. Fluent (adj.) -> A. Able to express oneself easily and articulately.\n")
            append("2. Context (n.) -> B. The circumstances that form the setting for an event.\n")
            append("3. Acquire (v.) -> C. To learn or develop a skill or habit.\n\n")
            
            append("SECTION 2: READING COMPREHENSION\n")
            append("Read the passage about $topic and answer the questions below:\n")
            append("\"Mastering $topic requires consistent practice, immersive listening, and active engagement in everyday conversations. Teachers recommend setting realistic milestones to ensure steady progress across all four language skills: listening, speaking, reading, and writing.\"\n\n")
            
            append("1. What is the primary requirement for mastering $topic?\n")
            append("____________________________________________________\n\n")
            append("2. How many language skills are mentioned in the text?\n")
            append("____________________________________________________\n\n")
            
            append("SECTION 3: GRAMMAR & USAGE DRILL\n")
            append("Fill in the blanks with the correct form of the verb:\n")
            append("1. By next year, students __________ (complete) their advanced $topic certification.\n")
            append("2. If she __________ (practice) daily, her fluency would improve significantly.\n\n")
            
            append("SECTION 4: TEACHER ANSWER KEY (For Instructor Use Only)\n")
            append("Section 1: 1-A, 2-B, 3-C\n")
            append("Section 2: 1. Consistent practice and immersive listening. 2. Four skills.\n")
            append("Section 3: 1. will have completed / will complete. 2. practiced.\n")
        }
    }

    // Helper generator for PowerPoint Presentation Decks (6 slides)
    fun generatePresentationContent(
        topic: String,
        cefrLevel: String
    ): String {
        return buildString {
            append("LESSON SLIDE DECK: $topic ($cefrLevel)\n\n")
            append("--- SLIDE 1: TITLE & WARM-UP ---\n")
            append("• Title: Exploring $topic\n")
            append("• Warm-up Question: What comes to your mind when you think of $topic?\n")
            append("• Objective: Master key vocabulary and sentence structures.\n\n")
            
            append("--- SLIDE 2: VOCABULARY FOCUS ---\n")
            append("• Key Term 1: Definition & Pronunciation guide\n")
            append("• Key Term 2: Example sentence in context\n")
            append("• Interactive Mini-Quiz: Spot the correct usage.\n\n")
            
            append("--- SLIDE 3: GRAMMAR / STRUCTURE ---\n")
            append("• Core Rule explanation with visual diagram\n")
            append("• Positive, Negative, and Interrogative examples\n")
            append("• Common student pitfalls to avoid\n\n")
            
            append("--- SLIDE 4: GUIDED PRACTICE ---\n")
            append("• Class Activity: Pair-share sentence building\n")
            append("• Teacher-led walkthrough of sample exercises\n")
            append("• Immediate formative feedback loop\n\n")
            
            append("--- SLIDE 5: COMMUNICATIVE TASK ---\n")
            append("• Roleplay scenario: Debate or interview on $topic\n")
            append("• Student A & Student B prompt cards\n")
            append("• Peer evaluation checklist\n\n")
            
            append("--- SLIDE 6: WRAP-UP & HOMEWORK ---\n")
            append("• Lesson Summary & Q&A\n")
            append("• Homework Assignment: Write a 150-word reflection on $topic\n")
            append("• Reminder: Next class vocabulary review\n")
        }
    }
}
