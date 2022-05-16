package com.example.jetquiz.network

import com.example.jetquiz.model.Question
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.http.GET
import javax.inject.Singleton

interface QuestionApi {
    @GET("/itmmckernan/triviaJSON/master/world.json")
    suspend fun getAllQuestions(): Question
}
