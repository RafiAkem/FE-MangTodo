package com.example.fe_mangtodo.ui.components

import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.shape.GenericShape

fun CurvedBottomShape(): Shape = GenericShape { size, _ ->
    moveTo(0f, 0f)
    lineTo(0f, size.height * 0.5f)
    cubicTo(
        size.width * 0.25f, size.height,
        size.width * 0.75f, size.height,
        size.width, size.height * 0.5f
    )
    lineTo(size.width, 0f)
    close()
}
