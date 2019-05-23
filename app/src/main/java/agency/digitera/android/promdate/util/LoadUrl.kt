package agency.digitera.android.promdate.util

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat
import agency.digitera.android.promdate.R
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

interface LoadUrl {
    companion object {
        //sets image from url & converts it to a circle
        fun loadUrl(context: Context, img: ImageView, url: String, transformType: Int = 0) {
            if (url == "") {
                return
            }
            val suffixUrl = if (url[0] == '.' && url[1] == '.') url.substring(2 until url.length) else url
            val fullUrl = "http://ec2-35-183-247-114.ca-central-1.compute.amazonaws.com$suffixUrl"
            Picasso.get()
                .load(fullUrl)
                .transform(
                    if (transformType == 0) {
                        CircleTransformation(
                            256,
                            1,
                            ContextCompat.getColor(context, R.color.lightGray)
                        )
                    }
                    else {
                        SelectImageOverlayTransformation(
                            256,
                            1,
                            ContextCompat.getColor(context, R.color.lightGray),
                            context
                        )
                    }
                )
                .resize(512, 512)
                .centerCrop()
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .placeholder(R.drawable.default_profile) //TODO: Change to loading animation
                .error(R.drawable.default_profile) //TODO: Change to actual error
                .into(img)
        }
    }
}