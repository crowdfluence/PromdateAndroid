package com.example.logan.promdate

import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.logan.promdate.data.User
import com.example.logan.promdate.util.LoadUrl
import kotlinx.android.synthetic.main.item_single.view.*

//Checks if list is updated
class SingleDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean = oldItem == newItem
}

//adapter for the singles recyclerview
class SingleAdapter(private val clickListener: (User) -> Unit) :
    PagedListAdapter<User, SingleAdapter.SingleViewHolder>(SingleDiffCallback()) {

    //sets content of view
    inner class SingleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(user: User, clickListener: (User) -> Unit) {
            with(itemView) {
                name_text.text = context.getString(R.string.full_name, user.firstName, user.lastName)
                if (user.profilePictureUrl.isNotEmpty()) {
                    LoadUrl.loadUrl(context, profile_picture_image, user.profilePictureUrl)
                }
                grade_text.text = context.getString(R.string.grade_number, user.grade)
                bio_text.text = user.bio
                setOnClickListener { clickListener(user) }
            }
        }
    }

    //create new view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleAdapter.SingleViewHolder {
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