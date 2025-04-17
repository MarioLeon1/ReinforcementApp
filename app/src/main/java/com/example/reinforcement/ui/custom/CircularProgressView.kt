package com.example.reinforcement.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.reinforcement.R

class CircularProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 40f
        color = ContextCompat.getColor(context, R.color.task_uncompleted)
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 40f
        color = ContextCompat.getColor(context, R.color.primary)
    }

    private val rectF = RectF()

    private var progress: Float = 0f

    fun setProgress(progress: Float) {
        this.progress = progress.coerceIn(0f, 1f)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val diameter = minOf(width, height) - backgroundPaint.strokeWidth

        // Calcular la posición del círculo
        val left = (width - diameter) / 2
        val top = (height - diameter) / 2
        val right = left + diameter
        val bottom = top + diameter

        rectF.set(left, top, right, bottom)

        // Dibujar el círculo de fondo
        canvas.drawCircle(width / 2, height / 2, diameter / 2, backgroundPaint)

        // Dibujar el arco de progreso
        val sweepAngle = 360 * progress
        canvas.drawArc(rectF, -90f, sweepAngle, false, progressPaint)
    }
}