package agency.digitera.android.promdate.adapters

import agency.digitera.android.promdate.R
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import agency.digitera.android.promdate.data.User
import agency.digitera.android.promdate.util.LoadUrl
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.item_single.view.*

//Checks if list is updated
class SingleDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean = oldItem == newItem
}

//adapter for the singles recyclerview
class SingleAdapter(private val clickListener: (User) -> Unit) :
    PagedListAdapter<User, SingleAdapter.SingleViewHolder>(
        SingleDiffCallback()
    ) {

    //sets content of view
    inner class SingleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(user: User, clickListener: (User) -> Unit) {
            with(itemView) {
                name_text.text = context.getString(R.string.full_name, user.firstName, user.lastName)
                if (user.profilePictureUrl.isNotEmpty()) {
                    LoadUrl.loadProfilePicture(context, profile_picture_image, user.profilePictureUrl)
                }
                if (user.grade != null) {
                    grade_text.text = context.getString(R.string.grade_number, user.grade)
                    grade_text.visibility = View.VISIBLE
                }
                else {
                    grade_text.visibility = View.GONE
                }
                if (user.gender != null && user.gender != "Select Gender") {
                    gender_text.text = context.getString(R.string.gender, user.gender)
                    gender_text.visibility = View.VISIBLE

                    /*Sets the correct drawable depending on the user's gender */
                    if(user.gender == "Female")
                        circle_background.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pink_circle_outline))
                    else if(user.gender == "Male")
                        circle_background.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_blue_circle_outline))
                    else if(user.gender == "Other")
                        circle_background.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_orange_circle_outline))
                }
                else {
                    gender_text.visibility = View.GONE
                }
                /*Determines if the heart should be displayed*/
                if (user.isFavourited)
                    wishlist_heart.visibility = View.VISIBLE
                else
                    wishlist_heart.visibility = View.GONE


                bio_text.text = user.bio
                setOnClickListener { clickListener(user) }
            }
        }
    }

    //create new view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_single, parent, false) as View

        return SingleViewHolder(itemView)
    }

    //replace contents of view
    override fun onBindViewHolder(holder: SingleViewHolder, position: Int) {
        val user = getItem(position)

        if (user != null) {
            holder.bind(user, clickListener)
        }
    }
}