package agency.digitera.android.promdate.adapters

import agency.digitera.android.promdate.R
import agency.digitera.android.promdate.data.INSTAGRAM
import agency.digitera.android.promdate.data.SNAPCHAT
import agency.digitera.android.promdate.data.TWITTER
import agency.digitera.android.promdate.data.UserSocial
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_social.view.*

class SocialAdapter(
    private val list: List<UserSocial>?
) : RecyclerView.Adapter<SocialAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_social, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(userSocial = list?.get(position))
    }


    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(userSocial: UserSocial?) {
            userSocial?.let {
                when (userSocial.socialMedia) {
                    INSTAGRAM -> {
                        view.social_image.setImageDrawable(
                            ContextCompat.getDrawable(
                                view.context,
                                R.drawable.instagram_logo
                            )
                        )
                        view.social_edit.setText(it.nameSocial)
                    }
                    SNAPCHAT -> {
                        view.social_image.setImageDrawable(
                            ContextCompat.getDrawable(
                                view.context,
                                R.drawable.snapchat_logo
                            )
                        )
                        view.social_edit.setCompoundDrawables(null, null, null, null)
                        view.social_edit.setText(it.nameSocial)
                    }
                    TWITTER -> {
                        view.social_image.setImageDrawable(
                            ContextCompat.getDrawable(
                                view.context,
                                R.drawable.twitter_logo
                            )
                        )
                        view.social_edit.setText(it.nameSocial)
                    }
                }
            }
        }
    }
}