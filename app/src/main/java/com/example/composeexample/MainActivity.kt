package com.example.composeexample
//https://github.com/elye/demo_android_jetpack_compose_list_update
//https://medium.com/mobile-app-development-publication/setup-a-self-modifiable-list-of-data-in-jetpack-compose-2057c1ae6109
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeexample.ui.theme.ComposeExampleTheme
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter

class MainActivity : ComponentActivity() {
    private val viewModel = ItemViewModel() //модель данных нашего списка

    //    var dbHelper : LangsDbHelper? = null // объект класса LangsDbHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dbHelper = LangsDbHelper(this)  //создаем объект класса LangsDbHelper
        if (savedInstanceState != null && savedInstanceState.containsKey("langs")) {
            //то мы наш массив langList берем из savedInstanceState
            val tempLangArray = savedInstanceState.getSerializable("langs") as ArrayList<ProgrLang>
            viewModel.clearList()
            tempLangArray.forEach {
                viewModel.addLangToEnd(it)
            }
            Toast.makeText(this, "From saved", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "From create", Toast.LENGTH_SHORT).show()
            if (dbHelper!!.isEmpty()) {  //если БД пустая
                println("DB is emty")
                var tempLangArray =
                    ArrayList<ProgrLang>() //временный ArrayList для сохранения данных
                viewModel.langListFlow.value.forEach {//переносим данные из нашего основного массива
                    tempLangArray.add(it)
                }
                dbHelper!!.addArrayToDB(tempLangArray) //заносим в нее наш массив
                dbHelper!!.printDB()  //и выводим в консоль для проверки
            } else {  //иначе, если в БД есть записи
                println("DB has records")
                dbHelper!!.printDB()   //выводим записи в консоль для проверки
                val tempLangArray = dbHelper!!.getLangsArray()  //берем записи из БД в виде массива
                viewModel.clearList() //очищаем нашу модель данных
                tempLangArray.forEach {//и в цикле по массиву переносим данные в нашу модель
                    viewModel.addLangToEnd(it)
                }
            }
        }
        setContent {
            val lazyListState = rememberLazyListState()
            ComposeExampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(Modifier.fillMaxSize()) { //создаем колонку
                        MakeAppBar(viewModel, lazyListState, dbHelper!!) // вызываем новую функцию
//                        MakeList(viewModel, lazyListState, dbHelper!!) //вызываем ф-ию для самого списка с данными
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

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakeAppBar(model: ItemViewModel, lazyListState: LazyListState, dbHelper: LangsDbHelper) {
//создаем объект для хранения состояния меню – открыто (true) или нет (false)
    var mDisplayMenu by remember { mutableStateOf(false) }
    val mContext = LocalContext.current // контекст нашего приложения
    val openDialog = remember { mutableStateOf(false) } //объект для состояния дочернего окна

    val scope = rememberCoroutineScope()
    val startForResult = //переменная-объект класса ManagedActivityResultLauncher,
//ей присваиваем результат вызова метода rememberLauncherForActivityResult
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//внутри метода смотрим результат работы запущенного активити – если закрытие с кодом RESULT_OK
            if (result.resultCode == Activity.RESULT_OK) {//то берем объект из его данных
                val newLang = result.data?.getSerializableExtra("newItem") as ProgrLang //как язык
                println("new lang name = ${newLang.name}") //вывод для отладки
                model.addLangToHead(newLang)
                dbHelper.addLang(newLang)
                scope.launch {//прокручиваем список, чтобы был виден добавленный элемент
                    lazyListState.scrollToItem(0)
                }
            }
        }

    if (openDialog.value) //если дочернее окно вызвано, то запускаем функцию для его создания
        MakeAlertDialog(context = mContext, dialogTitle = "About", openDialog = openDialog)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
//    val scaffoldState = rememberScaffoldState()
    TopAppBar( //создаем верхнюю панель нашего приложения, в нем будет меню
        title = { Text("Языки программирования") }, //заголовок в верхней панели
        actions = { //здесь разные действия можно прописать, например, иконку для меню
            IconButton(onClick = { mDisplayMenu = !mDisplayMenu }) { //создаем иконку
                Icon(Icons.Default.MoreVert, null)  //в виде трех вертикальных точек
            } //в методе onClick прописано изменение объекта для хранения состояния меню
            DropdownMenu( //создаем меню
                expanded = mDisplayMenu, //признак, открыто оно или нет
                onDismissRequest = { mDisplayMenu = false } //при закрытии меню устанавливаем
                //соответствующее значение объекту mDisplayMenu
            ) {
                DropdownMenuItem( //создаем пункт меню для вызова информации о программе (About)
                    text = { Text(text = "About") }, //его текст
                    onClick = {  //и обработчик нажатия на него
                        //всплывающее сообщение с названием пункта
                        Toast.makeText(mContext, "About", Toast.LENGTH_SHORT).show()
                        mDisplayMenu =
                            !mDisplayMenu //меняем параметр, отвечающий за состояние меню,
                        openDialog.value =
                            true //и параметр, отвечающий за состояние дочернего окна,
                    }                //в котором выводим	 доп. информацию
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
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    scope.launch {
                        drawerState?.open()
                    }
                },
            ) {
                Icon(
                    Icons.Rounded.Menu,
                    contentDescription = ""
                )
            }
        }
    )
//    val drawerState = rememberDrawerState(DrawerValue.Closed)
//    val scope = rememberCoroutineScope()
    // icons to mimic drawer destinations
    val items = listOf(Icons.Default.Favorite, Icons.Default.Face, Icons.Default.Email)
    val selectedItem = remember { mutableStateOf(items[0]) }
    ModalNavigationDrawer( //todo сделать вызов окна с рисованием
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                items.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item, contentDescription = null) },
                        label = { Text(item.name) },
                        selected = item == selectedItem.value,
                        onClick = {
                            scope.launch { drawerState.close() }
                            selectedItem.value = item
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
//                Text(text = if (drawerState.isClosed) ">>> Swipe >>>" else "<<< Swipe <<<")
//                Spacer(Modifier.height(20.dp))
//                Button(onClick = { scope.launch { drawerState.open() } }) {
//                    Text("Click to open")
//                }
                MakeList(viewModel = model, lazyListState, dbHelper)
            }
        }
    )

}

