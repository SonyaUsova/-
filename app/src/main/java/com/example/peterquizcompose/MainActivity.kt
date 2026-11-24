package com.example.peterquizcompose

import android.os.Bundle
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import kotlinx.coroutines.delay
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// --- ЦВЕТА ---
// --- ЦВЕТА ---
val ImperialRed = Color(0xFF704326) // Тёплый коричневый
val Gold = Color(0xFFC39743)        // Охра
val Parchment = Color(0xFFF3E5C7)   // Цвет старой бумаги
val DarkText = Color(0xFF3D2A1D)    // Тёмно-коричневый
val CorrectBg = Color(0xFFD9C38C)   // Светлая охра
val WrongBg = Color(0xFFCFAD8F)     // Приглушённый коричневый
val PalaceFont = FontFamily(
    Font(R.font.cormorant_regular)
)
// --- ДАННЫЕ (20 вопросов) ---
data class Question(
    val text: String,
    val imageResId: Int?, // null если картинки нет
    val answer: String,
    val isTrue: Boolean
)

val allQuestions = listOf(
    Question("Прадедушка Пушкина, Абрам Ганнибал, был родным сыном Петра I?", null, "Нет. Он был крестником Петра.", false),
    Question("По указу 1722 года дворянам, не годным к наукам, запрещали жениться?", null, "Да. Их свидетельствовали и запрещали вступать в брак.", true),
    Question("Иностранные послы считали, что все дела в России вершатся «за бутылкой»?", null, "Да. Так писал герцог де Лириа.", true),
    Question("Царевича Алексея Петр простил и отправил в монастырь?", null, "Нет. Его пытали и казнили.", false),
    Question("В 1704 году в Уфимском уезде ввели налог на цвет глаз?", null, "Да. Голубоглазые платили больше.", true),
    Question("Екатерина I происходила из знатного шведского рода?", null, "Нет. Она была дочерью крестьянина.", false),
    Question("В Прутском походе Екатерина спасла армию, отдав драгоценности туркам?", null, "Да. Это помогло договориться о мире.", true),
    Question("Евдокию Лопухину Петр насильно постриг в монахини?", null, "Да. А её брата казнили.", true),
    Question("«Всешутейший и всепьянейший собор» — это тайный совет министров?", null, "Нет. Это была пародийная пьяная организация.", false),
    Question("Указ об ассамблеях был издан в 1718 году?", null, "Да. Это были светские балы.", true),
    Question("Петр I лично казнил стрельцов после бунта 1698 года?", null, "Да. Он даже рубил головы сам.", true),
    Question("Табель о рангах позволял получить дворянство за службу?", null, "Да. Это сломало родовую замкнутость.", true),
    Question("Петр запретил носить бороды всем без исключения?", null, "Нет. Можно было платить налог и носить бороду.", false),
    Question("Строительство Санкт-Петербурга началось в 1703 году?", null, "Да. На Заячьем острове.", true),
    Question("Северная война длилась более 20 лет?", null, "Да. С 1700 по 1721 год.", true),
    Question("Петр I был первым императором России?", null, "Да. Титул принят в 1721 году.", true),
    Question("Меншиков был сыном простого крестьянина?", null, "Нет. Он был из небогатой семьи, но не крестьянин.", false),
    Question("Петр I ввел новый календарь и Новый год с 1 января?", null, "Да. С 1700 года.", true),
    Question("Кунсткамера была создана для хранения редких книг?", null, "Нет. Это музей редкостей и анатомических аномалий.", false),
    Question("Флот при Петре I стал одним из сильнейших в Европе?", null, "Да. Балтийский флот стал мощной силой.", true)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PeterQuizApp()
            }
        }
    }
}

@Composable
fun PeterQuizApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "register") {
        composable("register") {
            RegisterScreen(onStart = { navController.navigate("quiz") })
        }
        composable("quiz") {
            QuizScreen(onBack = { navController.popBackStack() })
        }
    }
}

