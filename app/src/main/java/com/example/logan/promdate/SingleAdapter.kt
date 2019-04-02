package com.example.logan.promdate

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_single.view.*

//adapter for the singles recyclerview
class SingleAdapter(private val singleList: ArrayList<User>,
                    private val clickListener: (User) -> Unit): RecyclerView.Adapter<SingleAdapter.SingleViewHolder>() {

    //sets content of view
    inner class SingleViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(user: User, clickListener: (User) -> Unit) = with(itemView) {
            name_text.text = context.getString(R.string.full_name, user.firstName, user.lastName)
            if (!user.profilePictureUrl.isEmpty()) {
                profile_picture_image.loadUrl(user.profilePictureUrl)
            }
            grade_text.text = context.getString(R.string.grade_number, user.grade)
            bio_text.text = user.bio
            setOnClickListener { clickListener(user) }
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
        holder.bind(singleList[position], clickListener)
    }

    //return size of dataset
    override fun getItemCount() = singleList.size

    //sets image from url
    fun ImageView.loadUrl(url: String) {
        Picasso.get()
            .load(url)
            .into(this)
    }
}