package com.example.data

import kotlinx.coroutines.flow.Flow

class LessonRepository(private val lessonDao: LessonDao) {
    val allLessons: Flow<List<LessonEntity>> = lessonDao.getAllLessons()

    suspend fun getLessonById(id: Long): LessonEntity? = lessonDao.getLessonById(id)

    suspend fun insertLesson(lesson: LessonEntity): Long = lessonDao.insertLesson(lesson)

    suspend fun deleteLesson(lesson: LessonEntity) = lessonDao.deleteLesson(lesson)

    suspend fun deleteLessonById(id: Long) = lessonDao.deleteLessonById(id)
}
