package com.example.composeexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.composeexample.ui.theme.ComposeExampleTheme

class DrawingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val drawingObject = remember { mutableStateListOf("") }
            ComposeExampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(Modifier.fillMaxSize()) {
                        MakeTopButtons(drawingObject)
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val canvasQuadrantSize = size / 2F
                            //todo сделать обход drawingObject.forEach, и смотреть какой это объект, и его рисовать
                            if (drawingObject.contains("Rect")) drawRect(
                                color = Color.Magenta,
                                size = canvasQuadrantSize
                            )
                            if (drawingObject.contains("Circle")) drawCircle(Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MakeTopButtons(drawingObject: SnapshotStateList<String>) {
    Row {
        Button(onClick = { drawingObject.add("Rect") }) {
            Text(text = "Rect")

        }
        Button(onClick = { drawingObject.add("Circle") }) {
            Text(text = "Circle")

        }
    }}
