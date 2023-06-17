package com.example.composeexample

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.composeexample.ui.theme.ComposeExampleTheme

class DrawingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val drawingObjects = remember { mutableStateListOf("") }
            val buttonNames = arrayOf(
                stringResource(R.string.rect),
                stringResource(R.string.circle),
                stringResource(R.string.image)
            )
            ComposeExampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(Modifier.fillMaxSize()) {
                        MakeTopButtons(buttonNames, drawingObjects)
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val canvasQuadrantSize = size / 2F
                            val canvasWidth = size.width
                            println("canvasWidth = $canvasWidth")
                            drawingObjects.forEach {
                                if (it.contains(buttonNames[0])) drawRect(
                                    color = Color.Magenta,
                                    size = canvasQuadrantSize
                                )
                                if (it.contains(buttonNames[1]))
//                                    scale(scale = 2f) {
                                        drawCircle(
                                            Color.Red,
                                            radius = canvasWidth / 4f
                                        )
//                                    }
                                if (it.contains(buttonNames[2])) {
                                    try {
                                        val mBitmapFromSdcard =
                                            BitmapFactory.decodeFile("/mnt/sdcard/face.png")
                                                .asImageBitmap()
                                        drawImage(
                                            image = mBitmapFromSdcard,
                                            topLeft = Offset(x = 0f, y = 0f)
                                        )

                                    } catch (e: NullPointerException) {
                                        Toast.makeText(
                                            applicationContext,
                                            "No image",
                                            Toast.LENGTH_LONG
                                        )
                                            .show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MakeTopButtons(buttonNames: Array<String>, drawingObjects: SnapshotStateList<String>) {
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