@Composable
fun MakeList(viewModel: ItemViewModel, lazyListState: LazyListState, dbHelper: LangsDbHelper) {
    val langListState = viewModel.langListFlow.collectAsState()
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
                ListRow(item, langListState, viewModel, dbHelper)
            }
        )
    }
}

@Composable
fun MakeAlertDialog(context: Context, dialogTitle: String, openDialog: MutableState<Boolean>) {
//создаем переменную, в ней будет сохраняться текст, полученный из строковых ресурсов для выбранного языка
    var strValue = remember { mutableStateOf("") } //для получения значения строки из ресурсов
//получаем id нужной строки из ресурсов через имя в dialogTitle
    val strId = context.resources.getIdentifier(dialogTitle, "string", context.packageName)
//секция try..catch нужна для обработки ошибки Resources.NotFoundException – отсутствие искомого ресурса
    try {
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

fun pictureIsInt(picture: String): Boolean {
    var data = try {
        picture.toInt()
    } catch (e: NumberFormatException) {
        null
    }
    return data != null
}

//@Composable
//fun getImage(picture: String): Painter {
//    val painter = if (pictureIsInt(picture)) {
//        painterResource(picture.toInt())
//    } else {
//        rememberImagePainter(picture)
//    }
//    return painter
//}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListRow(
    model: ProgrLang,
    langListState: State<List<ProgrLang>>,
    viewModel: ItemViewModel,
    dbHelper: LangsDbHelper
) {
    val context = LocalContext.current //получаем текущий контекст, он нужен для создания
    //всплывающего сообщения
    val openDialog = remember { mutableStateOf(false) } //по умолчанию – false, т.е. окно не вызвано
    var langSelected = remember { mutableStateOf("") } // и переменная для сохранения названия языка
    if (openDialog.value) //если дочернее окно (AlertDialog) вызвано
        MakeAlertDialog(context, langSelected.value, openDialog) //то создаем его
    var mDisplayMenu by remember { mutableStateOf(false) }
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.data?.data != null) {
                println("image uri = ${res.data?.data}") //отладочный вывод (будет в разделе Run внизу IDE)
                val imgURI = res.data?.data
                val index = langListState.value.indexOf(model)
                viewModel.changeImage(index, imgURI.toString())
                dbHelper!!.changeImgForLang(model.name, imgURI.toString())
            }
        }

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
                    langSelected.value =
                        model.name //сохраняем имя языка, чтобы вставить в заголовок
                    // AlertDialog
                    Toast
                        .makeText(context, "item = ${model.name}", Toast.LENGTH_LONG)
                        .show()
                    openDialog.value = true //присваиваем признаку открытия дочернего окна true
                },
                onLongClick = {
                    mDisplayMenu = true
//                    langSelected = model
//                    println("long item = ${model.name}")
                }

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
        DropdownMenu(
            expanded = mDisplayMenu,
            onDismissRequest = { mDisplayMenu = false }
        ) {
            // Creating dropdown menu item, on click would create a Toast message
            DropdownMenuItem(
                text = { Text(text = "Поменять картинку", fontSize = 20.sp) },
                onClick = {
//                Toast.makeText(context, "About", Toast.LENGTH_SHORT).show()
//                    Toast
//                        .makeText(context, "pos = $pos", Toast.LENGTH_LONG)
//                        .show()
                    mDisplayMenu = !mDisplayMenu
                    val permission: String = Manifest.permission.READ_EXTERNAL_STORAGE
                    val grant = ContextCompat.checkSelfPermission(context, permission)
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        val permission_list = arrayOfNulls<String>(1)
                        permission_list[0] = permission
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            permission_list,
                            1
                        )
                    }
//                    val intent = Intent()
//                        .setType("image/*")
//                        .setAction(Intent.ACTION_OPEN_DOCUMENT)
//                        .addCategory(Intent.CATEGORY_OPENABLE)
                    val intent = Intent(
                        Intent.ACTION_OPEN_DOCUMENT,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                        .apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                        }
//                    startForImage.launch(intent)
//                    launcher.launch("image/*")
                    launcher.launch(intent)
//                openDialog.value = true
                }
            )
        }
        Image(//нужен import androidx.compose.foundation.Image
//            painter = painterResource(id = model.picture), //указываем источник изображения
            //implementation "io.coil-kt:coil-compose:1.3.2"
            //import coil.compose.rememberImagePainter
//            painter = if (model.imageIsUri) rememberImagePainter(model.picture) else painterResource(id = model.picture.toInt()),
//            painter = if (pictureIsInt(model.picture)) {
//                painterResource(model.picture.toInt())
//            } else {
//                rememberImagePainter(model.picture)
//            },
            painter = if (pictureIsInt(model.picture)) painterResource(model.picture.toInt())
            else rememberImagePainter(data = Uri.parse(model.picture)),
            contentDescription = "",  //можно вставить описание изображения
            contentScale = ContentScale.Fit, //параметры масштабирования изображения
            modifier = Modifier.size(90.dp)
        )
    }
}
