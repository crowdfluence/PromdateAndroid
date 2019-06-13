package agency.digitera.android.promdate.adapters

import agency.digitera.android.promdate.R
import agency.digitera.android.promdate.data.Couple
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import agency.digitera.android.promdate.util.LoadUrl
import kotlinx.android.synthetic.main.item_couple.view.*

//Checks if list is updated
class CoupleDiffCallback : DiffUtil.ItemCallback<Couple>() {
    override fun areItemsTheSame(oldItem: Couple, newItem: Couple): Boolean {
        return oldItem.user1.id == newItem.user1.id && oldItem.user2.id == newItem.user2.id
    }

    override fun areContentsTheSame(oldItem: Couple, newItem: Couple): Boolean = oldItem == newItem
}

//adapter for the couples recycler view
class CoupleAdapter(private val clickListener: (Couple) -> Unit) :
    PagedListAdapter<Couple, CoupleAdapter.CoupleViewHolder>(
        CoupleDiffCallback()
    ) {

    //sets content of view
    inner class CoupleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(couple: Couple, clickListener: (Couple) -> Unit) = with(itemView) {
            names_text.text = context.getString(
                R.string.couple_name,
                couple.user1.firstName,
                couple.user1.lastName[0],
                couple.user2.firstName,
                couple.user2.lastName[0]
            )
            if (couple.user1.profilePictureUrl.isNotEmpty()) {
                LoadUrl.loadProfilePicture(context, profile_picture_1_image, couple.user1.profilePictureUrl)
            }
            if (couple.user2.profilePictureUrl.isNotEmpty()) {
                LoadUrl.loadProfilePicture(context, profile_picture_2_image, couple.user2.profilePictureUrl)
            }
            //TODO: Set prom location
            setOnClickListener { clickListener(couple) }
        }
    }

    //create new view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoupleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_couple, parent, false) as View

        return CoupleViewHolder(itemView)
    }

    //replace contents of view
    override fun onBindViewHolder(holder: CoupleViewHolder, position: Int) {
        val couple = getItem(position)

        if (couple != null) {
            holder.bind(couple, clickListener)
        }
    }
}