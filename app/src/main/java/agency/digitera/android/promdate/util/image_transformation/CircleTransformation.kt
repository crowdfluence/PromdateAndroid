package agency.digitera.android.promdate.util.image_transformation

import android.graphics.*

//converts image into rounded image with border
class CircleTransformation(
    private val radius: Int,
    private val margin: Int,
    private val borderColor: Int
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

        return output
    }

    override fun key(): String {
        return "rounded"
    }
}