package com.huann305.library

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView

@RequiresApi(Build.VERSION_CODES.O)
class BorderedTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private val borderPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var customTypeface: Typeface? = null
    private var gradient: LinearGradient? = null

    private var gradientStartColor: Int = Color.parseColor("#943DFF")
    private var gradientCenterColor: Int = Color.parseColor("#E642FF")
    private var gradientEndColor: Int = Color.parseColor("#E642FF")
    private var borderColor: Int = Color.WHITE
    private var borderWidth: Float = 5f

    init {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.BorderedTextView,
            0, 0
        )

        try {
            val fontResId = typedArray.getResourceId(R.styleable.BorderedTextView_customFont, -1)
            if (fontResId != -1) {
                customTypeface = resources.getFont(fontResId)
            }

            gradientStartColor = typedArray.getColor(R.styleable.BorderedTextView_gradientStartColor, gradientStartColor)
            gradientCenterColor = typedArray.getColor(R.styleable.BorderedTextView_gradientCenterColor, gradientCenterColor)
            gradientEndColor = typedArray.getColor(R.styleable.BorderedTextView_gradientEndColor, gradientEndColor)
            borderColor = typedArray.getColor(R.styleable.BorderedTextView_borderColor, Color.WHITE)
            borderWidth = typedArray.getDimension(R.styleable.BorderedTextView_borderWidth, 5f)

        } finally {
            typedArray.recycle()
        }

        // Cài đặt paint ban đầu
        initPaints()
    }

    // Cập nhật lại kích thước chữ khi thay đổi textSize
    private fun initPaints() {
        borderPaint.apply {
            style = Paint.Style.STROKE
            color = borderColor // Màu viền
            strokeWidth = borderWidth // Độ dày viền
            textSize = this@BorderedTextView.textSize // Lấy textSize từ TextView
            typeface = customTypeface // Áp dụng font cho border
        }

        textPaint.apply {
            textSize = this@BorderedTextView.textSize // Lấy textSize từ TextView
            style = Paint.Style.FILL
            typeface = customTypeface // Áp dụng font cho chữ
            updateGradient(width.toFloat()) // Tạo gradient
        }
    }

    // Tạo LinearGradient cho textPaint
    private fun updateGradient(width: Float) {
        gradient = LinearGradient(
            0f, 0f, width, 0f,
            intArrayOf(gradientStartColor, gradientCenterColor, gradientEndColor), // Các màu của gradient từ XML
            null, // Phân bổ màu đồng đều
            Shader.TileMode.CLAMP
        )
        textPaint.shader = gradient
    }

    // Phương thức này được gọi khi kích thước TextView thay đổi
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initPaints() // Cập nhật lại Paints khi kích thước thay đổi
        updateGradient(w.toFloat()) // Cập nhật gradient khi kích thước thay đổi
    }

    // Cập nhật lại kích thước chữ nếu textSize được thay đổi thủ công
    override fun setTextSize(size: Float) {
        super.setTextSize(size)
        initPaints() // Cập nhật lại Paints với kích thước mới
        invalidate() // Vẽ lại view
    }

    override fun onDraw(canvas: Canvas) {
        // Vẽ chữ
        val text = text.toString()
        val x = (width / 2).toFloat() - (textPaint.measureText(text) / 2)
        val y = (height / 2).toFloat() - ((textPaint.descent() + textPaint.ascent()) / 2)

        // Vẽ viền trước
        canvas.drawText(text, x, y, borderPaint)

        // Vẽ chữ với gradient sau
        canvas.drawText(text, x, y, textPaint)
    }
}
