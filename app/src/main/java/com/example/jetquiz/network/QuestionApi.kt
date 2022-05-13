package com.example.jetquiz.network

import com.example.jetquiz.model.Question
import retrofit2.http.GET

interface QuestionApi {
    @GET("/")
    suspend fun getAllQuestions(): Question
}