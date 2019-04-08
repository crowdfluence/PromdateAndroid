package com.example.logan.promdate

import android.graphics.*

//converts image into rounded image with border
class CircleTransformation(
    private val radius: Int,
    private val margin: Int,
    private val borderColor: Int) : com.squareup.picasso.Transformation {

    override fun transform(source: Bitmap): Bitmap {

        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        val output = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        canvas.drawCircle(
            (source.width.toFloat() - margin.toFloat()) / 2f,
            (source.height.toFloat() - margin.toFloat()) / 2f,
            radius.toFloat() - 2f,
            paint
        )

        if (source != output) {
            source.recycle()
        }

        val paint1 = Paint()
        paint1.color = borderColor
        paint1.style = Paint.Style.STROKE
        paint1.isAntiAlias = true
        paint1.strokeWidth = 2f
        canvas.drawCircle(
            (source.width.toFloat() - margin.toFloat()) / 2f,
            (source.height.toFloat() - margin.toFloat()) / 2f,
            radius.toFloat() - 2f,
            paint1
        )

        return output
    }

    override fun key(): String {
        return "rounded"
    }
}