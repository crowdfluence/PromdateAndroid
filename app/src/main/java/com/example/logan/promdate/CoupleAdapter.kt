package com.example.logan.promdate

import android.arch.paging.PagedListAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.logan.promdate.data.Couple
import com.example.logan.promdate.data.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_couple.view.*
import kotlinx.android.synthetic.main.item_single.view.*

//Checks if list is updated
class CoupleDiffCallback : DiffUtil.ItemCallback<Couple>() {
    override fun areItemsTheSame(oldItem: Couple, newItem: Couple): Boolean {
        return oldItem.user1.id == newItem.user1.id && oldItem.user2.id == newItem.user2.id
    }

    override fun areContentsTheSame(oldItem: Couple, newItem: Couple): Boolean = oldItem == newItem
}

//adapter for the couples recycler view
class CoupleAdapter(private val clickListener: (Couple) -> Unit) :
    PagedListAdapter<Couple, CoupleAdapter.CoupleViewHolder>(CoupleDiffCallback()) {

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
            if (!couple.user1.profilePictureUrl.isEmpty()) {
                profile_picture_1_image.loadUrl(couple.user1.profilePictureUrl)
            }
            if (!couple.user2.profilePictureUrl.isEmpty()) {
                profile_picture_2_image.loadUrl(couple.user2.profilePictureUrl)
            }
            location_text
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
            .error(R.drawable.promdate_logo) //TODO: Change to actual error
            .into(this)
    }
}