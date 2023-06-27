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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.composeexample.ui.theme.ComposeExampleTheme
import kotlin.reflect.KFunction0

//data class Line(
//    val start: Offset,
//    val end: Offset,
//    val color: Color = Color.Black,
//    val strokeWidth: Dp = 1.dp
//)
class DrawingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val buttonNames = arrayOf(
                stringResource(R.string.rect),
                stringResource(R.string.circle),
                stringResource(R.string.image),
                stringResource(R.string.save)
            )
            val myView: MyGraphView? = MyGraphView(applicationContext)
            val viewRemember = remember {
                mutableStateOf(myView)
            }
            ComposeExampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(Modifier.fillMaxSize()) {
                        MakeTopButtons(buttonNames, viewRemember.value)
                        CustomView(viewRemember.value)
                    }
                }
            }
        }
    }
}

@Composable
fun MakeTopButtons(buttonNames: Array<String>, myView: MyGraphView?) {
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
                myView!!.funcArray[buttonNames.lastIndexOf(it)]()
//                if (it==buttonNames[0]) myView?.drawSquare()
//                if (it==buttonNames[1]) myView?.drawCircle()
//                if (it==buttonNames[2]) myView?.drawFace()
//                if (it==buttonNames[3]) myView?.onSaveClick()
            }) {
                Text(text = it)
            }
        }
    }
}

@Composable
fun CustomView(myView: MyGraphView?) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            myView!!
        },
    )
}
