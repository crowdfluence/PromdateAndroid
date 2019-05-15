package com.example.logan.promdate.util

import android.graphics.*
import com.example.logan.promdate.R
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import java.util.*


//converts image to round image with camera icon overlayed
class SelectImageOverlayTransformation(
    private val radius: Int,
    private val margin: Int,
    private val borderColor: Int,
    private val context: Context
) : com.squareup.picasso.Transformation {

    override fun transform(source: Bitmap): Bitmap {

        val output = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        //fills in background
        val paint0 = Paint()
        paint0.color = Color.WHITE
        paint0.style = Paint.Style.FILL
        paint0.isAntiAlias = true
        canvas.drawCircle(
            (source.width.toFloat() - margin.toFloat()) / 2f,
            (source.height.toFloat() - margin.toFloat()) / 2f,
            radius.toFloat() - 2f,
            paint0
        )

        //draws image as circle
        val paint1 = Paint()
        paint1.isAntiAlias = true
        paint1.shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        canvas.drawCircle(
            (source.width.toFloat() - margin.toFloat()) / 2f,
            (source.height.toFloat() - margin.toFloat()) / 2f,
            radius.toFloat() - 2f,
            paint1
        )

        if (source != output) {
            source.recycle()
        }

        //adds gray border
        val paint2 = Paint()
        paint2.color = borderColor
        paint2.style = Paint.Style.STROKE
        paint2.isAntiAlias = true
        paint2.strokeWidth = 8f
        canvas.drawCircle(
            (source.width.toFloat() - margin.toFloat()) / 2f,
            (source.height.toFloat() - margin.toFloat()) / 2f,
            radius.toFloat() - 2f,
            paint2
        )



        //overlays dark over image
        val paint3 = Paint()
        paint3.apply {
            color = ContextCompat.getColor(context, R.color.darkeningOverlay)
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawCircle(
            (source.width.toFloat() - margin.toFloat()) / 2f,
            (source.height.toFloat() - margin.toFloat()) / 2f,
            radius.toFloat() - 2f,
            paint3
        )

        //adds camera in centre
        val drawable = context.getDrawable(R.drawable.ic_camera_white) ?: throw MissingResourceException("Missing camera icon drawable", "Drawable", "missing_icon")
        drawable.setBounds(radius / 2, radius / 2, 3 * radius / 2, 3 * radius / 2)
        drawable.draw(canvas)

        return output
    }

    override fun key(): String {
        return "rounded"
    }
}