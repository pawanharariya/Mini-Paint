package com.example.minipaint

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import kotlin.math.abs

private const val STROKE_WIDTH = 12f
class CanvasView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    // Path representing the drawing so far
    private val drawing = ArrayList<DrawnPath>()

    // Path representing what is currently being drawn
    private var curPath = Path()

    // Set up the paint with which to draw.
    private var curPaint = getDefaultPaint()
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)
    private var drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f
    private var currentX = 0f
    private var currentY = 0f

    /**
     *
     * There is not need to draw every pixel and each time request a refresh of the display
     * Instead draw a path between points, for better performance
     * If the finger is barely moved or moved less than tolerance distance don't draw
     */
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private fun getDefaultPaint(): Paint {
        return Paint().apply {
            color = drawColor
            // Smooths out edges of what is drawn without affecting shape.
            isAntiAlias = true
            // Dithering affects how colors with higher-precision than the device are down-sampled.
            isDither = true
            style = Paint.Style.STROKE // default: FILL
            strokeJoin = Paint.Join.ROUND // default: MITER
            strokeCap = Paint.Cap.ROUND // default: BUTT
            strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // Draw the drawing so far
        for (coloredPath in drawing) {
            canvas?.drawPath(coloredPath.path, coloredPath.paint)
        }
        canvas?.drawPath(curPath, curPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

    private fun touchUp() {
        // Add the current path to the drawing so far
        drawing.add(DrawnPath(curPath, curPaint))
        // Rewind the current path and for the next touch
        curPath = Path()
        curPaint = getDefaultPaint()
    }

    private fun touchMove() {
        // Calculate the distance moved by the finger
        val dx = abs(motionTouchEventX - currentX)
        val dy = abs(motionTouchEventY - currentY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching control point (x1,y1), and ending at (x2,y2).
            curPath.quadTo(
                currentX,
                currentY,
                (motionTouchEventX + currentX) / 2,
                (motionTouchEventY + currentY) / 2
            )
            // Reset the end point to current point
            currentX = motionTouchEventX
            currentY = motionTouchEventY
        }
        invalidate()
    }

    private fun touchStart() {
        curPath.reset()
        curPath.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    public fun onPaintColorChanged(paintId: Int) {
        drawColor = ResourcesCompat.getColor(resources, paintId, null)
        curPaint.color = drawColor
    }
}

data class DrawnPath(val path: Path, val paint: Paint)
