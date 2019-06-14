package agency.digitera.android.promdate.util

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat
import agency.digitera.android.promdate.R
import android.util.Log
import com.squareup.picasso.Picasso


interface LoadUrl {
    companion object {
        //sets image from url & converts it to a circle
        fun loadProfilePicture(context: Context, img: ImageView, url: String, transformType: Int = 0) {
            if (url == "") {
                img.setImageDrawable(context.getDrawable(R.drawable.default_profile))
                return
            }

            val fullUrl = "http://ec2-35-183-247-114.ca-central-1.compute.amazonaws.com/$url"

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
                .placeholder(R.drawable.default_profile)
                .error(R.drawable.default_profile)
                .into(img)
        }

        fun loadDressPicture(context: Context, img: ImageView, url: String) {
            if (url == "") {
                img.setImageDrawable(context.getDrawable(R.drawable.dress_picture))
                return
            }

            val fullUrl = "http://ec2-35-183-247-114.ca-central-1.compute.amazonaws.com/$url"
            Picasso.get()
                .load(fullUrl)
                .transform(
                    CircleTransformation(
                        256,
                        1,
                        ContextCompat.getColor(context, R.color.lightGray)
                    )
                )
                .resize(512, 512)
                .centerCrop()
                .placeholder(R.drawable.dress_picture)
                .error(R.drawable.dress_picture)
                .into(img)
        }
    }
}