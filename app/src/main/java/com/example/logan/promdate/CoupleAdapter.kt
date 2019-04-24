package com.example.logan.promdate

import androidx.paging.PagedListAdapter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.logan.promdate.data.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_couple.view.*

//Checks if list is updated
class CoupleDiffCallback : DiffUtil.ItemCallback<List<User>>() {
    override fun areItemsTheSame(oldItem: List<User>, newItem: List<User>): Boolean {
        return oldItem[0].id == newItem[0].id && oldItem[1].id == newItem[1].id
    }

    override fun areContentsTheSame(oldItem: List<User>, newItem: List<User>): Boolean = oldItem == newItem
}

//adapter for the couples recycler view
class CoupleAdapter(private val clickListener: (List<User>) -> Unit) :
    PagedListAdapter<List<User>, CoupleAdapter.CoupleViewHolder>(CoupleDiffCallback()) {

    //sets content of view
    inner class CoupleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(couple: List<User>, clickListener: (List<User>) -> Unit) = with(itemView) {
            names_text.text = context.getString(
                R.string.couple_name,
                couple[0].firstName,
                couple[0].lastName[0],
                couple[1].firstName,
                couple[1].lastName[0]
            )
            if (couple[0].profilePictureUrl.isNotEmpty()) {
                profile_picture_1_image.loadUrl(couple[0].profilePictureUrl)
            }
            if (couple[1].profilePictureUrl.isNotEmpty()) {
                profile_picture_2_image.loadUrl(couple[1].profilePictureUrl)
            }
            //TODO: Set prom location
            setOnClickListener { clickListener(couple) }
        }
    }

    //create new view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoupleAdapter.CoupleViewHolder {
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

    //sets image from url & converts it to a circle
    fun ImageView.loadUrl(url: String) {
        val fullUrl = "http://ec2-35-183-247-114.ca-central-1.compute.amazonaws.com${url.substring(2 until url.length)}"
        Picasso.get()
            .load(fullUrl)
            .transform(CircleTransformation(40, 1, ContextCompat.getColor(context, R.color.lightGray)))
            .resize(80, 80)
            .centerCrop()
            .placeholder(R.drawable.default_profile) //TODO: Change to loading animation
            .error(R.drawable.default_profile) //TODO: Change to actual error
            .into(this)
    }
}