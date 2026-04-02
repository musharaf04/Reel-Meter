package com.example.scrollometer

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class ProgressImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var progressPercentage: Int = 0
    private val grayscalePaintFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })

    fun setProgress(percentage: Int) {
        progressPercentage = percentage.coerceIn(0, 100)
        invalidate() // This tells the view to redraw itself
    }

    // This is the completely rewritten drawing function
    override fun onDraw(canvas: Canvas) {
        // Get the drawable (the image). If there isn't one, do nothing.
        val drawable = drawable ?: return

        // Get the actual width and height of this ImageView on the screen.
        val viewWidth = width
        val viewHeight = height

        if (viewWidth <= 0 || viewHeight <= 0) {
            return
        }

        // This is the most important change: We explicitly tell the drawable
        // that it must fill the entire space of our ImageView.
        drawable.setBounds(0, 0, viewWidth, viewHeight)

        // 1. Draw the full, dimmed background image
        drawable.colorFilter = grayscalePaintFilter
        drawable.alpha = 100 // Make it quite dim
        drawable.draw(canvas)

        // 2. Draw the colorful progress part on top
        val progressHeight = viewHeight * (progressPercentage / 100f)
        if (progressHeight > 0) {
            canvas.save()
            // Create a clipping "window" that starts from the bottom of the view and goes up.
            canvas.clipRect(0f, viewHeight - progressHeight, viewWidth.toFloat(), viewHeight.toFloat())

            // Draw the full, colorful image again. It will only be visible inside the "window" we just created.
            drawable.colorFilter = null // Remove the grayscale filter
            drawable.alpha = 255       // Make it fully opaque
            drawable.draw(canvas)

            canvas.restore()
        }
    }
}