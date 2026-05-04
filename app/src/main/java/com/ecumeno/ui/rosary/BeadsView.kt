package com.ecumeno.ui.rosary

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.ecumeno.R
import kotlin.math.roundToInt

class BeadsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var currentBigCircleIndex = 5
    private val circleCount = 40
    private var animator: ValueAnimator? = null
    private var currentOffset = 0f

    private val bigRadius = 80f
    private val smallRadius = 40f
    private val circleSpacing = 40f

    private val step: Float
        get() = bigRadius * 2 + circleSpacing

    fun setBigCircleIndex(targetIndex: Int) {
        if (targetIndex !in 0..circleCount) return
        animator?.cancel()
        startAnimationTo(targetIndex)
    }

    private fun startAnimationTo(targetIndex: Int) {
        val startOffset = currentOffset
        val targetOffset = -targetIndex * step

        animator = ValueAnimator.ofFloat(startOffset, targetOffset).apply {
            duration = 300
            interpolator = DecelerateInterpolator()

            addUpdateListener { animation ->
                currentOffset = animation.animatedValue as Float
                invalidate()
            }

            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    currentBigCircleIndex = (-currentOffset / step).roundToInt().coerceIn(0, circleCount)
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {}
            })

            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerY = height / 2f
        val screenCenterX = width / 2f

        val offset = screenCenterX + currentOffset

        val animatedBigIndex = if (animator?.isRunning == true) {
            (-currentOffset / step).roundToInt().coerceIn(0, circleCount)
        } else {
            currentBigCircleIndex
        }

        for (i in 0..circleCount) {
            val centerX = i * step + offset
            val isBig = (i == animatedBigIndex)

            if (centerX + bigRadius < 0 || centerX - bigRadius > width) {
                continue
            }

            val alpha = calculateAlpha(centerX)

            drawCircle(canvas, centerX, centerY, isBig, alpha)
        }

        paint.alpha = 255
    }

    private fun calculateAlpha(centerX: Float): Float {
        return when {
            centerX < 100 -> (centerX / 100).coerceIn(0.2f, 1f)
            centerX > width - 100 -> ((width - centerX) / 100).coerceIn(0.2f, 1f)
            else -> 1f
        }
    }

    private fun drawCircle(canvas: Canvas, centerX: Float, centerY: Float, isBig: Boolean, alpha: Float) {
        val radius = if (isBig) bigRadius else smallRadius

        paint.color = if (isBig) context.getColor(R.color.accent) else context.getColor(R.color.not_available)
        paint.alpha = (255 * alpha).toInt()
        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, radius, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = (step * 3).toInt()
        val width = resolveSize(desiredWidth, widthMeasureSpec)

        val desiredHeight = (bigRadius * 3).toInt()
        val height = resolveSize(desiredHeight, heightMeasureSpec)

        setMeasuredDimension(width, height)

        if (currentOffset == 0f) {
            currentOffset = -currentBigCircleIndex * step
        }
    }
}