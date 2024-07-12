package com.example.greensignal.presentation.ui.element

import android.graphics.Picture
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.greensignal.R

@Composable
fun DrawingField(picture: Picture, isSaveVisible: Boolean = false, save: () -> Unit) {
    val tempPath = remember { Path() }
    val path = remember { mutableStateOf(Path()) }

    fun clearCanvas() {
        tempPath.reset()
        path.value = Path()
    }

    Canvas(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .height(200.dp)
            .fillMaxWidth()
            .drawWithCache {
                val width = this.size.width.toInt()
                val height = this.size.height.toInt()
                onDrawWithContent {
                    val pictureCanvas =
                        Canvas(
                            picture.beginRecording(
                                width,
                                height
                            )
                        )
                    draw(this, this.layoutDirection, pictureCanvas, this.size) {
                        this@onDrawWithContent.drawContent()
                    }
                    picture.endRecording()

                    drawIntoCanvas { canvas -> canvas.nativeCanvas.drawPicture(picture) }
                }
            }
            .pointerInput(true) {
                detectDragGestures { change, dragAmount ->
                    if (change.position.x in 20f..(size.width.toFloat() - 20f) && change.position.y in 20f..(size.height.toFloat() - 20f)) {
                        tempPath.moveTo(
                            change.position.x - dragAmount.x,
                            change.position.y - dragAmount.y
                        )
                        tempPath.lineTo(
                            change.position.x,
                            change.position.y
                        )

                        path.value = Path().apply {
                            addPath(tempPath)
                        }
                    }
                }
            }
    ) {
        drawPath(
            path.value,
            color = Color.Blue,
            style = Stroke(20f, cap = StrokeCap.Round)
        )
    }


    Spacer(modifier = Modifier.height(10.dp))
    Row {
        if(isSaveVisible) {
            Button(
                onClick = { save() },
                modifier = Modifier
            ) {
                Text(stringResource(id = R.string.save))
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        Button(
            onClick = { clearCanvas() },
            modifier = Modifier
        ) {
            Text(text = "Очистить")
        }
    }
}