package com.example.jetquiz

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetquiz.model.QuestionItem
import com.example.jetquiz.ui.theme.*
import com.example.jetquiz.viewmodel.QuestionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetQuizTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainApp()
                    // Modify here!
                }
            }
        }
    }
}

@Composable
fun MainApp() {
    val context = LocalContext.current
    val questionViewModel = viewModel<QuestionViewModel>()
    val count = remember{ mutableStateOf(0) }
    val answered = remember{ mutableStateOf(false) }

    // 1
    if (questionViewModel.getLoadingState().value)
        LoadingScreen()
    else if (questionViewModel.getData().value.data == null)
        ErrorScreen(questionViewModel)
    else if (count.value + 1 > questionViewModel.getTotalQuestionCount())
        ReachAllQuiz()
    else {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            QuestionHeader(count.value + 1, questionViewModel.getTotalQuestionCount())
            val item = questionViewModel.getData().value.data!![count.value]
            Spacer(modifier = Modifier.size(10.dp))
            ShowQuestionItem(item, answered.value) {
                answered.value = true
            }
            Spacer(modifier = Modifier.size(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        if (answered.value) {
                            answered.value = false
                            count.value += 1
                        }
                        else Toast.makeText(context, "You need to answer this quiz first.\nClick \"Skip\" instead to skip question.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .width(120.dp)
                        .height(50.dp)
                        .clip(RoundedCornerShape(30.dp))
                ) {
                    Text("Next")
                }
                Spacer(modifier = Modifier.size(10.dp))
                Button(
                    onClick = {
                        answered.value = false
                        count.value += 1
                    },
                    modifier = Modifier
                        .width(120.dp)
                        .height(50.dp)
                        .clip(RoundedCornerShape(30.dp))
                ) {
                    Text("Skip")
                }
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Loading quiz from server.")
        Text("Please wait...")
    }
}

@Composable
fun ErrorScreen(questionViewModel: QuestionViewModel) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Cannot fetch questions from server!")
        Text("Check your internet connection and try again.")
        Spacer(modifier = Modifier.size(5.dp))
        Button(onClick = {
            questionViewModel.getAllQuestions()
        }) {
            Text("Reload")
        }
    }
}

@Composable
fun ReachAllQuiz() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("You reached of the end question of quiz!")
        Text("Thanks for take quiz from this app!")
    }
}

@Preview(showBackground = true)
@Composable
fun ShowQuestionTest() {
    Column {
        QuestionHeader(1, 1)
        val sample = QuestionItem(
            question = "LFD2 was banned in Australia",
            category = "world",
            answer = "True",
            choices = listOf("False", "True", "123"),
        )
        Spacer(modifier = Modifier.size(10.dp))
        ShowQuestionItem(sample, true) {

        }
    }
}


@Composable
fun QuestionHeader(current: Int, total: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSystemInDarkTheme())
                    QuestionHeaderColorDark
                else QuestionHeaderColorLight
            ),
    ) {
        Row(
            modifier = Modifier
                .padding(top = 40.dp, bottom = 40.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Question $current",
                style = MaterialTheme.typography.h4,
                color = (
                        if (isSystemInDarkTheme()) TextDark
                        else TextLight
                        )
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                modifier = Modifier
                    .padding(bottom = 5.dp),
                text = "/$total",
                style = MaterialTheme.typography.h6,
                color = (
                        if (isSystemInDarkTheme()) TextDark
                        else TextLight
                        )
            )
        }
    }
}

@Composable
fun ShowQuestionItem(item: QuestionItem, answered: Boolean, answer: () -> Unit) {
    val selectedOption = remember {mutableStateOf("")}

    if (!answered)
        selectedOption.value = ""

    Column(
        modifier = Modifier
            .padding(start = 30.dp, end = 30.dp),
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            text = item.question,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.size(20.dp))
        Column {
            item.choices.forEach { choice ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp)
                        .clip(AnswerBorderShapes)
                        .background(
                            if (answered) {
                                if (choice == selectedOption.value && choice != item.answer)
                                    ChoseAnswerColor
                                else if (choice == item.answer) RightAnswerColor
                                else if (isSystemInDarkTheme())
                                    DefaultAnswerColorDark
                                else DefaultAnswerColorLight
                            } else if (isSystemInDarkTheme()) DefaultAnswerColorDark
                            else DefaultAnswerColorLight
                        )
                        .border(
                            1.dp,
                            BorderAnswerColor,
                            AnswerBorderShapes
                        )
                        .selectable(
                            selected = (choice == selectedOption.value),
                            onClick = {
                                if (!answered) {
                                    selectedOption.value = choice
                                }
                                answer()
                            }
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            // this method is called when radio button is selected.
                            selected = (choice == selectedOption.value),
                            // below method is called on clicking of radio button.
                            onClick = {
                                if (!answered) {
                                    selectedOption.value = choice
                                }
                                answer()
                            }
                        )
                        Text(text = choice)
                    }
                }

            }
        }
        Spacer(modifier = Modifier.size(20.dp))
        if (answered) {
            val correct = selectedOption.value == item.answer
            val resultText =
                if (correct)
                    "You chose correct answer. Congratulations!"
                else "You seem to chose wrong answer!"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ResultBoxColor),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                ) {
                    Text(
                        text = resultText
                    )
                    if (!correct) {
                        Text (
                            text = "Correct answer: ${item.answer}"
                        )
                    }
                }
            }
        }
    }
}
