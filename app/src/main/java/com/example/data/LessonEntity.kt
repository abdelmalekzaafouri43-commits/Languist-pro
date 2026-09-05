package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lesson_materials")
data class LessonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val type: String, // "WORKSHEET" or "PRESENTATION"
    val subType: String, // e.g., "Grammar Drill", "Reading Comp", "Vocabulary Matching", "Slide Deck"
    val cefrLevel: String, // e.g., "B1 Intermediate"
    val topic: String,
    val contentJson: String, // JSON or formatted text of questions/slides
    val teacherNotes: String,
    val createdAt: Long = System.currentTimeMillis()
)
