package com.example.composeexample
//https://github.com/elye/demo_android_jetpack_compose_list_update
//https://medium.com/mobile-app-development-publication/setup-a-self-modifiable-list-of-data-in-jetpack-compose-2057c1ae6109
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeexample.ui.theme.ComposeExampleTheme
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.MutableState
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

class MainActivity : ComponentActivity() {
    private val viewModel = ItemViewModel() //модель данных нашего списка

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState!=null && savedInstanceState.containsKey("langs")) {
            //то мы наш массив langList берем из savedInstanceState
            val tempLangArray = savedInstanceState.getSerializable("langs") as ArrayList<ProgrLang>
            viewModel.clearList()
            tempLangArray.forEach {
                viewModel.addLangToEnd(it)
            }
            Toast.makeText(this, "From saved", Toast.LENGTH_SHORT).show()
        } else Toast.makeText(this, "From create", Toast.LENGTH_SHORT).show()//иначе просто сообщение

        setContent {
            val lazyListState = rememberLazyListState()
            ComposeExampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(Modifier.fillMaxSize()) { //создаем колонку
                        MakeAppBar(viewModel, lazyListState) // вызываем новую функцию
//                        MakeInputPart(
//                            viewModel,
//                            lazyListState
//                        ) //вызываем ф-ию для создания полей ввода данных
                        MakeList(
                            viewModel,
                            lazyListState
                        ) //вызываем ф-ию для самого списка с данными
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show() //сообщение для отслеживания
        var tempLangArray = ArrayList<ProgrLang>() //временный ArrayList для сохранения данных
        viewModel.langListFlow.value.forEach {//переносим данные из нашего основного массива
            tempLangArray.add(it)
        }
        outState.putSerializable("langs", tempLangArray) //помещаем созданный массив в хранилище
        //и даем ему метку langs, по ней потом его и найдем
        super.onSaveInstanceState(outState)  //вызов метода базового класса
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakeAppBar(model: ItemViewModel, lazyListState: LazyListState) {
//создаем объект для хранения состояния меню – открыто (true) или нет (false)
    var mDisplayMenu by remember { mutableStateOf(false) }
    val mContext = LocalContext.current // контекст нашего приложения
    val openDialog = remember { mutableStateOf(false)} //объект для состояния дочернего окна

    val scope = rememberCoroutineScope()
    val startForResult = //переменная-объект класса ManagedActivityResultLauncher,
//ей присваиваем результат вызова метода rememberLauncherForActivityResult
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//внутри метода смотрим результат работы запущенного активити – если закрытие с кодом RESULT_OK
            if (result.resultCode == Activity.RESULT_OK) {//то берем объект из его данных
                val newLang = result.data?.getSerializableExtra("newItem") as ProgrLang //как язык
                println("new lang name = ${newLang.name}") //вывод для отладки
                model.addLangToHead(newLang)
                scope.launch {//прокручиваем список, чтобы был виден добавленный элемент
                    lazyListState.scrollToItem(0)
                }
            }
        }

    if (openDialog.value) //если дочернее окно вызвано, то запускаем функцию для его создания
        MakeAlertDialog(context = mContext, dialogTitle = "About", openDialog = openDialog)
    TopAppBar( //создаем верхнюю панель нашего приложения, в нем будет меню
        title = { Text("Языки программирования") }, //заголовок в верхней панели
        actions = { //здесь разные действия можно прописать, нпаример, иконку для меню
            IconButton(onClick = { mDisplayMenu = !mDisplayMenu }) { //создаем иконку
                Icon(Icons.Default.MoreVert, null)  //в виде трех вертикальных точек
            } //в методе onClick прописано изменение объекта для хранения состояния меню
            DropdownMenu( //создаем меню
                expanded = mDisplayMenu, //признак, открыто оно или нет
                onDismissRequest = { mDisplayMenu = false } //при закрытии меню устанавливаем
                //соответствующее значение объекту mDisplayMenu
            ) {
                DropdownMenuItem( //создаем пункт меню для вызова информации о программе (About)
                    text = { Text(text = "About")}, //его текст
                    onClick = {  //и обработчик нажатия на него
                        //всплывающее сообщение с названием пункта
                        Toast.makeText(mContext, "About", Toast.LENGTH_SHORT).show()
                        mDisplayMenu = !mDisplayMenu //меняем параметр, отвечающий за состояние меню,
                        openDialog.value = true //и параметр, отвечающий за состояние дочернего окна,
                    }				//в котором выводим	 доп. информацию
                )
                //создаем второй пункт меню для вызова окна, в которое перенесли ввод нового языка
                DropdownMenuItem(
                    text = { Text(text = "Add lang") },
                    onClick = {
                        Toast.makeText(mContext, "Add lang", Toast.LENGTH_SHORT).show()
                        val newAct = Intent(mContext, InputActivity::class.java)
                        startForResult.launch(newAct) //запускаем новое окно и ждем от него данные
                        mDisplayMenu = !mDisplayMenu
                    }
                )
            }
        }
    )
}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MakeInputPart(model: ItemViewModel, lazyListState: LazyListState) {
//    var langName by remember { //объект для работы с текстом, для названия языка
//        mutableStateOf("")  //его начальное значение
//    }//в функцию mutableStateOf() в качестве параметра передается отслеживаемое значение
//    var langYear by remember { //объект для работы с текстом, для года создания языка
//        mutableStateOf(0) //его начальное значение
//    }
//    val scope = rememberCoroutineScope()
//    Row(
//        //ряд для расположения эл-ов
//        verticalAlignment = Alignment.CenterVertically, //центруем по вертикали
//        horizontalArrangement = Arrangement.spacedBy(10.dp), //и добавляем отступы между эл-ми
//    ) {
//        TextField( //текстовое поле для ввода имени языка
//            value = langName, //связываем текст из поля с созданным ранее объектом
//            onValueChange = { newText ->  //обработчик ввода значений в поле
//                langName = newText  //все изменения сохраняем в наш объект
//            },
//            textStyle = TextStyle( //объект для изменения стиля текста
//                fontSize = 20.sp  //увеличиваем шрифт
//            ),
//            label = { Text("Название") }, //это надпись в текстовом поле
//            modifier = Modifier.weight(2f)//это вес колонки. Нужен для распределения долей в ряду.
////Контейнер Row позволяет назначить вложенным компонентам ширину в соответствии с их весом.
////Поэтому полям с данными назначаем вес 2, кнопке вес 1, получается сумма
//// всех весов будет 5, и для полей с весом 2 будет выделяться по 2/5 от всей ширины ряда, для
////кнопки с весом 1 будет выделяться 1/5 от всей ширины ряда
//        )
//        TextField( //текстовое поле для ввода года создания языка
//            value = langYear.toString(), //связываем текст из поля с созданным ранее объектом
//            onValueChange = { newText ->  //обработчик ввода значений в поле
////т.к. newText (измененный текст) – это строка, а langYear – целое, то нужно преобразовывать
//                langYear = if (newText != "") newText.toInt() else 0  //в нужный формат
//            },                    //с учетом возможной пустой строки
//            textStyle = TextStyle( //объект для изменения стиля текста
//                fontSize = 20.sp  //увеличиваем шрифт
//            ),
//            //и меняем тип допустимых символов для ввода – только цифры
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            label = { Text("Год создания") },
//            modifier = Modifier.weight(2f) //назначаем вес поля
//        )
//        Button( //кнопка для добавления нового языка
//            onClick = { //при нажатии кнопки делаем отладочный вывод
//                println("added $langName $langYear")
//                //и добавляем в начало списка новый язык с нужными параметрами
//                model.addLangToHead(ProgrLang(langName, langYear))
//                scope.launch {//прокручиваем список, чтобы был виден добавленный элемент
//                    lazyListState.scrollToItem(0)
//                }
//                langName = ""  //и очищаем поля
//                langYear = 0
//            },
//            modifier = Modifier.weight(1f)
//        ) {
//            Text("Add")  //надпись для кнопки
//        }
//    }
//}

@Composable
fun MakeList(viewModel: ItemViewModel, lazyListState: LazyListState) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        state = lazyListState
    ) {
        items(
            items = viewModel.langListFlow.value,
            key = { lang -> lang.name },
            itemContent = { item ->
                ListRow(item)
            }
        )
    }
}

