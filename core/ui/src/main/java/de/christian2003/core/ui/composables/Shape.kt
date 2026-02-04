package de.christian2003.core.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asComposePath
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath


/**
 * Displays a rounded polygon shape.
 *
 * @param shape     Shape.
 * @param color     Color.
 * @param modifier  Modifier.
 * @param content   Content for the shape.
 */
@Composable
fun Shape(
    shape: RoundedPolygon,
    color: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .drawWithCache() {
                val polygonPath = shape.toPath().asComposePath()
                val scaledPath = Path().apply {
                    addPath(polygonPath)
                    transform(
                        Matrix().apply {
                            scale(size.width, size.height)
                        }
                    )
                }
                onDrawBehind {
                    drawPath(scaledPath, color)
                }
            }
            .fillMaxSize()
    ) {
        content()
    }

}
