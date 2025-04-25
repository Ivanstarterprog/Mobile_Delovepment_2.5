package com.example.mobiledelovepment25

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var drawPath = Path()
    private var drawPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
    }
    private var canvasBitmap: Bitmap? = null
    private var canvasDraw: Canvas? = null
    private var lastX = 0f
    private var lastY = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvasDraw = canvasBitmap?.let { Canvas(it) }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvasBitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }
        canvas.drawPath(drawPath, drawPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                drawPath.moveTo(x, y)
                lastX = x
                lastY = y
            }

            MotionEvent.ACTION_MOVE -> {
                drawPath.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2)
                lastX = x
                lastY = y
            }

            MotionEvent.ACTION_UP -> {
                canvasDraw?.drawPath(drawPath, drawPaint)
                drawPath.reset()
            }
        }
        invalidate()
        return true
    }

    fun setColor(newColor: Int) {
        drawPaint.color = newColor
    }

    fun setBrushSize(newSize: Float) {
        drawPaint.strokeWidth = newSize
    }

    fun setBackgroundBitmap(bitmap: Bitmap) {
        canvasBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        canvasDraw = canvasBitmap?.let { Canvas(it) }
        invalidate()
    }

    fun getBitmap(): Bitmap? {
        return canvasBitmap
    }

    fun clearCanvas() {
        canvasBitmap?.eraseColor(Color.TRANSPARENT)
        invalidate()
    }
}