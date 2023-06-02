package com.example.composeexample

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeexample.ui.theme.ComposeExampleTheme
import kotlinx.coroutines.launch

class InputActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeExampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MakeInputPart()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
//fun MakeInputPart(model: ItemViewModel, lazyListState: LazyListState) {
    fun MakeInputPart() {
        var langName by remember { //объект для работы с текстом, для названия языка
            mutableStateOf("")  //его начальное значение
        }//в функцию mutableStateOf() в качестве параметра передается отслеживаемое значение
        var langYear by remember { //объект для работы с текстом, для года создания языка
            mutableStateOf(0) //его начальное значение
        }
//        val scope = rememberCoroutineScope()
        Row(
            //ряд для расположения эл-ов
            verticalAlignment = Alignment.CenterVertically, //центруем по вертикали
            horizontalArrangement = Arrangement.spacedBy(10.dp), //и добавляем отступы между эл-ми
        ) {
            TextField( //текстовое поле для ввода имени языка
                value = langName, //связываем текст из поля с созданным ранее объектом
                onValueChange = { newText ->  //обработчик ввода значений в поле
                    langName = newText  //все изменения сохраняем в наш объект
                },
                textStyle = TextStyle( //объект для изменения стиля текста
                    fontSize = 20.sp  //увеличиваем шрифт
                ),
                label = { Text("Название") }, //это надпись в текстовом поле
                modifier = Modifier.weight(2f)//это вес колонки. Нужен для распределения долей в ряду.
            )
            TextField( //текстовое поле для ввода года создания языка
                value = langYear.toString(), //связываем текст из поля с созданным ранее объектом
                onValueChange = { newText ->  //обработчик ввода значений в поле
//т.к. newText (измененный текст) – это строка, а langYear – целое, то нужно преобразовывать
                    langYear = if (newText != "") newText.toInt() else 0  //в нужный формат
                },                    //с учетом возможной пустой строки
                textStyle = TextStyle( //объект для изменения стиля текста
                    fontSize = 20.sp  //увеличиваем шрифт
                ),
                //и меняем тип допустимых символов для ввода – только цифры
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Год создания") },
                modifier = Modifier.weight(2f) //назначаем вес поля
            )
            Button( //кнопка для добавления нового языка
                onClick = { //при нажатии кнопки делаем отладочный вывод
                    println("added $langName $langYear")
                    //создаем новый язык с введенными параметрами
                    val newLang = ProgrLang(langName, langYear)
                    val intent = Intent() //создаем намерение
                    //задаем доп.данные для намерения – наш новый объект
                    intent.putExtra("newItem", newLang)
                    //вставляем намерение в результат текущего окна
                    setResult(RESULT_OK, intent);
                    langName = "" //очищаем поля
                    langYear = 0
                    finish()  //и закрываем текущее окно
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Add")  //надпись для кнопки
            }
        }
    }
}
