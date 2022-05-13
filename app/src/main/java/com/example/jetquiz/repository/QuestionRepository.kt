package com.example.jetquiz.repository

import com.example.jetquiz.data.DataOrException
import com.example.jetquiz.model.QuestionItem
import com.example.jetquiz.network.QuestionApi
import javax.inject.Inject

class QuestionRepository @Inject constructor(private val api: QuestionApi) {
    private val dataOrException = DataOrException<ArrayList<QuestionItem>, Boolean, Exception> ()

    suspend fun getAllQuestions():
            DataOrException<ArrayList<QuestionItem>, Boolean, Exception> {
        try {
            dataOrException.loading = true
            dataOrException.data = api.getAllQuestions()
            if (dataOrException.data.toString().isNotEmpty())
                dataOrException.loading = false
        } catch (ex: Exception) {
            dataOrException.e = ex
        }

        return dataOrException
    }
}