@Composable
fun MakeAlertDialog(context: Context, dialogTitle: String, openDialog: MutableState<Boolean>) {
//создаем переменную, в ней будет сохраняться текст, полученный из строковых ресурсов для выбранного языка
    var strValue = remember{ mutableStateOf("") } //для получения значения строки из ресурсов
//получаем id нужной строки из ресурсов через имя в dialogTitle
    val strId = context.resources.getIdentifier(dialogTitle, "string", context.packageName)
//секция try..catch нужна для обработки ошибки Resources.NotFoundException – отсутствие искомого ресурса
    try{
//если такой ресурс есть (т.е. его id не равен 0), то берем само значение этого ресурса
        if (strId != 0) strValue.value = context.getString(strId)
    } catch (e: Resources.NotFoundException) {
//если произошла ошибка Resources.NotFoundException, то ничего неделаем
    }
    AlertDialog( // создаем AlertDialog
        onDismissRequest = { openDialog.value = false },//действия при закрытии окна
        title = { Text(text = dialogTitle) }, //заголовок окна
        text = { Text(text = strValue.value, fontSize = 20.sp) },//содержимое окна
        confirmButton = { //кнопка Ok, которая будет закрывать окно
            Button(onClick = { openDialog.value = false })
            { Text(text = "OK") }
        }
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListRow(model: ProgrLang) {
    val context = LocalContext.current //получаем текущий контекст, он нужен для создания
    //всплывающего сообщения
    val openDialog = remember { mutableStateOf(false)} //по умолчанию – false, т.е. окно не вызвано
    val langSelected = remember { mutableStateOf("") } // и переменная для сохранения названия языка
    if (openDialog.value) //если дочернее окно (AlertDialog) вызвано
        MakeAlertDialog(context, langSelected.value, openDialog) //то создаем его

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween, //для правильного расположения эл-ов
        //в данном случае они будут располагаться по краям ряда
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .border(BorderStroke(2.dp, Color.Blue))
            .combinedClickable(
                //добавляем модификатор, отвечающий за обработку нажатий
                onClick = { //и прописываем действия в случае нажатия на элемент списка
                    println("item = ${model.name}") //это будет выведено в Logcat (вкладка
                    //внизу Android Studio)
// а тут делаем всплывающее сообщение, передаем ему текущий контекст, текст сообщения –
// названия языка, на который нажали, и время показа сообщения, после этого вызываем
// show() у созданного сообщения
                    langSelected.value = model.name //сохраняем имя языка, чтобы вставить в заголовок
                    // AlertDialog
                    Toast.makeText(context, "item = ${model.name}", Toast.LENGTH_LONG).show()
                    openDialog.value = true //присваиваем признаку открытия дочернего окна true
                },
            )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) { //ряд для текстовых полей
            Text(
                text = model.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 20.dp)
            )
            Text(
                text = model.year.toString(),
                fontSize = 20.sp,
                modifier = Modifier.padding(10.dp),
                fontStyle = FontStyle.Italic
            )
        }
        Image(//нужен import androidx.compose.foundation.Image
            painter = painterResource(id = model.picture), //указываем источник изображения
            contentDescription = "",  //можно вставить описание изображения
            contentScale = ContentScale.Fit, //параметры масштабирования изображения
            modifier = Modifier.size(90.dp)
        )
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeExampleTheme {
        Greeting("Android")
    }
}