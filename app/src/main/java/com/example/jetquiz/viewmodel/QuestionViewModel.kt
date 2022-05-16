package com.example.jetquiz.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetquiz.data.DataOrException
import com.example.jetquiz.model.QuestionItem
import com.example.jetquiz.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(private val repository: QuestionRepository) : ViewModel() {
    private val data: MutableState<DataOrException<ArrayList<QuestionItem>, Boolean, Exception>>
    = mutableStateOf(DataOrException(null, true, Exception("")))

    private var loading: MutableState<Boolean> = mutableStateOf(false)

    fun getLoadingState(): MutableState<Boolean> {
        return loading
    }

    init {
        getAllQuestions()
    }

    fun getAllQuestions() {
        viewModelScope.launch {
            loading.value = true
            data.value = repository.getAllQuestions()
            loading.value = false
        }
    }

    fun getData(): MutableState<DataOrException<ArrayList<QuestionItem>, Boolean, Exception>> {
        return data
    }

    fun getTotalQuestionCount(): Int {
        return data.value.data?.toMutableList()?.size!!
    }
}