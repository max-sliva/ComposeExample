package com.example.composeexample
//https://github.com/daniatitienei/DrawingCourse/blob/main/app/src/main/java/com/ad_coding/drawingcourse/MainActivity.kt
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.composeexample.ui.theme.ComposeExampleTheme

data class LineObj(
    val start: Offset,
    val end: Offset,
    val color: Color = Color.Black,
    val strokeWidth: Dp = 1.dp
)
class DrawingActivity3 : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val lines = remember {
                mutableStateListOf<LineObj>()
            }
            val drawingObjects = remember { mutableStateListOf("") }
            val buttonNames = arrayOf(
                stringResource(R.string.rect),
                stringResource(R.string.circle),
                stringResource(R.string.image)
            )
            val myView: MyGraphView? = MyGraphView(applicationContext)
            val viewRemember = remember {
                mutableStateOf(myView)
            }
//            val screenshotState = rememberScreenshotState()
            ComposeExampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

//                    val myView: MyGraphView? = MyGraphView(applicationContext)
                    Column(Modifier.fillMaxSize()) {
                        MakeTopButtonsOld(buttonNames, drawingObjects, viewRemember.value)
                        CustomViewOld(viewRemember.value)
//                        ScreenshotBox(screenshotState = screenshotState) {
//                        Canvas(
//                            modifier = Modifier.
//                            fillMaxSize()
//                            .pointerInput(true) { //добавляем обработчик касаний
//                                detectDragGestures { change, dragAmount ->
////                                    change.consume()
//                                    val line = Line(
//                                        start = change.position - dragAmount,
//                                        end = change.position
//                                    )
//                                    lines.add(line)
//                                }
//                            }
//                        )
//                        {
//                            val canvasQuadrantSize = size / 2F
//                            val canvasWidth = size.width
//                            println("canvasWidth = $canvasWidth")
//                            drawingObjects.forEach {
//                                if (it.contains(buttonNames[0])) drawRect(
//                                    color = Color.Magenta,
//                                    size = canvasQuadrantSize
//                                )
//                                if (it.contains(buttonNames[1]))
////                                    scale(scale = 2f) {
//                                        drawCircle(
//                                            Color.Red,
//                                            radius = canvasWidth / 4f
//                                        )
////                                    }
//                                if (it.contains(buttonNames[2])) {
//                                    try {
//                                        val mBitmapFromSdcard =
//                                            BitmapFactory.decodeFile("/mnt/sdcard/face.png")
//                                                .asImageBitmap()
//                                        drawImage(
//                                            image = mBitmapFromSdcard,
//                                            topLeft = Offset(x = 0f, y = 0f)
//                                        )
//
//                                    } catch (e: NullPointerException) {
//                                        Toast.makeText(applicationContext,"No image", Toast.LENGTH_LONG).show()
//                                    }
//                                }
//                            }
//                            lines.forEach { line ->
//                                drawLine(
//                                    color = line.color,
//                                    start = line.start,
//                                    end = line.end,
//                                    strokeWidth = line.strokeWidth.toPx(),
//                                    cap = StrokeCap.Round
//                                )
//                            }
//                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MakeTopButtonsOld(
    buttonNames: Array<String>,
    drawingObjects: SnapshotStateList<String>,
    myView: MyGraphView?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .border(BorderStroke(2.dp, Color.Blue))
    ) {
        buttonNames.forEach {
            Button(onClick = {
                drawingObjects.remove(it)
                drawingObjects.add(it)
                if (it==buttonNames[0]) myView?.drawSquare()
            }) {
                Text(text = it)
            }
        }
//        Button(onClick = {
//            drawingObjects.remove("Rect")
//            drawingObjects.add("Rect")
//            println("drawingObjects = ${drawingObjects.toList()}")
//        }) {
//            Text(text = "Rect")
//        }
//        Button(onClick = {
//            drawingObjects.remove("Circle")
//            drawingObjects.add("Circle")
//            println("drawingObjects = ${drawingObjects.toList()}")
//        }) {
//            Text(text = "Circle")
//        }
//        Button(onClick = {
//            drawingObjects.remove("Image")
//            drawingObjects.add("Image")
//            println("drawingObjects = ${drawingObjects.toList()}")
//        }) {
//            Text(text = "Image")
//        }
    }
}

@Composable
fun CustomViewOld(myView: MyGraphView?) {
    var selectedItem by remember { mutableStateOf(0) }

    // Adds view to Compose
    AndroidView(
        modifier = Modifier.fillMaxSize(), // Occupy the max size in the Compose UI tree
        factory = { context ->
            // Creates view
            myView!!
//            MyGraphView(context).apply {
//                // Sets up listeners for View -> Compose communication
////                setOnClickListener {
////                    selectedItem = 1
////                }
//            }
        },
//        update = { view ->
//            // View's been inflated or state read in this block has been updated
//            // Add logic here if necessary
//
//            // As selectedItem is read here, AndroidView will recompose
//            // whenever the state changes
//            // Example of Compose -> View communication
//            view.selectedItem = selectedItem
//        }
    )
}