@Composable
fun RegisterScreen(onStart: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Parchment)
    ) {
        Image(
        painter = painterResource(id = R.drawable.register_background),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                alpha = 0.5f
            },
        contentScale = ContentScale.Crop
    )


        // Содержимое экрана поверх картинок
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Дворцовые Интриги",
                fontSize = 36.sp,
                lineHeight = 42.sp,
                fontWeight = FontWeight.Bold,
                color = ImperialRed,
                textAlign = TextAlign.Center,
                fontFamily = PalaceFont
            )

            Text(
                text = "Эпоха Петра I",
                fontSize = 24.sp,
                color = DarkText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    errorMessage = null
                },
                label = { Text("Ваше имя") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onStart()
                    } else {
                        errorMessage = "Введите имя!"
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ImperialRed,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Начать игру",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Composable
fun QuizScreen(onBack: () -> Unit) {
    var currentIndex by remember { mutableStateOf(0) }
    var mistakes by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    // idle — карточка на месте, right/left — улетает.
    var cardState by remember { mutableStateOf("idle") }

    val coroutineScope = rememberCoroutineScope()

    if (mistakes >= 5) {
        GameOverScreen(onBack = onBack)
        return
    }

    if (currentIndex >= allQuestions.size) {
        WinScreen(mistakes = mistakes, onBack = onBack)
        return
    }

    val question = allQuestions[currentIndex]

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 900),
        label = "cardRotation"
    )

    val translationX by animateFloatAsState(
        targetValue = when (cardState) {
            "right" -> 2000f
            "left" -> -2000f
            else -> 0f
        },
        animationSpec = tween(durationMillis = 800),
        label = "cardTranslation"
    )

    val cardAlpha by animateFloatAsState(
        targetValue = if (cardState == "idle") 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "cardAlpha"
    )

    // isCorrect — не ответ «да/нет», а твоя оценка своего ответа.
    fun markAnswer(isCorrect: Boolean) {
        if (!isFlipped || cardState != "idle") return

        cardState = if (isCorrect) "right" else "left"

        coroutineScope.launch {
            // Сначала показываем анимацию вылета.
            delay(800)

            if (!isCorrect) {
                mistakes++
            }

            currentIndex++
            isFlipped = false
            cardState = "idle"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Parchment)
    ) {
        TextButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Text("Назад", color = ImperialRed)
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ошибки: $mistakes / 5",
                color = ImperialRed,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Вопрос ${currentIndex + 1} / ${allQuestions.size}",
                color = DarkText,
                fontSize = 16.sp
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 100.dp),
            contentAlignment = Alignment.Center
        ) {
            FlipCard(
                question = question,
                rotation = rotation,
                onFlip = {
                    if (cardState == "idle") {
                        isFlipped = !isFlipped
                    }
                },
                modifier = Modifier.graphicsLayer {
                    this.translationX = translationX
                    alpha = cardAlpha
                }
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { markAnswer(isCorrect = false) },
                enabled = isFlipped && cardState == "idle",
                colors = ButtonDefaults.buttonColors(
                    containerColor = WrongBg,
                    contentColor = DarkText
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier.size(110.dp)
            ) {
                Text(
                    text = "Неверно",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = { markAnswer(isCorrect = true) },
                enabled = isFlipped && cardState == "idle",
                colors = ButtonDefaults.buttonColors(
                    containerColor = CorrectBg,
                    contentColor = DarkText
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier.size(110.dp)
            ) {
                Text(
                    text = "Верно",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FlipCard(
    question: Question,
    rotation: Float,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp)
            .fillMaxHeight()
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .shadow(
                elevation = 20.dp,
                shape = shape,
                ambientColor = DarkText,
                spotColor = DarkText
            )
            .border(3.dp, ImperialRed, shape)
            .clickable(onClick = onFlip),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Parchment)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .border(
                    width = 1.dp,
                    color = Gold,
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(16.dp)
                .graphicsLayer {
                    rotationY = if (rotation > 90f) 180f else 0f
                },
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    question.imageResId?.let { resId ->
                        Image(
                            painter = painterResource(id = resId),
                            contentDescription = null,
                            modifier = Modifier
                                .size(150.dp)
                                .padding(bottom = 16.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Text(
                        text = question.text,
                        fontFamily = PalaceFont,
                        fontSize = 36.sp,
                        lineHeight = 30.sp,
                        textAlign = TextAlign.Center,
                        color = DarkText,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Text(
                        text = "Нажмите на карточку, чтобы увидеть ответ",
                        fontFamily = PalaceFont,
                        fontSize = 25.sp,
                        lineHeight = 18.sp,
                        color = DarkText.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }
            } else {
                Text(
                    text = question.answer,
                    fontFamily = PalaceFont,
                    fontSize = 34.sp,
                    lineHeight = 28.sp,
                    textAlign = TextAlign.Center,
                    color = DarkText,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp)
                )
            }
        }
    }
}


@Composable
fun GameOverScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ImperialRed)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Картинка над текстом
            Image(
                painter = painterResource(id = R.drawable.beard_cut),
                contentDescription = "Пётр отрезает бороду",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "ПЁТР ОТРЕЗАЛ\nТВОЮ БОРОДУ!",
                fontSize = 36.sp,
                lineHeight = 44.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontFamily = PalaceFont
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Ты допустила 5 ошибок.",
                fontSize = 18.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = ImperialRed
                )
            ) {
                Text("Попробовать снова")
            }
        }
    }
}


@Composable
fun WinScreen(mistakes: Int, onBack: () -> Unit) {
    val resultText = if (mistakes <= 2) {
        "Теперь ты потомственный дворянин!"
    } else {
        "Молодец, Пётр тебя сегодня не казнит!"
    }

    // Выбираем картинку в зависимости от результата
    val resultImage = if (mistakes <= 2) {
        R.drawable.noble
    } else {
        R.drawable.survived
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Parchment)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = resultImage),
                contentDescription = "Результат игры",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = resultText,
                fontFamily = PalaceFont,
                fontSize = 32.sp,
                lineHeight = 38.sp,
                color = ImperialRed,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Ты ответила на все вопросы!",
                fontSize = 18.sp,
                color = DarkText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ошибок: $mistakes",
                fontSize = 18.sp,
                color = ImperialRed,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ImperialRed,
                    contentColor = Color.White
                )
            ) {
                Text("В главное меню")
            }
        }
    }
}
